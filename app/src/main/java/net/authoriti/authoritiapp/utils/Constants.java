package net.authoriti.authoritiapp.utils;

/**
 * Created by mac on 11/30/17.
 */

public interface Constants {

    String API_BASE_URL = "ga1YBx5IFYSUOrnyed2XIw==:+WP0nDUps/9zc/sXhAAGjsjNdHGsePro3H6Ema2z+9s=:uKc+QJFeTOe03JonkBacwycP7rPVIIg+DzG3CwVEDP8=";
    String API_BASE_URL_POLLING =
            "https://s3.amazonaws.com/authoriti-requests-prod/requests/";
    String HELP_BASE = "https://help.authoriti.net";


//    String API_BASE_URL = "https://authoriti-test.crts.io";
//    String API_BASE_URL = "https://authoriti-qa.crts.io";

    // BroadCast Id
    String BROADCAST_CHANGE_MENU = "BROADCAST_CHANGE_MENU";
    String BROADCAST_ADD_BUTTON_CLICKED = "BROADCAST_ADD_BUTTON_CLICKED";
    String BROADCAST_SYNC_BUTTON_CLICKED = "BROADCAST_SYNC_BUTTON_CLICKED";

    // Menu Id
    long MENU_CODE = 1001;
    long MENU_ACCOUNT = 1002;
    long MENU_WIPE = 1003;
    long MENU_LOGOUT = 1004;
    long MENU_POLLING = 1005;

    String MENU_ID = "menu_id";

    // Picker Identification
    String PICKER_ACCOUNT = "accountId";
    String PICKER_INDUSTRY = "industry";
    String PICKER_LOCATION_COUNTRY = "location_country";
    String PICKER_LOCATION_STATE = "location_state";
    String PICKER_TIME = "time";
    String PICKER_GEO = "geo";
    String PICKER_REQUEST = "requestor";
    String PICKER_DATA_TYPE = "data_type";
    String PICKER_DATA_INPUT_TYPE = "input";

    String TIME_15_MINS = "15 Mins";
    String TIME_1_HOUR = "1 Hour";
    String TIME_4_HOURS = "4 Hours";
    String TIME_1_DAY = "1 Day";
    String TIME_1_WEEK = "1 Week";
    String TIME_CUSTOM_TIME = "Custom Time";
    String TIME_CUSTOM_DATE = "Custom Date/Time";

    String ACUANT_LICENSE_KEY = "FAD9B2F0E7F1";

    String TOPIC_PURPOSE_MANAGE_MY_AC = "help-phone-inquiry";
    String TOPIC_PURPOSE_SEND_MONEY = "help-send-cash";
    String TOPIC_PURPOSE_MOVE_MONEY = "help-move-money";
    String TOPIC_PURPOSE_OPEN_NEW_AC = "help-open-new-account";
    String TOPIC_PURPOSE_EQUIDITY_TRADE = "help-equity-trade";
    String TOPIC_PURPOSE_SHARE_PERSONAL_DATA = "help-share-my-data";
    String TOPIC_PURPOSE_INSURENCE_CLAIM = "help-insurance-claim";
    String TOPIC_PURPOSE_TEX_RETURN = "help-file-taxes";
    String TOPIC_PURPOSE_ESCROW = "help-escrow-disbursement";

    String TOPIC_PICKER_ACCOUNT_ID = "help-picker-account-id";
    String TOPIC_PICKER_TIME = "help-picker-time";
    String TOPIC_PICKER_INDUSTRY = "help-picker-industry";
    String TOPIC_PICKER_LOCATION = "help-picker-location";
    String TOPIC_PICKER_GEO = "help-picker-geo";
    String TOPIC_PICKER_DATA_TYPE = "help-picker-data-type";
    String TOPIC_PICKER_REQUESTOR = "help-picker-requestor";

    String TOPIC_INVITE = "help-invite-code";
    String TOPIC_DLV = "help-dlv";
    String TOPIC_GENERAL = "help-general";
    String TOPIC_ACCOUNT_2018 = "help-account-id";
    String TOPIC_PASSWORD = "TOPIC_PASSWORD";
    String TOPIC_RESET_PASSWORD = "TOPIC_RESET_PASSWORD";
    String TOPIC_CHASE = "help-account-id";
    String TOPIC_ABOUT = "TOPIC_ABOUT";
    String TOPIC_PURPOSE_DETAIL = "TOPIC_PURPOSE_DETAIL";
    String TOPIC_PURPOSE_DETAIL_PICKER = "TOPIC_PURPOSE_DETAIL_PICKER";
}
