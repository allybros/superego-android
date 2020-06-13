package com.allybros.superego.unit;

public class ConstantValues {

    //Const variables
    public static final String RECAPTCHA_SKIP = "d860b038b50ddc9c137ce15fdc93b738";
    public static final String ADMOB_ADD_INTERFACE_ID = "ca-app-pub-3940256099942544/5224354917";//TODO: ally bros-->ca-app-pub-5360761814312438/1782285846
    public static final int RATED_LIMIT = 10;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";  //Shared preferences Tag
    public static final int MAX_FILE_SIZE = 33177600;

    //Links

    public static final String API_ROOT =  "https://api.insightof.me/";
    public static final String AVATAR_URL = API_ROOT + "images.php?get=";
    public static final String TEST_LINK_ROOT = "https://insightof.me/";


    //API LINKS
    public static final String UPDATE_INFORMATION = API_ROOT + "edit-profile.php";
    public static final String URL_EARN_REWARD = API_ROOT + "reward.php";
    public static final String URL_SOCIAL_ACCOUNTS_LOGIN = API_ROOT +  "oauth-client.php";
    public static final String LOGIN_URL = API_ROOT +  "login.php";
    public static final String UPLOAD_IMAGE = API_ROOT +  "upload.php";
    public static final String ALL_TRAITS = API_ROOT +  "traits.php";
    public static final String LOAD_PROFILE = API_ROOT + "load-profile.php";
    public static final String REGISTER = API_ROOT + "register.php";
    public static final String SEARCH_URL = API_ROOT +  "search.php?q=";
    public static final String PARAM_TEST = API_ROOT +  "param_test.php";
    public static final String PASSWORD_CHANGE = API_ROOT +  "reset-password.php";
    public static final String CREATE_TEST = "https://insightof.me/create.php";
    public static final String RATE_URL = "https://insightof.me/rate.php?embed=true&test=";
    public static final String LOGOUT_URL = API_ROOT + "logout.php";

    //Broadcast-Reciever actions
    public static final String ACTION_UPDATE_INFORMATION ="update-info-status-share";
    public static final String ACTION_UPDATE_IMAGE="update-image-status-share";
    public static final String ACTION_EARNED_REWARD="earned-reward-status-share";
    public static final String ACTION_LOGIN = "login-status-share";
    public static final String ACTION_LOGOUT = "logout-status-share";
    public static final String ACTION_SOCIAL_MEDIA_LOGIN = "social-media-login-status-share";
    public static final String ACTION_REFRESH_PROFILE = "profile-refresh-status-share";
    public static final String ACTION_REGISTER = "register-status-share";
    public static final String ACTION_REFRESH_RESULTS = "refresh-results";
    public static final String ACTION_CREATE_TEST = "create_test";
    public static final String ACTION_SEARCH = "result-query";
    public static final String ACTION_PASSWORD_CHANGE = "password-change-status-share";
}
