package com.allybros.superego.unit;

import java.util.Locale;

public class ConstantValues {

    //Const variables
    public static final String USER_INFORMATION_PREF = "USER_INFORMATION_PREF";  //Shared preferences Tag
    public static final String IS_SHOWN = "IS_SHOWN";
    public static final String COOKIE_SESSION_TOKEN = "sego_session_token";
    public static final int MAX_FILE_SIZE = 209715200;
    public final String locale = Locale.getDefault().getLanguage();
    //Links
    public static final String API_ROOT = "https://dev.allybros.com/superego-api/";
    public static final String WEB_URL = "https://dev.allybros.com/superego-web/";
    public static final String TWEMOJI_URL = "https://dev.allybros.com/twemoji";
    //API LINKS
    public static final String UPDATE_INFORMATION = API_ROOT + "edit-profile.php";
    public static final String URL_EARN_REWARD = API_ROOT + "reward.php";
    public static final String URL_SOCIAL_ACCOUNTS_LOGIN = API_ROOT +  "oauth-client.php";
    public static final String LOGIN_URL = API_ROOT +  "login.php";
    public static final String UPLOAD_IMAGE = API_ROOT +  "upload.php";
    public static final String LOAD_PROFILE = API_ROOT + "load-profile.php";
    public static final String REGISTER = API_ROOT + "register.php";
    public static final String SEARCH_URL = API_ROOT + "search.php?q=";
    public static final String PASSWORD_CHANGE = API_ROOT + "reset-password.php";
    public static final String CREATE_TEST = "/new-test?embed=1";
    public static final String RATE_URL = "/rate.php?embed=true&test=";
    public static final String LOGOUT_URL = API_ROOT + "logout.php";
    public static final String ALERTS_URL = API_ROOT + "alerts.php";
    public static final String EMOJI_END_POINT = TWEMOJI_URL + "?name=";

    public String getWebUrlWithLocale(String url) {
        return WEB_URL + locale + url;
    }
}
