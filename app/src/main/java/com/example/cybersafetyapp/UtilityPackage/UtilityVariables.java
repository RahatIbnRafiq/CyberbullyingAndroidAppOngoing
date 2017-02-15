package com.example.cybersafetyapp.UtilityPackage;

/**
 * Created by RahatIbnRafiq on 11/16/2016.
 */

public class UtilityVariables {


    public static boolean isAlarmOn = false;

    public static final String tag = "cybersafetyapp";

    public static final String INSTAGRAM_AUTH_URL = "https://api.instagram.com/oauth/authorize/";


    //websta.me client
    //public static final String INSTAGRAM_CLIENT_ID = "9d836570317f4c18bca0db6d2ac38e29";
    //public static final String INSTAGRAM_CLIENT_SECRET = "pbnfE3ntLr00nuZk3qX0w5m3avmhEUO7";
    //public static final String INSTAGRAM_CALLBACK_URL = "http://websta.me/callback";



    public static final String INSTAGRAM_CLIENT_ID = "362741ea25924668af07edfb3873e3a2";
    //public static final String INSTAGRAM_CLIENT_SECRET = "ed14022584a2494690a6d9da21f7ee6e";
    public static final String INSTAGRAM_CALLBACK_URL = "http://localhost";

    //public static final String VINE_URL_USER_SEARCH = "https://api.vineapp.com/search/users/";
    //public static final String VINE_URL_USER_DETAIL = "https://api.vineapp.com/users/profiles/";
    //public static final String VINE_URL_USER_TIMELINE = "https://api.vineapp.com/timelines/users/";
   // public static final String VINE_URL_POST_COMMENTS = "https://api.vineapp.com/posts/";


    public static final String ROOT_URL = "http://192.168.0.12:3000";
    public static final String LOGIN_GUARDIAN = ROOT_URL+"/api/guardian/login";
    public static final String REGISTER_GUARDIAN = ROOT_URL+"/api/guardian/register";
    public static final String INSTAGRAM_GET_ACCESS_TOKEN  = ROOT_URL+"/api/guardian/instagramAuthToken";




    public static final int VALID_EMAIL_LENGTH = 3;
    public static final int VALID_PASSWORD_LENGTH = 3;
    public static final int VALID_PHONE_LENGTH =10;
    public static final String VALID = "valid";


    public static final int NO_OF_TO_BE_MONITORED_USERS = 2;


}
