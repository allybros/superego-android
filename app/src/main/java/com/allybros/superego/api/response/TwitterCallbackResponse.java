package com.allybros.superego.api.response;

import com.google.gson.annotations.SerializedName;

public class TwitterCallbackResponse {
    private Integer status;
    private Boolean created;
    @SerializedName("session_token")
    private String sessionToken;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
