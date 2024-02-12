package com.allybros.superego.api;

import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of changing password
 *
 * @author orcunkamiloglu
 */
public class PasswordChangeTask extends ApiTask<ApiStatusResponse> {
    /**
     * Password change request to API
     *
     * @param sessionToken     required to verify the user
     * @param oldPassword      required to verify the user
     * @param newPassword      required to change old password
     * @param newPasswordAgain required to change old password
     */
    public PasswordChangeTask(String sessionToken, String oldPassword, String newPassword, String newPasswordAgain) {
        super(Request.Method.POST, ConstantValues.PASSWORD_CHANGE, ApiStatusResponse.class);
        setParam("session-token", sessionToken);
        setParam("old-password", oldPassword);
        setParam("new-password", newPassword);
        setParam("new-password-again", newPasswordAgain);
    }
}
