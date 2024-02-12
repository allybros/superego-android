package com.allybros.superego.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orcunkamiloglu on 12.02.2024
 */

public class RegisterResponse extends ApiStatusResponse {
    @SerializedName("session_token")
    private String sessionToken;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

}
