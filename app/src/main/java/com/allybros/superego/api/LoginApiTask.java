package com.allybros.superego.api;

import com.allybros.superego.api.response.LoginResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

public class LoginApiTask extends ApiTask<LoginResponse> {

    public LoginApiTask(String uid, String password,
                           String gRecaptchaResponse, String gRecaptchaClientKey) {
        super(Request.Method.POST, ConstantValues.LOGIN_URL, LoginResponse.class);
        setParam("uid", uid);
        setParam("password", password);
        setParam("g-recaptcha-response", gRecaptchaResponse);
        setParam("g-recaptcha-site-key", gRecaptchaClientKey);
    }

}
