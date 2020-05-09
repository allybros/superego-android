package com.allybros.superego.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.User;

/**
 * SessionManager class keeps sessionToken, userId, password variables
 * and determines the ways to reach, use and change them.
 * @author 0rcun
 */



public class SessionManager {

    private static SessionManager instance;
    private User user;                      // User who logged in
    private String sessionToken, password, userId;

    private SessionManager(){}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void readInfo(Context context){
        /**
         * Reads userId, password and sessionToken from local storage and update SessionManager
         * @param context required to use SharedPreferences functions
         *
         */
        SharedPreferences pref = context.getSharedPreferences(ConstantValues.getUserInformationPref(), context.MODE_PRIVATE);

        SessionManager.getInstance().setSessionToken(pref.getString("session_token", ""));
        SessionManager.getInstance().setUserId(pref.getString("uid",""));
        SessionManager.getInstance().setPassword(pref.getString("password",""));
    }

    public void writeInfoLocalStorage(String uid, String password, String session_token,Context context){
        /**
         * Writes userId, password and sessionToken to local storage
         * @param uid           required to verify the user
         * @param password      required to verify the user
         * @param session_token required to verify the user
         * @param context       required to use SharedPreferences functions
         *
         */
        SharedPreferences pref = context.getSharedPreferences(ConstantValues.getUserInformationPref(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uid",uid);
        editor.putString("password",password);
        editor.putString("session_token", session_token);
        editor.commit();
    }

    public User getUser() {
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
}
