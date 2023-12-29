package com.allybros.superego.unit;


import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int userType, rated, credit;
    private String image,testId, testResultId,username,userBio,email, avatarName;
    private List<TraitScore> scores;
    private Ocean ocean;
    private Personality personality;
    private Bitmap avatar;      //Görsel öğeler için kullanılıyor.

    public User() {
    }

    public User(int userType, int rated, int credit, String image, String testId, String testResultId, String username, String userBio, String email, ArrayList<TraitScore> scores, Ocean ocean, Personality personality) {
        this.userType = userType;
        this.rated = rated;
        this.credit = credit;
        this.image = image;
        if (testId.equals("null")) this.testId = null;
        else this.testId = testId;
        if (testResultId.equals("null")) this.testResultId = null;
        else this.testResultId = testResultId;
        this.username = username;
        if (userBio.equals("null")) this.userBio = null;
        else this.userBio = userBio;
        this.email = email;
        this.scores = scores;
        this.personality = personality;
        this.ocean = ocean;
    }

    public User(String testId, String username, String userBio, String avatarName) {
        if (testId.equals("null")) this.testId = null;
        else this.testId = testId;
        this.username = username;
        if (userBio.equals("null")) this.userBio = null;
        else this.userBio = userBio;
        this.avatarName = avatarName;
    }

    public int getUserType() {
        return userType;
    }

    public int getRated() {
        return rated;
    }

    public int getCredit() {
        return credit;
    }

    public String getImage() {
        return image;
    }

    public String getTestId() {
        return testId;
    }

    public String getTestResultId() {
        return testResultId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserBio() {
        return userBio;
    }

    public String getEmail() {
        return email;
    }

    public List<TraitScore> getScores() {
        return scores;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setRated(int rated) {
        this.rated = rated;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public void setTestResultId(String testResultId) {
        this.testResultId = testResultId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setScores(List<TraitScore> traitScores) {
        this.scores = traitScores;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public boolean hasTest(){
        return this.getTestId() != null;
    }

    public boolean hasResults(){
        return this.getScores().size() > 0;
    }
    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
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
}
