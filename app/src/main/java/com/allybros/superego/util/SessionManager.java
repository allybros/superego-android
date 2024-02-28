package com.allybros.superego.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.allybros.superego.unit.User;

import static com.allybros.superego.unit.ConstantValues.USER_INFORMATION_PREF;

/**
 * SessionManager class keeps sessionToken, userId, password variables
 * and determines the ways to reach, use and change them.
 *
 * @author orcunkamiloglu
 */

public class SessionManager {

    private static SessionManager instance;
    private User user;                      // User who logged in
    private String sessionToken, password, userId;
    private boolean modified = false;

    private SessionManager() {
    }

    /**
     * Reads userId, password and sessionToken from local storage and update SessionManager
     *
     * @param context required to use SharedPreferences functions
     */
    public void readInfo(Context context) {
        SharedPreferences pref = context.getSharedPreferences(USER_INFORMATION_PREF, context.MODE_PRIVATE);

        SessionManager.getInstance().setSessionToken(pref.getString("session_token", ""));
        SessionManager.getInstance().setUserId(pref.getString("uid", ""));
        SessionManager.getInstance().setPassword(pref.getString("password", ""));
    }

    /**
     * Writes userId, password and sessionToken to local storage
     *
     * @param uid          required to verify the user
     * @param password     required to verify the user
     * @param sessionToken required to verify the user
     * @param context      required to use SharedPreferences functions
     */
    public void writeInfoLocalStorage(String uid, String password, String sessionToken, Context context) {
        SharedPreferences pref = context.getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uid", uid);
        editor.putString("password", password);
        editor.putString("session_token", sessionToken);
        editor.commit();
    }

    /**
     * Delete variables from local storage
     *
     * @param context
     */
    public void clearSession(Context context) {
        // Clear fields
        this.user = null;
        this.userId = null;
        this.password = null;
        this.sessionToken = null;
        // Clear data from device
        SharedPreferences pref = context.getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();
    }

    /**
     * Change variables in local storage
     *
     * @param newUid         required to verify the user
     * @param newEmail       required to verify the user
     * @param newInformation required to verify the user
     */
    public void updateLocalVariables(String newUid, String newEmail, String newInformation) {
        user.setUsername(newUid);
        user.setUserBio(newInformation);
        user.setEmail(newEmail);
    }

    /**
     * Change credentials in local storage
     *
     * @param newPassword required to verify the user
     * @param context     required to use Session Manager
     */
    public void updateCredentials(String newPassword, Context context) {
        writeInfoLocalStorage(SessionManager.getInstance().getUser().getUsername(), newPassword,
                SessionManager.getInstance().getSessionToken(), context);
        readInfo(context);
    }

    public User getUser() {
        this.modified = false;
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isModified() {
        return modified;
    }

    public void touchSession() {
        this.modified = true;
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }


}
