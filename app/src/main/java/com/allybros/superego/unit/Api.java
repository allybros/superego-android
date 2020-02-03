package com.allybros.superego.unit;

public class Api {

    private static final String API_ROOT =  "https://api.insightof.me";
    private static final String RECAPTCHA_SKIP= "d860b038b50ddc9c137ce15fdc93b738";
    private static final String TEST_LINK_ROOT= "https://demo.allybros.com/superego/rate.php?test=";
    private static final int RATED_LIMIT=10;

    public static int getRatedLimit() {
        return RATED_LIMIT;
    }

    public static String getTestLinkRoot() {
        return TEST_LINK_ROOT;
    }

    public static String getAPI_ROOT() {
        return API_ROOT;
    }

    public static String getRECAPTCHA_SKIP() {
        return RECAPTCHA_SKIP;
    }
}
