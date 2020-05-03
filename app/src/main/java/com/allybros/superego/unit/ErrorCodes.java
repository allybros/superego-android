package com.allybros.superego.unit;

public class ErrorCodes {
    public static final int SUCCESS=10;

    //User input errors
    public static final int USERNAME_EMPTY=20;
    public static final int USERNAME_NOT_LEGAL=21;
    public static final int USERNAME_ALREADY_EXIST=22;
    public static final int EMAIL_EMPTY=23;
    public static final int EMAIL_NOT_LEGAL=24;
    public static final int EMAIL_ALREADY_EXIST=25;
    public static final int PASSWORD_EMPTY=26;
    public static final int PASSWORD_NOT_LEGAL=27;
    public static final int ALREADY_RATED=28;
    public static final int PASSWORD_MISMATCH=29;

    //Session errors
    public static final int UNAUTHORIZED=30;
    public static final int SESSION_EXPIRED=31;
    public static final int SUSPEND_SESSION=32;

    //User related operation errors
    public static final int USER_NOT_FOUND=40;

    //Captcha errors
    public static final int INVALID_CAPTCHA=70;
    public static final int CONDITIONS_NOT_ACCEPTED=71;
    public static final int CAPTCHA_REQUIRED=72;

    //File errors
    public static final int INVALID_FILE_EXTENSION= 80;
    public static final int INVALID_FILE_SIZE= 81;
    public static final int INVALID_FILE_TYPE= 82;
    public static final int FILE_WRITE_ERROR=83;

    //General failure
    public static final int SYSFAIL=0;

    //Only android-side Error Code
    public static final int CONNECTION_ERROR=2016;
}

