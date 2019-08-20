package com.allybros.superego.unit;

import java.util.ArrayList;

public class User {

    private static int userType;
    private static String username,userBio,email;
    private static ArrayList<Trait> scores;
    private static int rated;

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

    public static ArrayList<Trait> getScores() {
        return scores;
    }

    public static void setScores(ArrayList<Trait> scores) {
        User.scores = scores;
    }


    public User(int userType, String username, String userBio, String email, ArrayList<Trait> scores,int rated) {
        this.userType = userType;
        this.username = username;
        this.userBio = userBio;
        this.email = email;
        this.scores = scores;
        this.rated=rated;
    }
}
