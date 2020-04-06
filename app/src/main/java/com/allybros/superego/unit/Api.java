package com.allybros.superego.unit;

public class Api {

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
    private static final String UPDATE_INFORMATION ="https://api.allybros.com/superego/edit-profile.php";



    //Broadcast-Reciever actions
    private static final String ACTION_UPDATE_INFORMATION ="update-info-status-share";
    private static final String ACTION_UPDATE_IMAGE="update-image-status-share";


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
