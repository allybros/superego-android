package com.allybros.superego.unit;

public class ConstantValues {

    //Const variables
    public static final String RECAPTCHA_SKIP = "d860b038b50ddc9c137ce15fdc93b738";
    public static final String ADMOB_ADD_INTERFACE_ID = "ca-app-pub-3940256099942544/5224354917";//TODO: ally bros-->ca-app-pub-5360761814312438/1782285846
    public static final int RATED_LIMIT = 10;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";  //Shared preferences Tag
    public static final int MAX_FILE_SIZE = 33177600;

    //Links

    //    private static final String API_ROOT =  "https://api.insightof.me";
    public static final String API_ROOT =  "https://api.allybros.com/superego/";//TODO: Yukarıda bulunan api roota geçilecek
    public static final String AVATAR_URL = API_ROOT + "images.php?get=";
    public static final String TEST_LINK_ROOT = "https://demo.allybros.com/superego/rate.php?test=";


    //API LINKS
    public static final String UPDATE_INFORMATION ="https://api.allybros.com/superego/edit-profile.php";
    public static final String URL_EARN_REWARD = "https://api.allybros.com/superego/reward.php";
    public static final String URL_SOCIAL_ACCOUNTS_LOGIN ="https://api.allybros.com/superego/oauth-client.php";
    public static final String LOGIN_URL ="https://api.allybros.com/superego/login.php";
    public static final String ADD_TEST_URL = "https://demo.allybros.com/superego/create.php";
    public static final String UPLOAD_IMAGE = "https://api.allybros.com/superego/upload.php";
    public static final String ALL_TRAITS = "https://api.allybros.com/superego/traits.php";
    public static final String LOAD_PROFILE ="https://api.allybros.com/superego/load-profile.php";
    public static final String REGISTER ="https://api.allybros.com/superego/register.php";
    public static final String CREATE_TEST = "https://demo.allybros.com/superego/create.php";
    public static final String SEARCH_URL = "https://api.allybros.com/superego/search.php?q=";
    public static final String PARAM_TEST = "https://api.allybros.com/superego/param_test.php";
    public static final String PASSWORD_CHANGE = "https://api.allybros.com/superego/reset-password.php";
    public static final String RATE_URL = "https://demo.allybros.com/superego/rate.php?embed=true&test=";

    //Broadcast-Reciever actions
    public static final String ACTION_UPDATE_INFORMATION ="update-info-status-share";
    public static final String ACTION_UPDATE_IMAGE="update-image-status-share";
    public static final String ACTION_EARNED_REWARD="earned-reward-status-share";
    public static final String ACTION_LOGIN = "login-status-share";
    public static final String ACTION_SOCIAL_MEDIA_LOGIN = "social-media-login-status-share";
    public static final String ACTION_REFRESH_PROFILE = "profile-refresh-status-share";
    public static final String ACTION_REGISTER = "register-status-share";
    public static final String ACTION_REFRESH_RESULTS = "refresh-results";
    public static final String ACTION_CREATE_TEST = "create_test";
    public static final String ACTION_SEARCH = "result-query";
    public static final String ACTION_PASSWORD_CHANGE = "password-change-status-share";
}
