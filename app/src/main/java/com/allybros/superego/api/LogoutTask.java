package com.allybros.superego.api;


import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of signing out function
 *
 * @author orcunkamiloglu
 */
public class LogoutTask extends ApiTask<ApiStatusResponse> {
    /**
     * Function requests to server and then receives response
     *
     * @param sessionToken required for verify user
     */
    public LogoutTask(String sessionToken) {
        super(Request.Method.POST, ConstantValues.LOGOUT_URL, ApiStatusResponse.class);
        setParam("session-token", sessionToken);
    }
}
