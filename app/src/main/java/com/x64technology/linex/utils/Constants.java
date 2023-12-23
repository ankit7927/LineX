package com.x64technology.linex.utils;

public class Constants {

    public static final String BASE_URL = "http://192.168.43.30:3000";
    public static final String SIGNUP_URL = BASE_URL+"/auth/signup";
    public static final String SIGNIN_URL = BASE_URL+"/auth/signin";

    // preference string
    public static final String USER_PREFERENCE = "user_preference";
    public static final String APP_PREFERENCE = "app_preference";
    public static final String STR_EMAIL = "email";
    public static final String STR_NAME = "name";
    public static final String STR_USERID = "userid";
    public static final String STR_DPLINK = "dplink";
    public static final String STR_TOKEN = "token";
    public static final String STR_UNKNOWN = "unknown";
    public static final String STR_ACTIVE_USER = "active_user";



    // database string

    public static final String ID = "id";
    public static final String RECEIVER = "receiver";
    public static final String SENDER = "sender";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String IS_MINE = "is_mine";


    public static final String CONTACT_TABLE_NAME = "contact";
    public static final String CONTACT_ID = "contact_id";

    public static final String CONTACT_NAME = "name";
    public static final String CONTACT_USERNAME = "username";
    public static final String CONTACT_USER_ID = "user_id";
    public static final String CONTACT_DP_IMAGE = "dp_image";
    public static final String CONTACT_REQUEST_TYPE = "request_type";


    // types of request
    public static final String REQUEST_RECEIVED = "received";
    public static final String REQUEST_SENT = "sent";
    public static final String REQUEST_ACCEPTED = "accepted";
    public static final String REQUEST_REJECTED = "rejected";



    // query strings
    public static final String CONTACT_TABLE_QUERY = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            CONTACT_TABLE_NAME, CONTACT_ID, CONTACT_NAME, CONTACT_USER_ID, CONTACT_DP_IMAGE, CONTACT_REQUEST_TYPE
    );

    public static final String DROP_CONTACT_TABLE_QUERY = String.format(
            "DROP TABLE IF EXISTS %s", CONTACT_TABLE_NAME
    );

    public static final String MESSAGE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER)";
    public static final String EVENT_CONTACT_REQUEST = "contact request";
    public static final String EVENT_REQUEST_ACCEPTED = "request accepted";
    public static final String EVENT_REQUEST_REJECTED = "request rejected";
    public static final String EVENT_REQUEST_CANCELED = "request canceled";
    public static final String EVENT_MESSAGE = "event message";
}
