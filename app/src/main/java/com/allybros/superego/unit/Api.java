package com.allybros.superego.unit;

public class Api {

    private static final String API_ROOT =  "https://api.insightof.me";
    private static final String RECAPTCHA_SKIP="d860b038b50ddc9c137ce15fdc93b738";

    public static String getAPI_ROOT() {
        return API_ROOT;
    }

    public static String getRECAPTCHA_SKIP() {
        return RECAPTCHA_SKIP;
    }
}
