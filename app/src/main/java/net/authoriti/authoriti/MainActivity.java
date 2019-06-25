package net.authoriti.authoriti;

import android.Manifest;
import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.request.RequestComplete;
import net.authoriti.authoriti.api.model.request.RequestSync;
import net.authoriti.authoriti.api.model.response.ResponseComplete;
import net.authoriti.authoriti.api.model.response.ResponseSync;
import net.authoriti.authoriti.core.SecurityActivity;
import net.authoriti.authoriti.ui.menu.AccountFragment;
import net.authoriti.authoriti.ui.menu.ScanPopulateFragment;
import net.authoriti.authoriti.ui.menu.ScanPopulateFragment_;
import net.authoriti.authoriti.ui.menu.SettingFragment_;
import net.authoriti.authoriti.ui.share.ImportActivity;
import net.authoriti.authoriti.utils.Log;

import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.DefaultValue;
import net.authoriti.authoriti.api.model.Group;
import net.authoriti.authoriti.api.model.Picker;
import net.authoriti.authoriti.api.model.Purpose;
import net.authoriti.authoriti.api.model.SchemaGroup;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.api.model.response.ResponsePolling;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.ui.auth.LoginActivity_;
import net.authoriti.authoriti.ui.code.CodePermissionActivity_;
import net.authoriti.authoriti.ui.menu.AccountFragment_;
import net.authoriti.authoriti.ui.menu.PurposeFragment_;
import net.authoriti.authoriti.ui.menu.WipeFragment_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;

import com.google.gson.JsonObject;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.sjl.foreground.Foreground;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

@EActivity(R.layout.activity_main)
public class MainActivity extends SecurityActivity implements SecurityActivity
        .TouchIDEnableAlertListener {

    private static String TAG = "Authoriti/" + MainActivity.class.getName();

    private AccountHeader header = null;
    private Drawer drawer = null;

    private Fragment purposeFragment;
    private Fragment settingFragment;
    private Fragment wipeFragment;
    private Fragment scanPopulateFragment;

    Fragment accountFragment;

    BroadcastReceiver broadcastReceiver;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.ivAdd)
    ImageButton ivAdd;


    @ViewById(R.id.ivCloud)
    ImageButton ivCloud;

    @ViewById(R.id.ivHelp)
    ImageButton ivHelp;

    boolean refreshAccountData;


    long SELECTED_MENU_ID;

    ArrayList<AccountID> userAccountIds = new ArrayList<>();

    long PollingStopMilliseconds = 0;

    public PurposeSchemaStoreInterface purposeSchemaStoreInterface;

    public static final int PERMISSIONS_REQUEST_CAMERA = 0;


    Foreground.Listener listener = new Foreground.Listener() {
        @Override
        public void onBecameForeground() {
            if (dataManager.getInactiveTime() != null && !dataManager.getInactiveTime().equals
                    ("")) {
                long currentTime = System.currentTimeMillis() / 1000;
                long inactiveTime = Long.parseLong(dataManager.getInactiveTime());

                if (currentTime - inactiveTime > INACTIVITY_TIME_OUT) {
                    logOut();
                } else {
                    dataManager.setInactiveTime("");
                }
            } else {
                dataManager.setInactiveTime("");
            }

        }

        @Override
        public void onBecameBackground() {
            long time = System.currentTimeMillis() / 1000;
            dataManager.setInactiveTime(String.valueOf(time));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Foreground.get(getApplication()).addListener(listener);
        if(getIntent().hasExtra("refreshAccountData")){
            refreshAccountData=getIntent().getExtras().getBoolean("refreshAccountData");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        logOut();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @AfterViews
    void callAfterViewInjection() {

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withHeaderBackground(android.R.color.white)
                .build();

        Typeface typeface = Typeface.createFromAsset(getAssets(), getResources().getString(R
                .string.font_oswaldo_regular));

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_code_generate)
                                .withIdentifier(MENU_CODE).withSelectable(true).withTypeface
                                (typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_polling).withIdentifier
                                (MENU_POLLING).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_scan_populate).withIdentifier
                                (MENU_SCAN_POPULATE).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_account).withIdentifier
                                (MENU_ACCOUNT).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_change_pwd).withIdentifier
                                (MENU_SETTING).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_wipe).withIdentifier
                                (MENU_WIPE).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_logOut).withIdentifier
                                (MENU_LOGOUT).withSelectable(true).withTypeface(typeface)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == MENU_LOGOUT) {
                            logOut();
                        } else {
                            menuSelected(drawerItem.getIdentifier());
                        }
                        return false;
                    }
                }).build();


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BROADCAST_CHANGE_MENU)) {
                    long menuId = intent.getLongExtra(MENU_ID, 0);
                    drawer.setSelection(menuId);
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, new
                IntentFilter(BROADCAST_CHANGE_MENU));

        menuSelected(MENU_CODE);
    }

    private void menuSelected(long menu_id) {
        if (SELECTED_MENU_ID == menu_id && menu_id != MENU_POLLING) {
            return;
        }

        SELECTED_MENU_ID = menu_id;
        Fragment fragment = null;
        if (menu_id == MENU_CODE) {
            if (purposeFragment == null) {
                purposeFragment = PurposeFragment_.builder().build();
            }
            fragment = purposeFragment;
        } else if (menu_id == MENU_ACCOUNT) {
            accountFragment = AccountFragment_.builder().build();
            ((AccountFragment) accountFragment).signupInProgress = false;
            fragment = accountFragment;
        } else if (menu_id == MENU_SCAN_POPULATE) {
            if (scanPopulateFragment == null) {
                scanPopulateFragment = ScanPopulateFragment_.builder().build();
            }
            fragment = scanPopulateFragment;
        } else if (menu_id == MENU_SETTING) {
            if (settingFragment == null) {
                settingFragment = SettingFragment_.builder().build();
            }
            fragment = settingFragment;
        } else if (menu_id == MENU_WIPE) {
            if (wipeFragment == null) {
                wipeFragment = WipeFragment_.builder().build();
            }
            fragment = wipeFragment;
        } else if (menu_id == MENU_POLLING) {
            userAccountIds.clear();
            PollingStopMilliseconds = System.currentTimeMillis() + (5 * 1000);
            userAccountIds.addAll(dataManager.getUser().getAccountIDs());
            displayProgressDialog("Please Wait...");
            startPolling();
        }

        changeFragment(fragment);
    }


    public void updateMenuToolbar(long menu_id) {
        drawer.setSelection(menu_id, false);
        SELECTED_MENU_ID = menu_id;
        if (menu_id == MENU_ACCOUNT) {
            ivCloud.setVisibility(View.VISIBLE);
            ivAdd.setVisibility(View.GONE);
        } else {
            ivAdd.setVisibility(View.INVISIBLE);
            ivCloud.setVisibility(View.GONE);
        }
    }

    boolean isFirstFragment = false;

    private void changeFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (!isFirstFragment) {
                isFirstFragment = true;
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment)
                        .commitAllowingStateLoss();
            } else {
                if (fragment instanceof ScanPopulateFragment) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment)
                                .addToBackStack(fragment.getClass().getName())
                                .commitAllowingStateLoss();
                    } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSIONS_REQUEST_CAMERA);
                    } else {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse
                                ("package:" + BuildConfig.APPLICATION_ID)));
                    }
                } else {
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment)
                            .addToBackStack(fragment.getClass().getName())
                            .commitAllowingStateLoss();
                }
            }
        }
    }

    private void logOut() {
        AuthLogIn logIn = dataManager.loginStatus();
        logIn.setLogin(false);
        dataManager.setAuthLogin(logIn);

        Intent intent = new Intent(this, LoginActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Click(R.id.ivMenu)
    void menuButtonClicked() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            drawer.openDrawer();
        }
    }

    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        String topic = "";
        if (SELECTED_MENU_ID == MENU_CODE) {
            topic = TOPIC_GENERAL;
        } else if (SELECTED_MENU_ID == MENU_ACCOUNT) {
            topic = TOPIC_CHASE;
        } else if (SELECTED_MENU_ID == MENU_WIPE) {
            topic = TOPIC_ABOUT;
        } else if (SELECTED_MENU_ID == MENU_SETTING) {
            topic = TOPIC_SETTINGS;
        }
        if (!topic.equals("")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils
                    .getHelpUrl(topic)));
            startActivity(browserIntent);
//            PermissionCodeRequest
//                    ("authoriti://purpose/file-insurance-claim?accountId=6e21466289dfe8fab5d2df4e7a4ba8c74a02a88624a5796e0f83aeea6a00b1f0&schemaVersion=8&customer=Aetna%20Health&customer_code=13&requestor_value=y&data_type=01%2C02&secret=hellothere");
        }
    }


    @Click(R.id.ivAdd)
    void addButtonClicked() {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(BROADCAST_ADD_BUTTON_CLICKED));
    }

    @Click(R.id.ivCloud)
    void cloudButtonClicked() {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(BROADCAST_CLOUD_BUTTON_CLICKED));
    }

    public void syncButtonClicked(final String ID) {
        displayProgressDialog("Downloading\nwallet items...");
        RequestSync sycnew = new RequestSync();
        if (ID == null || ID.equals("")) {
            List<String> downloadIdList = dataManager.getUser().getDownloadedWalletIDList();
            if (!downloadIdList.contains(dataManager.getUser().getUserId())) {
                if (dataManager.getUser().getChaseType()) {
                    downloadIdList.add(dataManager.getUser().getUserId());
                }
            }
        } else {
            List<String> downloadIdList = new ArrayList<>();
            downloadIdList.add(ID);
            sycnew.setUserId(downloadIdList);
        }
        AuthoritiAPI.APIService().sync("Bearer " + dataManager.getUser().getToken(), sycnew).enqueue(new Callback<ResponseSync>() {
            @Override
            public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {
                if (response.isSuccessful()) {
                    User user = dataManager.getUser();
                    List<AccountID> savedAccountIDs = user.getAccountIDs();
                    List<AccountID> newIds = new ArrayList<>();
                    for (ResponseSync.Sync responseSync : response.body().getUpdates()) {
                        List<AccountID> newAccountIDs = responseSync.getAccounts();
                        final int newAccounts = newAccountIDs.size();
                        Log.e("Sync", "Total number of accounts: " + newAccounts);
                        for (int i = 0; i < newAccounts; i++) {
                            Log.e("Loop", "" + i);
                            boolean isContained = false;
                            newAccountIDs.get(i).setCustomer(responseSync.getCustomerName());
                            newAccountIDs.get(i).setCustomer_ID(responseSync.getUserId());
                            if (responseSync.isCallAuth() && responseSync.getCallAuthNumber() != null && !responseSync.getCallAuthNumber().equals("")) {
                                newAccountIDs.get(i).setCallAuthNumber(responseSync.getCallAuthNumber());
                            }

                            for (int k = 0; k < savedAccountIDs.size(); k++) {
                                if (savedAccountIDs.get(k).getIdentifier().equals(newAccountIDs.get(i)
                                        .getIdentifier())
                                        && savedAccountIDs.get(k).getType().equals(newAccountIDs.get(i)
                                        .getType())) {
                                    isContained = true;
                                    break;
                                }
                            }
                            if (!isContained) {
                                newIds.add(newAccountIDs.get(i));
                            }
                        }
                    }
                    savedAccountIDs.addAll(newIds);
                    user.setAccountIDs(savedAccountIDs);
                    dataManager.setUser(user);
                    dismissProgressDialog();
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(BROADCAST_SYNC_DONE));
                    Toast.makeText(MainActivity.this, "Wallets downloaded successfully", Toast.LENGTH_LONG).show();
                } else {
                    dismissProgressDialog();
                    Toast.makeText(MainActivity.this, "Wallets downloaded failed. Please try again later!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSync> call, Throwable t) {
                t.printStackTrace();
                dismissProgressDialog();
                Toast.makeText(MainActivity.this, "Wallets downloaded failed. Please try again later!", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    int isAllDataLoaded = 0;

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        if ((dataManager.getPurposes() == null || dataManager.getScheme() == null)) {
            isAllDataLoaded = 0;
        } else {
            isAllDataLoaded = 2;
        }

        loadPurposes();
        loadScheme();
        checkVersion();
        if (refreshAccountData && dataManager.getPurposes()!=null) {
            refreshCallAuthentication();
        }

        if (dataManager.getUser().getFingerPrintAuthStatus().equals(TOUCH_NOT_CONFIGURED) && !dataManager.getUser().isFingerPrintAuthEnabled()) {
            if (isBelowMarshmallow || fingerPrintHardwareNotDetected) {

            } else {
                setListener(this);
                showTouchIDEnableAlert();
            }
        }

    }


    private void loadPurposes() {
        String inviteCode = dataManager.getUser().getInviteCode();
        AuthoritiAPI.APIService().getPurposes(ConstantUtils.isBuildFlavorVnb() ? "vnb" : "", inviteCode).enqueue(new Callback<List<Purpose>>() {
            @Override
            public void onResponse(Call<List<Purpose>> call, Response<List<Purpose>> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    dataManager.setPurposes(response.body());
                    updateDataLoaded();
                }
            }

            @Override
            public void onFailure(Call<List<Purpose>> call, Throwable t) {
                dismissProgressDialog();
                updateDataLoaded();
            }
        });
    }

    private void loadScheme() {
        AuthoritiAPI.APIService().getSchemeGroup(ConstantUtils.isBuildFlavorVnb() ? "vnb" : "").enqueue(new Callback<SchemaGroup>() {
            @Override
            public void onResponse(Call<SchemaGroup> call, Response<SchemaGroup> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    if (response.body().getDataType() != null) {
                        dataManager.setDataType(response.body().getDataType());
                    }
                    if (response.body().getDataTypeKeys() != null) {
                        dataManager.setDataTypeKeys(response.body().getDataTypeKeys());
                    }
                    if (dataManager.getScheme() == null) {
                        dataManager.setScheme(response.body().getSchema());
                        addDefaultvalues();
                    } else {
                        dataManager.setScheme(response.body().getSchema());
                        updateDefaultvalues();
                    }
                }
                updateDataLoaded();
            }

            @Override
            public void onFailure(Call<SchemaGroup> call, Throwable t) {
                updateDataLoaded();
                dismissProgressDialog();
            }
        });
    }

    private void checkVersion() {
        Log.i(TAG, "Checking Version");
        try {
            AuthoritiAPI.APIService().getMinimuVersion().enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.code() == 200 && response.body() != null) {
                        int version = response.body().get("minimum").getAsInt();
                        if (version > 2) {
                            showErrorAlert("Warning!", "You are using an expired version of " + (ConstantUtils.isBuildFlavorVnb() ? "Valley" : "Authoriti") + "! Please go to the AppStore to update it");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable throwable) {
                    // ignore
                    Log.i(TAG, "Error: " + call);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }
    }

    @UiThread
    protected void showErrorAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Exit", new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        try {
            alertDialog.show();
        } catch (Exception ignore) {

        }
    }

    private void updateDataLoaded() {
        if (isAllDataLoaded == 0) {
            isAllDataLoaded = 1;
        } else if (isAllDataLoaded == 1) {
            isAllDataLoaded = 2;
            purposeSchemaStoreInterface.onDataSaved();
        }
    }

    private void addDefaultvalues() {
        try {
            Map<String, List<Picker>> schemaHashList = dataManager.getScheme();
            List<String> keyList = new ArrayList<String>(schemaHashList.keySet());

            Map<String, HashMap<String, DefaultValue>> defaultSelectedList = new HashMap<>();
            for (String key : keyList) {
                List<Picker> pickers = schemaHashList.get(key);
                HashMap<String, DefaultValue> defaultValuesHashMap = new HashMap();
                for (Picker picker : pickers) {
                    DefaultValue defValue;
                    // Adding default values of Picker is of Time
                    if (picker.getPicker().equals(PICKER_TIME)) {
                        defValue = new DefaultValue(TIME_15_MINS, TIME_15_MINS, false);
                    } else if (picker.getPicker().equals(PICKER_ACCOUNT)) {
                        if (dataManager.getUser().getAccountIDs().size() > 0) {
                            defValue = new DefaultValue(dataManager.getUser().getAccountIDs().get(0)
                                    .getType(), dataManager.getUser().getAccountIDs().get(0)
                                    .getIdentifier(), false);
                            defValue.setCustomer(dataManager.getUser().getAccountIDs().get(0).getCustomer());
                        } else {
                            defValue = new DefaultValue("", "", false);
                        }
                    } else if (picker.getPicker().equals(PICKER_DATA_TYPE)) {
                        if (defaultValuesHashMap.containsKey(PICKER_REQUEST)) {
                            List<Value> list = dataManager.getValuesFromDataType(defaultValuesHashMap.get(PICKER_REQUEST).getValue());
                            if (list.size() == 0) {
                                defValue = new DefaultValue("", "", false);
                            } else {
                                defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                        false);
                            }
                        } else {
                            List<Value> list = dataManager.getValuesFromDataType(key);
                            if (list.size() == 0) {
                                defValue = new DefaultValue("", "", false);
                            } else {
                                defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                        false);
                            }
                        }
                    } else if (picker.getValues() != null && picker.getValues().size() > 0) {
                        defValue = new DefaultValue(picker.getValues().get(0).getTitle(), picker
                                .getValues()
                                .get(0).getValue(), false);
                    } else {
                        defValue = new DefaultValue("", "", false);
                    }
                    defaultValuesHashMap.put(picker.getPicker(), defValue);
                }
                defaultSelectedList.put("" + key.trim(), defaultValuesHashMap);
            }
            dataManager.setDefaultValues(defaultSelectedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateDefaultvalues() {
        try {
            Map<String, List<Picker>> schemaHashList = dataManager.getScheme();
            List<String> keyList = new ArrayList<String>(schemaHashList.keySet());
            Map<String, HashMap<String, DefaultValue>> defaultSelectedList = dataManager.getDefaultValues();
            if (defaultSelectedList == null) {
                defaultSelectedList = new HashMap<>();
            }
            for (String key : keyList) {
                if (defaultSelectedList.containsKey(key) && !defaultSelectedList.get(key.trim()).get(PICKER_ACCOUNT).getValue().equals("")) {
                    continue;
                }
                List<Picker> pickers = schemaHashList.get(key);
                HashMap<String, DefaultValue> defaultValuesHashMap = new HashMap();
                for (Picker picker : pickers) {
                    DefaultValue defValue;
                    // Adding default values of Picker is of Time
                    if (picker.getPicker().equals(PICKER_TIME)) {
                        defValue = new DefaultValue(TIME_15_MINS, TIME_15_MINS, false);
                    } else if (picker.getPicker().equals(PICKER_ACCOUNT)) {
                        if (dataManager.getUser().getAccountIDs().size() > 0) {
                            defValue = new DefaultValue(dataManager.getUser().getAccountIDs().get(0)
                                    .getType(), dataManager.getUser().getAccountIDs().get(0)
                                    .getIdentifier(), false);
                            defValue.setCustomer(dataManager.getUser().getAccountIDs().get(0).getCustomer());
                        } else {
                            defValue = new DefaultValue("", "", false);
                        }
                    } else if (picker.getPicker().equals(PICKER_DATA_TYPE)) {
                        if (defaultValuesHashMap.containsKey(PICKER_REQUEST)) {
                            List<Value> list = dataManager.getValuesFromDataType(defaultValuesHashMap.get(PICKER_REQUEST).getValue());
                            if (list.size() == 0) {
                                defValue = new DefaultValue("", "", false);
                            } else {
                                defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                        false);
                            }
                        } else {
                            List<Value> list = dataManager.getValuesFromDataType(key);
                            if (list.size() == 0) {
                                defValue = new DefaultValue("", "", false);
                            } else {
                                defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                        false);
                            }
                        }
                    } else if (picker.getValues() != null && picker.getValues().size() > 0) {
                        defValue = new DefaultValue(picker.getValues().get(0).getTitle(), picker
                                .getValues()
                                .get(0).getValue(), false);
                    } else {
                        defValue = new DefaultValue("", "", false);
                    }
                    defaultValuesHashMap.put(picker.getPicker(), defValue);
                }
                defaultSelectedList.put("" + key.trim(), defaultValuesHashMap);
            }
            dataManager.setDefaultValues(defaultSelectedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPolling() {
        if (userAccountIds.size() == 0) {
            dismissProgressDialog();
            showAlert("", "No Account/ID added");
            return;
        }
        StringBuilder query = new StringBuilder("");
        boolean first = true;
        final HashMap<String, ArrayList<String>> accCustomerMap = new HashMap<>();
        for (AccountID accId : userAccountIds) {
            String customer = accId.getCustomer();
            if (!customer.equalsIgnoreCase("")) {
                if (first) {
                    query.append("?");
                    first = false;
                } else {
                    query.append("&");
                }
                String id = accId.getIdentifier();
                query.append("accountIds[]=" + id);

                if (!accCustomerMap.containsKey(id)) {
                    accCustomerMap.put(id, new ArrayList<String>());
                }

                accCustomerMap.get(id).add(customer);
            }
        }

        final String url = "https://kwhhlke7m3.execute-api.us-east-1.amazonaws.com/qa/poll" + query.toString();
        AuthoritiAPI.APIService().getPollingUrl(url).enqueue
                (new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String resp = response.body().string();
                            System.out.println("Response: " + resp);
                            JSONObject jsonObject = new JSONObject(resp);
                            System.out.println("Converted");
                            boolean available = jsonObject.getBoolean("available");
                            if (available) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                int n = data.length();
                                for (int i = 0; i < n; i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    String requestString = obj.getString("url");
                                    String id = obj.getString(("id"));

                                    boolean done = false;

                                    List<String> customers = accCustomerMap.get(id);
                                    for (String customer : customers) {
                                        if (PermissionCodeRequest(requestString, customer)) {
                                            removePendingRequest(id);
                                            dismissProgressDialog();
                                            done = true;
                                        }
                                    }

                                    if (!done) {
                                        pollAgain();
                                    }
                                }
                            } else {
                                pollAgain();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        dismissProgressDialog();
                        showAlert("", "No Pending Updates");
                    }
                });

    }

    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startPolling();
        }
    };

    private void pollAgain() {
        if (System.currentTimeMillis() < PollingStopMilliseconds) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 100);
        } else {
            dismissProgressDialog();
            showAlert("", "No Pending Updates");
        }
    }

    private void removePendingRequest(String accountID) {
        RequestComplete requestComplete = new RequestComplete(accountID, "");
        AuthoritiAPI.APIService().removePendingPollingRequest(requestComplete).enqueue
                (new Callback<ResponseComplete>() {
                    @Override
                    public void onResponse(Call<ResponseComplete> call,
                                           Response<ResponseComplete>
                                                   response) {
                    }

                    @Override
                    public void onFailure(Call<ResponseComplete> call, Throwable t) {

                    }
                });
    }


    // Parse polling url and redirect to next screen
    public boolean PermissionCodeRequest(String url, String customer) {
        String[] splitUrl = url.split("\\?");
        if (splitUrl.length > 0) {
            String label = splitUrl[0].replace("authoriti://purpose/", "");
            label = label.replace("-", " ");

            List<Purpose> purposes = dataManager.getPurposes();
            String schemaIndex = "";

            int indexGroup = -1;
            int indexItem = -1;
            for (int i = 0; i < purposes.size(); i++) {
                List<Group> groupList = purposes.get(i).getGroups();
                for (int k = 0; k < groupList.size(); k++) {
                    if (groupList.get(k).getLabel().equalsIgnoreCase(label)) {
                        schemaIndex = "" + groupList.get(k).getSchemaIndex();
                        indexGroup = i;
                        indexItem = k;
                    }
                }
            }
//            accountId=some_account_id" +
//            "&data_type=00,02,03&time=15"
            String defString = splitUrl[1];
            String defStringSplit[] = defString.split("&");
            HashMap<String, String> hashMap = new HashMap<>();
            if (defStringSplit.length > 0) {
                for (String value : defStringSplit) {
                    String splitValue[] = value.split("=");
                    if (splitValue.length > 1) {
                        hashMap.put(splitValue[0].replace("-", ""), splitValue[1].replace("-", ""));
                    }
                }


                String customer_name = "";
                try {
                    customer_name = URLDecoder.decode(hashMap.get("origin"), "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                if (!hashMap.isEmpty() && indexGroup != -1 && indexItem != -1) {
                    // This is the case for auto populate to get customer name from local data
                    if (customer.equals("")) {
                        AccountID accountID = dataManager.getUser().getAccountFromID(hashMap.get(PICKER_ACCOUNT), customer_name);
                        if (accountID == null || accountID.getCustomer().equals("")) {
                            return false;
                        } else {
                            customer = accountID.getCustomer();
                        }
                    }

                    if (customer_name.toLowerCase().equals(customer.toLowerCase())) {
                        CodePermissionActivity_.intent(mContext).purposeIndex(indexGroup)
                                .purposeIndexItem(indexItem).defParamFromUrl(hashMap)
                                .start();
                        return true;
                    } else {
                        return false;
                    }
                } else return false;
            } else return false;
        } else {
            return false;
        }
    }

    public interface PurposeSchemaStoreInterface {
        public void onDataSaved();
    }


    @Override
    public void allowButtonClicked() {
        hideTouchIDEnabledAlert();
        if (fingerPrintNotRegistered) {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        } else {
            removeListener();
            User user = dataManager.getUser();
            user.setFingerPrintAuthEnabled(true);
            user.setFingerPrintAuthStatus(TOUCH_ENABLED);
            dataManager.setUser(user);
        }
    }

    @Override
    public void dontAllowButtonClicked() {
        User user = dataManager.getUser();
        user.setFingerPrintAuthStatus(TOUCH_DISABLED);
        dataManager.setUser(user);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        changeFragment(scanPopulateFragment);
                    }
                    break;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Refresh Saved AccountID's callAuthentication Status
    public void refreshCallAuthentication() {
        RequestSync sycnew = new RequestSync();
        List<AccountID> downloadIdList = dataManager.getUser().getAccountIDs();
        List<String> customerIDList = new ArrayList<>();
        for (AccountID accountID : downloadIdList) {
            if (!accountID.getCustomer_ID().equals("") && !customerIDList.contains(accountID.getCustomer_ID())) {
                customerIDList.add(accountID.getCustomer_ID());
            }
        }
        if (customerIDList.size() == 0) {
            refreshAccountData = false;
            return;
        }
        sycnew.setUserId(customerIDList);
        AuthoritiAPI.APIService().sync("Bearer " + dataManager.getUser().getToken(), sycnew).enqueue(new Callback<ResponseSync>() {
            @Override
            public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {
                if (response.isSuccessful()) {
                    User user = dataManager.getUser();
                    List<AccountID> savedAccountIDs = user.getAccountIDs();
                    for (ResponseSync.Sync responseSync : response.body().getUpdates()) {
                        List<AccountID> newAccountIDs = responseSync.getAccounts();
                        Log.e("Sync", "Total number of accounts: " + newAccountIDs.size());
                        for (int i = 0; i < newAccountIDs.size(); i++) {
                            Log.e("Loop", "" + i);
                            newAccountIDs.get(i).setCustomer(responseSync.getCustomerName());
                            newAccountIDs.get(i).setCustomer_ID(responseSync.getUserId());
                            if (responseSync.isCallAuth() && responseSync.getCallAuthNumber() != null && !responseSync.getCallAuthNumber().equals("")) {
                                newAccountIDs.get(i).setCallAuthNumber(responseSync.getCallAuthNumber());
                            }

                            for (int k = 0; k < savedAccountIDs.size(); k++) {
                                if (savedAccountIDs.get(k).getIdentifier().equals(newAccountIDs.get(i)
                                        .getIdentifier())
                                        && savedAccountIDs.get(k).getType().equals(newAccountIDs.get(i)
                                        .getType())) {
                                    savedAccountIDs.get(k).setCallAuthNumber(newAccountIDs.get(i).getCallAuthNumber());
                                }
                            }
                        }
                    }
                    user.setAccountIDs(savedAccountIDs);
                    dataManager.setUser(user);
                } else {
                }
                refreshAccountData = false;
            }

            @Override
            public void onFailure(Call<ResponseSync> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
