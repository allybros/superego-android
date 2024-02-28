package com.allybros.superego.api.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends ApiStatusResponse {
    @SerializedName("session_token")
    private String sessionToken;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

}
