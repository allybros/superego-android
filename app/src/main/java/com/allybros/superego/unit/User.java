package com.allybros.superego.unit;


import android.graphics.Bitmap;

import java.util.ArrayList;

public class User {

    private int userType, rated, credit;
    private String image,testId,username,userBio,email, avatarName;
    private ArrayList<Score> scores;
    private Bitmap avatar;      //Görsel öğeler için kullanılıyor.

    public User(int userType, int rated, int credit, String image, String testId, String username, String userBio, String email, ArrayList<Score> scores) {
        this.userType = userType;
        this.rated = rated;
        this.credit = credit;
        this.image = image;
        this.testId = testId;
        this.username = username;
        if (userBio.equals("null")) this.userBio = null;
        else this.userBio = userBio;
        this.email = email;
        this.scores = scores;
    }

    public User(String testId, String username, String userBio, String avatarName) {
        this.testId = testId;
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

    public String getUsername() {
        return username;
    }

    public String getUserBio() {
        return userBio;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Score> getScores() {
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setScores(ArrayList<Score> scores) {
        this.scores = scores;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }
}
