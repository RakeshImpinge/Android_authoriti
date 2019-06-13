package net.authoriti.authoriti.utils;

/**
 * Created by mac on 11/30/17.
 */

public interface Constants {
    String DEV = "davjnabB8rvyZZLrMOq3jw==:JDJXMckcsxcMhtMi1QVmEs4DpAFtLn9FMZwASUiH9B4=:qeU5Uk2rFKcoMtK0N1XYosOSfhIar0K8Hgk8rIn4BcEdVmyDMOnYBJtEg4P6hZXg";
    String QA = "WSNTO7K0zcMl0pE/kL8L4g==:91JmJhXx2OQPfLr/0aCesZktWi72LMccgjw0zLzkIJg=:J28cEDRsdvjgcFVrtRdE6dUjc5VZUDKPKIKe+UoUm1Y=";
    String PROD = "OUCfUyCJLsvrtuZ+HWMTTA==:Z7psrKA4pogc9O1ZjtGatTPDwpoq6T8hb599+VLZzr0=:SIkVX1tZ6KKTjszqQB0cd/GE/0pqD5OMdpqvvQlNLTA=";
    String API_BASE_URL = DEV;

    String HELP_BASE = "https://help.authoriti.net";
    String IDENTIFIER = "authoriti";

    // Auto logout user if no interaction with app
    int INACTIVITY_TIME_OUT = 60;


    // BroadCast Id
    String BROADCAST_CHANGE_MENU = "BROADCAST_CHANGE_MENU";
    String BROADCAST_ADD_BUTTON_CLICKED = "BROADCAST_ADD_BUTTON_CLICKED";
    String BROADCAST_CLOUD_BUTTON_CLICKED = "BROADCAST_ADD_CLOUD_CLICKED";
    String BROADCAST_SYNC_DONE = "BROADCAST_SYNC_DONE";

    // Menu Id
    long MENU_CODE = 1001;
    long MENU_ACCOUNT = 1002;
    long MENU_WIPE = 1003;
    long MENU_LOGOUT = 1004;
    long MENU_POLLING = 1005;
    long MENU_EXPORT = 1006;
    long MENU_SETTING = 1007;
    long MENU_SCAN_POPULATE = 1008;


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
    String AUTHORIZE_CALL_NUMBER = "+17203866660";

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
    String TOPIC_PASSWORD = "help-download-initial-wallet";
    String TOPIC_MASTER_PASSWORD = "help-master-password";
    String TOPIC_RESET_PASSWORD = "help-changepw";
    String TOPIC_CHASE = "help-account-id";
    String TOPIC_ABOUT = "help-about";
    String TOPIC_SETTINGS = "help-settings";
    String TOPIC_PURPOSE_DETAIL = "TOPIC_PURPOSE_DETAIL";
    String TOPIC_PURPOSE_DETAIL_PICKER = "TOPIC_PURPOSE_DETAIL_PICKER";

    public final String BUILD_FLAVOUR_VNB = "vnb";
    public final String BUILD_FLAVOUR_AUTHORITI = "vnb";

    String TOUCH_ENABLED = "enabled";
    String TOUCH_DISABLED = "disabled";
    String TOUCH_NOT_CONFIGURED = "";

}
