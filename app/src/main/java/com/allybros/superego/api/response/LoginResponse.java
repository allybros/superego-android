package com.allybros.superego.api.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private int status;
    private String message;
    @SerializedName("session_token")
    private String sessionToken;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

}
