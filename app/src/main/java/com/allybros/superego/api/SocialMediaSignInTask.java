package com.allybros.superego.api;

import com.allybros.superego.api.response.ApiStatusResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of Sign in
 *
 * @author orcunkamiloglu
 */
public class SocialMediaSignInTask extends ApiTask<ApiStatusResponse> {
    /**
     * Function requests to server and then receives response.
     *
     * @param accessToken   required for verify user
     * @param authenticator required to select a way for sign in
     */
    public SocialMediaSignInTask(final String accessToken, final String authenticator) {
        super(Request.Method.POST, ConstantValues.URL_SOCIAL_ACCOUNTS_LOGIN, ApiStatusResponse.class);
        // set params
        setParam("authenticator", authenticator);
        setParam("access_token", accessToken);
    }
}
