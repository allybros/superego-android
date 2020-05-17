package com.allybros.superego.unit;

public class ConstantValues {

    //Const variables
    private static final String RECAPTCHA_SKIP = "d860b038b50ddc9c137ce15fdc93b738";
    private static final String ADMOB_ADD_INTERFACE_ID = "ca-app-pub-3940256099942544/5224354917";//TODO: ally bros-->ca-app-pub-5360761814312438/1782285846
    private static final int RATED_LIMIT = 10;
    private static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";  //Shared preferences Tag

    //Links

    //    private static final String API_ROOT =  "https://api.insightof.me";
    private static final String API_ROOT =  "https://api.allybros.com/superego/";//TODO: Yukarıda bulunan api roota geçilecek
    private static final String AVATAR_URL = API_ROOT + "images.php?get=";
    private static final String TEST_LINK_ROOT = "https://demo.allybros.com/superego/rate.php?test=";   //TODO: Test links must be like AVATAR_URL variable


    //API LINKS
    private static final String UPDATE_INFORMATION ="https://api.allybros.com/superego/edit-profile.php";
    private static final String URL_EARN_REWARD = "https://api.allybros.com/superego/reward.php";
    private static final String URL_SOCIAL_ACCOUNTS_LOGIN ="https://api.allybros.com/superego/oauth-client.php";
    private static final String LOGIN_URL ="https://api.allybros.com/superego/login.php";
    private static final String ADD_TEST_URL = "https://demo.allybros.com/superego/create.php";
    private static final String UPLOAD_IMAGE = "https://api.allybros.com/superego/upload.php";
    private static final String ALL_TRAITS = "https://api.allybros.com/superego/traits.php";
    private static final String LOAD_PROFILE ="https://api.allybros.com/superego/load-profile.php";
    private static final String REGISTER ="https://api.allybros.com/superego/register.php";
    private static final String CREATE_TEST = "https://demo.allybros.com/superego/create.php";
    private static final String SEARCH_URL = "https://api.allybros.com/superego/search.php?q=";
    private static final String PARAM_TEST = "https://api.allybros.com/superego/param_test.php";


    //Broadcast-Reciever actions
    private static final String ACTION_UPDATE_INFORMATION ="update-info-status-share";
    private static final String ACTION_UPDATE_IMAGE="update-image-status-share";
    private static final String ACTION_EARNED_REWARD="earned-reward-status-share";
    private static final String ACTION_LOGIN = "login-status-share";
    private static final String ACTION_SOCIAL_MEDIA_LOGIN = "social-media-login-status-share";
    private static final String ACTION_REFRESH_PROFILE = "profile-refresh-status-share";
    private static final String ACTION_REGISTER = "register-status-share";
    private static final String ACTION_REFRESH_RESULTS = "refresh-results";
    private static final String ACTION_CREATE_TEST = "create_test";
    private static final String ACTION_SEARCH = "result-query";

    public static String getActionSearch() {
        return ACTION_SEARCH;
    }

    public static String getSearchUrl() {
        return SEARCH_URL;
    }

    public static String getParamTest() {
        return PARAM_TEST;
    }

    public static String getActionRefreshResults() {
        return ACTION_REFRESH_RESULTS;
    }

    public static String getActionCreateTest() {
        return ACTION_CREATE_TEST;
    }

    public static String getCreateTest() {
        return CREATE_TEST;
    }

    public static String getActionRegister() {
        return ACTION_REGISTER;
    }

    public static String getAllTraits() {
        return ALL_TRAITS;
    }

    public static String getLoadProfile() {
        return LOAD_PROFILE;
    }

    public static String getAddTestUrl() {
        return ADD_TEST_URL;
    }

    public static String getUploadImage() {
        return UPLOAD_IMAGE;
    }

    public static String getREGISTER() {
        return REGISTER;
    }

    public static String getActionRefreshProfile() {
        return ACTION_REFRESH_PROFILE;
    }

    public static String getActionLogin() {
        return ACTION_LOGIN;
    }

    public static String getLoginUrl() {
        return LOGIN_URL;
    }

    public static String getActionSocialMediaLogin() {
        return ACTION_SOCIAL_MEDIA_LOGIN;
    }

    public static String getActionEarnedReward() {
        return ACTION_EARNED_REWARD;
    }

    public static String getUrlSocialAccountsLogin() {
        return URL_SOCIAL_ACCOUNTS_LOGIN;
    }

    public static String getUrlEarnReward() {
        return URL_EARN_REWARD;
    }

    public static String getActionUpdateImage() {
        return ACTION_UPDATE_IMAGE;
    }

    public static String getActionUpdateInformation() {
        return ACTION_UPDATE_INFORMATION;
    }

    public static String getUpdateInformation() {
        return UPDATE_INFORMATION;
    }

    public static String getAvatarUrl() {
        return AVATAR_URL;
    }

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

    public static String getAdmobAddInterfaceId() {
        return ADMOB_ADD_INTERFACE_ID;
    }

    public static String getUserInformationPref() {
        return USER_INFORMATION_PREF;
    }
}
