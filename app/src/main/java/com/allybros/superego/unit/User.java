package com.allybros.superego.unit;


import android.graphics.Bitmap;

import java.util.ArrayList;

public class User {

    private static int userType, rated, credit;
    private static String image,testId,username,userBio,email;
    private static ArrayList<Score> scores;
    private static ArrayList<Trait> test;
    private static Bitmap avatar;

    public static Bitmap getAvatar() {
        return avatar;
    }

    public static void setAvatar(Bitmap avatar) {
        User.avatar = avatar;
    }

    public static int getCredit() {
        return credit;
    }

    public static void setCredit(int credit) {
        User.credit = credit;
    }

    public static String getTestId() {
        return testId;
    }

    public static void setTestId(String testId) {
        User.testId = testId;
    }

    public static int getRated() {
        return rated;
    }

    public static void setRated(int rated) {
        User.rated = rated;
    }

    public static int getUserType() {
        return userType;
    }

    public static void setUserType(int userType) {
        User.userType = userType;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String getUserBio() {
        return userBio;
    }

    public static void setUserBio(String userBio) {
        User.userBio = userBio;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static ArrayList<Score> getScores() {
        return scores;
    }

    public static void setScores(ArrayList<Score> scores) {
        User.scores = scores;
    }

    public static String getImage() {
        return image;
    }

    public static ArrayList<Trait> getTest() {
        return test;
    }

    public static void setTest(ArrayList<Trait> test) {
        User.test = test;
    }

    public static void setImage(String image) {
        User.image = image;
    }

    public User(int userType, String username, String userBio, String email, ArrayList<Score> scores, int rated) {
        this.userType = userType;
        this.username = username;
        this.userBio = userBio;
        this.email = email;
        this.scores = scores;
        this.rated=rated;
    }
}
