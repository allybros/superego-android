package com.allybros.superego.api.response;

import com.allybros.superego.unit.Ocean;
import com.allybros.superego.unit.Personality;
import com.allybros.superego.unit.TraitScore;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileResponse {
    private int status;
    @SerializedName("user_type")
    private int userType;
    private String username;
    @SerializedName("user_bio")
    private String userBio;
    @SerializedName("test_id")
    private String testId;
    @SerializedName("test_result_id")
    private String testResultId;
    private String avatar;
    private String email;
    private int credit;
    private int rated;
    private Ocean ocean;
    private Personality personality;
    private List<TraitScore> scores = new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

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

    public String getTestResultId() {
        return testResultId;
    }

    public void setTestResultId(String testResultId) {
        this.testResultId = testResultId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getRated() {
        return rated;
    }

    public void setRated(int rated) {
        this.rated = rated;
    }

    public Ocean getOcean() {
        return ocean;
    }

    public void setOcean(Ocean ocean) {
        this.ocean = ocean;
    }

    public Personality getPersonality() {
        return personality;
    }

    public void setPersonality(Personality personality) {
        this.personality = personality;
    }

    public List<TraitScore> getScores() {
        return scores;
    }

    public void setScores(List<TraitScore> scores) {
        this.scores = scores;
    }
}
