package com.allybros.superego.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by orcun on 27.02.2024
 */
public class SearchResponse {
    private String username;
    @SerializedName("user_bio")
    private String userBio;
    @SerializedName("test_id")
    private String testId;
    private String avatar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
