package com.allybros.superego.api;

import com.allybros.superego.api.response.TwitterCallbackResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

public class TwitterCallbackTask extends ApiTask<TwitterCallbackResponse> {

    public TwitterCallbackTask(String code, String challenge) {
        super(Request.Method.POST, ConstantValues.URL_SOCIAL_ACCOUNTS_LOGIN, TwitterCallbackResponse.class);
        // set params
        setParam("authenticator", "twitter");
        setParam("code", code);
        setParam("challenge", challenge);
    }

}
