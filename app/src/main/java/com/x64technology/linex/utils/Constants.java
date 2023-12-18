package com.x64technology.linex.utils;

public class Constants {

    // preference string
    public static final String USER_PREFERENCE = "user_preference";
    public static final String APP_PREFERENCE = "app_preference";
    public static final String STR_USERNAME = "username";
    public static final String STR_NAME = "name";
    public static final String STR_USERID = "userid";
    public static final String STR_DPLINK = "dplink";
    public static final String STR_TOKEN = "token";
    public static final String STR_UNKNOWN = "unknown";
    public static final String STR_CURRENT_CHAT = "current_chat";



    // database string
    public static final int DB_VERSION = 1;
    public static final String  DB_NAME = "LineXDB";

    public static final String ID = "id";
    public static final String SENDER_ID = "sender_id";
    public static final String SENDER_USERNAME = "sender_username";
    public static final String CONTENT = "content";
    public static final String TIME = "time";


    public static final String CONTACT_TABLE_NAME = "contact";
    public static final String CONTACT_ID = "contact_id";

    public static final String CONTACT_NAME = "name";
    public static final String CONTACT_USERNAME = "username";
    public static final String CONTACT_USER_ID = "user_id";
    public static final String CONTACT_DP_IMAGE_LINK = "dp_image_link";
    public static final String CONTACT_REQUEST_TYPE = "request_type";


    // types of request
    public static final String REQUEST_RECEIVED = "received";
    public static final String REQUEST_SENT = "sent";
    public static final String REQUEST_ACCEPTED = "accepted";
    public static final String REQUEST_REJECTED = "rejected";



    // query strings
    public static final String CONTACT_TABLE_QUERY = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
            CONTACT_TABLE_NAME, CONTACT_ID, CONTACT_NAME,CONTACT_USERNAME, CONTACT_USER_ID, CONTACT_DP_IMAGE_LINK, CONTACT_REQUEST_TYPE
    );

    public static final String DROP_CONTACT_TABLE_QUERY = String.format(
            "DROP TABLE IF EXISTS %s", CONTACT_TABLE_NAME
    );
}
