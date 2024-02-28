package com.allybros.superego.api;

import com.allybros.superego.api.response.RegisterResponse;
import com.allybros.superego.unit.ConstantValues;
import com.android.volley.Request;

/**
 * Class includes the function of registering user
 *
 * @author orcunkamiloglu
 */
public class RegisterTask extends ApiTask<RegisterResponse> {
    /**
     * The function sends a request to register with information and then receives response.
     *
     * @param username  required to register user
     * @param email     required to register user
     * @param password  required to register user
     * @param agreement required to register user
     */
    public RegisterTask(
            String username,
            String email,
            String password,
            Boolean agreement,
            String recaptchaResponse,
            String recaptchaClientKey
    ) {
        super(Request.Method.POST, ConstantValues.REGISTER, RegisterResponse.class);
        setParam("username", username);
        setParam("email", email);
        setParam("password", password);
        setParam("conditions", String.valueOf(agreement));
        setParam("g-recaptcha-response", recaptchaResponse);
        setParam("g-recaptcha-site-key", recaptchaClientKey);
    }
}


