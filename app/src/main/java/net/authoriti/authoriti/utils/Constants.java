package net.authoriti.authoriti.utils;

/**
 * Created by mac on 11/30/17.
 */

public interface Constants {
    String DEV = "davjnabB8rvyZZLrMOq3jw==:JDJXMckcsxcMhtMi1QVmEs4DpAFtLn9FMZwASUiH9B4=:qeU5Uk2rFKcoMtK0N1XYosOSfhIar0K8Hgk8rIn4BcEdVmyDMOnYBJtEg4P6hZXg";
    String QA = "16/AtQ1ZYsw2x48wBUNVrA==:EO1rTvhdVo8jfjClM1zcc9Cqrl2c4CugEaJCaoMK+78=:Cao10YznfpfHZxvJLzkzFyU5dhlfaIzubXBh6b6wBsg=";
    String PROD = "ylTU0o9L9ldUKosJwH7a4Q==:Lr1DDV3P4FgAYkAl4osdKT+w1F8pW/y5bWAuoY4Qedk=:OnJLNvBmGZyX9XyfZ8OIjoP5R2Br9SnyI3d6TAK5lo8=";
    String API_BASE_URL = QA;
    String API_BASE_URL_POLLING =
            "https://s3.amazonaws.com/authoriti-requests-qa/requests/";
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
