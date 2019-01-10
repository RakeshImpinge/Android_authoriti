package net.authoriti.authoriti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;

import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.request.RequestComplete;
import net.authoriti.authoriti.api.model.request.RequestSync;
import net.authoriti.authoriti.api.model.response.ResponseComplete;
import net.authoriti.authoriti.api.model.response.ResponseSync;
import net.authoriti.authoriti.ui.menu.AccountChaseFragment;
import net.authoriti.authoriti.ui.menu.SettingFragment;
import net.authoriti.authoriti.ui.menu.SettingFragment_;
import net.authoriti.authoriti.ui.share.ExportActivity_;
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
import net.authoriti.authoriti.ui.menu.AccountChaseFragment_;
import net.authoriti.authoriti.ui.menu.AccountFragment_;
import net.authoriti.authoriti.ui.menu.PurposeFragment_;
import net.authoriti.authoriti.ui.menu.WipeFragment_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.Constants;

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
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private static String TAG = "Authoriti/" + MainActivity.class.getName();

    private AccountHeader header = null;
    private Drawer drawer = null;

    private Fragment purposeFragment;
    private Fragment settingFragment;
    private Fragment accountFragment;
    private Fragment wipeFragment;

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

    long SELECTED_MENU_ID;

    ArrayList<AccountID> userAccountIds = new ArrayList<>();

    long PollingStopMilliseconds = 0;


    Foreground.Listener listener = new Foreground.Listener() {
        @Override
        public void onBecameForeground() {
            System.out.println("onBecameForeground");
            if (dataManager.getInactiveTime() != null && !dataManager.getInactiveTime().equals
                    ("")) {
                long currentTime = System.currentTimeMillis() / 1000;
                Log.e("Active TimeStamp", String.valueOf(currentTime));

                long inactiveTime = Long.parseLong(dataManager.getInactiveTime());
                Log.e("Inactive TimeStamp", String.valueOf(inactiveTime));

                if (currentTime - inactiveTime > 60) {
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
//        if (menu_id == MENU_EXPORT) {
//            ExportActivity_.intent(getApplicationContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
//            return;
//        }

        SELECTED_MENU_ID = menu_id;
        Fragment fragment = null;
        if (menu_id == MENU_CODE) {
            if (purposeFragment == null) {
                purposeFragment = PurposeFragment_.builder().build();
            }
            fragment = purposeFragment;
        } else if (menu_id == MENU_ACCOUNT) {
            if (!dataManager.getUser().getChaseType()) {
                accountFragment = AccountFragment_.builder().build();
            } else {
                accountFragment = AccountChaseFragment_.builder().build();
            }
            fragment = accountFragment;
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
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment)
                        .addToBackStack(fragment.getClass().getName())
                        .commitAllowingStateLoss();
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
        System.out.println("Help clicked");
        String topic = "";
        if (SELECTED_MENU_ID == MENU_CODE) {
            topic = TOPIC_GENERAL;
        } else if (SELECTED_MENU_ID == MENU_ACCOUNT) {
            if (!dataManager.getUser().getChaseType()) {
                topic = TOPIC_ACCOUNT_2018;
            } else {
                topic = TOPIC_CHASE;
            }
        } else if (SELECTED_MENU_ID == MENU_WIPE) {
            topic = TOPIC_ABOUT;
        } else if (SELECTED_MENU_ID == MENU_SETTING) {
            topic = TOPIC_SETTINGS;
        }
        System.out.println("Topic: " + topic);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadPurposes();
        loadScheme();
    }

    private void loadPurposes() {
        AuthoritiAPI.APIService().getPurposes().enqueue(new Callback<List<Purpose>>() {
            @Override
            public void onResponse(Call<List<Purpose>> call, Response<List<Purpose>> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    dataManager.setPurposes(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Purpose>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void loadScheme() {
        AuthoritiAPI.APIService().getSchemeGroup().enqueue(new Callback<SchemaGroup>() {
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
            }

            @Override
            public void onFailure(Call<SchemaGroup> call, Throwable t) {
                dismissProgressDialog();
            }
        });
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
                        defValue = new DefaultValue(dataManager.getUser().getAccountIDs().get(0)
                                .getType(), dataManager.getUser().getAccountIDs().get(0)
                                .getIdentifier(), false);
                    } else if (picker.getPicker().equals(PICKER_DATA_TYPE)) {
                        if (defaultValuesHashMap.containsKey(PICKER_REQUEST)) {
                            List<Value> list = dataManager.getValuesFromDataType(defaultValuesHashMap.get(PICKER_REQUEST).getValue());
                            defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                    false);
                        } else {
                            List<Value> list = dataManager.getValuesFromDataType(key);
                            defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                    false);
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
                if (defaultSelectedList.containsKey(key)) {
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
                        defValue = new DefaultValue(dataManager.getUser().getAccountIDs().get(0)
                                .getType(), dataManager.getUser().getAccountIDs().get(0)
                                .getIdentifier(), false);
                    } else if (picker.getPicker().equals(PICKER_DATA_TYPE)) {
                        if (defaultValuesHashMap.containsKey(PICKER_REQUEST)) {
                            List<Value> list = dataManager.getValuesFromDataType(defaultValuesHashMap.get(PICKER_REQUEST).getValue());
                            defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                    false);
                        } else {
                            List<Value> list = dataManager.getValuesFromDataType(key);
                            defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                    false);
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
            Log.e("defaultSelectedList", defaultSelectedList.toString());
            dataManager.setDefaultValues(defaultSelectedList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int currentId = -1;

    private void startPolling() {
        if (currentId == userAccountIds.size() - 1) currentId = 0;
        else currentId = currentId + 1;
        AccountID accId = userAccountIds.get(currentId);
        if (!accId.getCustomer().equalsIgnoreCase("")) {
            pollingApi(accId.getIdentifier(), accId.getCustomer());
        } else {
            if (System.currentTimeMillis() < PollingStopMilliseconds) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 100);
            } else {
                dismissProgressDialog();
                showAlert("", "No Pending Updates");
            }
        }
    }

    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startPolling();
        }
    };

    private void pollingApi(final String Id, final String customer) {
        String pollingUrl = Constants.API_BASE_URL_POLLING + Id + ".json";
        AuthoritiAPI.APIService().getPollingUrl(pollingUrl).enqueue
                (new Callback<ResponsePolling>() {
                    @Override
                    public void onResponse(Call<ResponsePolling> call,
                                           Response<ResponsePolling>
                                                   response) {
                        if (response.isSuccessful() && response.body().getUrl() != null &&
                                !response.body().getUrl().equals("")) {
                            removePendingRequest(Id);
                            dismissProgressDialog();
                            PermissionCodeRequest(response.body().getUrl(), customer);
                        } else {
                            if (System.currentTimeMillis() < PollingStopMilliseconds) {
                                handler.removeCallbacks(runnable);
                                handler.postDelayed(runnable, 100);
                            } else {
                                dismissProgressDialog();
                                showAlert("", "No Pending Updates");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePolling> call, Throwable t) {
                        t.printStackTrace();
                        dismissProgressDialog();
                    }
                });
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
    private void PermissionCodeRequest(String url, String customer) {
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
                }
                if (!hashMap.isEmpty() && indexGroup != -1 && indexItem != -1) {
                    if (customer_name.toLowerCase().equals(customer.toLowerCase())) {
                        CodePermissionActivity_.intent(mContext).purposeIndex(indexGroup)
                                .purposeIndexItem(indexItem).defParamFromUrl(hashMap)
                                .start();
                    } else {
                        Log.e("Message", "Invalid Url");
                    }
                }
            }
        } else {
            Log.e("Message", "Invalid Url");
        }
    }

}
