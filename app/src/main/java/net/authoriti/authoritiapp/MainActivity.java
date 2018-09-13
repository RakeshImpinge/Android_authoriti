package net.authoriti.authoritiapp;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import net.authoriti.authoritiapp.api.AuthoritiAPI;
import net.authoriti.authoritiapp.api.model.AccountID;
import net.authoriti.authoritiapp.api.model.AuthLogIn;
import net.authoriti.authoritiapp.api.model.DefaultValue;
import net.authoriti.authoritiapp.api.model.Group;
import net.authoriti.authoritiapp.api.model.Picker;
import net.authoriti.authoritiapp.api.model.Purpose;
import net.authoriti.authoritiapp.api.model.SchemaGroup;
import net.authoriti.authoritiapp.api.model.Value;
import net.authoriti.authoritiapp.api.model.response.ResponsePolling;
import net.authoriti.authoritiapp.core.BaseActivity;
import net.authoriti.authoritiapp.ui.auth.LoginActivity_;
import net.authoriti.authoritiapp.ui.code.CodePermissionActivity_;
import net.authoriti.authoritiapp.ui.menu.AccountChaseFragment_;
import net.authoriti.authoritiapp.ui.menu.AccountFragment_;
import net.authoriti.authoritiapp.ui.menu.PurposeFragment_;
import net.authoriti.authoritiapp.ui.menu.WipeFragment_;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.ConstantUtils;
import net.authoriti.authoritiapp.utils.Constants;

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
    private Fragment accountFragment;
    private Fragment wipeFragment;

    BroadcastReceiver broadcastReceiver;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.btnAdd)
    Button btnAdd;

    @ViewById(R.id.ivHelp)
    ImageButton ivHelp;

    long SELECTED_MENU_ID;

    ArrayList<AccountID> userAccountIds = new ArrayList<>();

    long PollingStopMilliseconds = 0;


    Foreground.Listener listener = new Foreground.Listener() {
        @Override
        public void onBecameForeground() {

            if (dataManager.getInactiveTime() != null && !dataManager.getInactiveTime().equals
                    ("")) {
                long currentTime = System.currentTimeMillis() / 1000;
                Log.e("Active TimeStamp", String.valueOf(currentTime));

                long inactiveTime = Long.parseLong(dataManager.getInactiveTime());
                Log.e("Inactive TimeStamp", String.valueOf(inactiveTime));

                if (currentTime - inactiveTime > 300) {

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
            Log.e("Inactive TimeStamp", String.valueOf(time));

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Foreground.get(getApplication()).addListener(listener);

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
                        new PrimaryDrawerItem().withName(R.string.menu_account).withIdentifier
                                (MENU_ACCOUNT).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_wipe).withIdentifier
                                (MENU_WIPE).withSelectable(true).withTypeface(typeface),
                        new PrimaryDrawerItem().withName(R.string.menu_polling).withIdentifier
                                (MENU_POLLING).withSelectable(true).withTypeface(typeface),
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
            if (!dataManager.getUser().getChaseType()) {
                accountFragment = AccountFragment_.builder().build();
            } else {
                accountFragment = AccountChaseFragment_.builder().build();
            }
            fragment = accountFragment;
        } else if (menu_id == MENU_WIPE) {
            if (wipeFragment == null) {
                wipeFragment = WipeFragment_.builder().build();
            }
            fragment = wipeFragment;
        } else if (menu_id == MENU_POLLING) {
            userAccountIds.clear();
            PollingStopMilliseconds = System.currentTimeMillis() + (30 * 1000);
            userAccountIds.addAll(dataManager.getUser().getAccountIDs());
            startPolling();
            displayProgressDialog("Please Wait...");
        }

        changeFragment(fragment);
    }


    public void updateMenuToolbar(long menu_id) {
        drawer.setSelection(menu_id, false);
        SELECTED_MENU_ID = menu_id;
        if (menu_id == MENU_ACCOUNT) {
            btnAdd.setVisibility(View.VISIBLE);
            if (!dataManager.getUser().getChaseType()) {
                btnAdd.setText("Add");
            } else {
                btnAdd.setText("Sync");
            }
        } else {
            btnAdd.setVisibility(View.INVISIBLE);
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
        }
        if (!topic.equals("")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils
                    .getHelpUrl(topic)));
            startActivity(browserIntent);
//            PermissionCodeRequest
//                    ("authoriti://purpose/manage-an-account?accountId=some_account_id" +
//                            "&data_type=00,02,03&time=1 Hour");
        }
    }

    @Click(R.id.btnAdd)
    void addButtonClicked() {
        if (dataManager.getUser().getChaseType()) {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent
                    (BROADCAST_SYNC_BUTTON_CLICKED));
        } else {
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent
                    (BROADCAST_ADD_BUTTON_CLICKED));
        }
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
                        List<Value> list = dataManager.getValuesFromDataType(Integer.valueOf(key));
                        defValue = new DefaultValue(list.get(0).getTitle(), list.get(0).getValue(),
                                false);
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
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    int currentId = -1;

    private void startPolling() {
        if (currentId == userAccountIds.size() - 1) currentId = 0;
        else currentId = currentId + 1;
        pollingApi(userAccountIds.get(currentId).getIdentifier());
    }

    Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startPolling();
        }
    };

    private void pollingApi(String Id) {
        Log.e("pollingApi", "Started");
        String pollingUrl = Constants.API_BASE_URL_POLLING + Id + ".json";
        AuthoritiAPI.APIService().getPollingUrl(pollingUrl).enqueue
                (new Callback<ResponsePolling>() {
                    @Override
                    public void onResponse(Call<ResponsePolling> call,
                                           Response<ResponsePolling>
                                                   response) {
                        if (response.isSuccessful() && response.body().getUrl() != null &&
                                !response.body().getUrl().equals("")) {
                            dismissProgressDialog();
                            PermissionCodeRequest(response.body().getUrl());
                        } else {
                            if (System.currentTimeMillis() < PollingStopMilliseconds) {
                                handler.removeCallbacks(runnable);
                                handler.postDelayed(runnable, 5000);
                            } else {
                                dismissProgressDialog();
                                showAlert("", "No Pending Updates");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePolling> call, Throwable t) {
                        dismissProgressDialog();
                    }
                });

    }

    // Parse polling url and redirect to next screen
    private void PermissionCodeRequest(String url) {
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
                    } else {

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
                if (!hashMap.isEmpty()) {
                    CodePermissionActivity_.intent(mContext).purposeIndex(indexGroup)
                            .purposeIndexItem(indexItem).defParamFromUrl(hashMap)
                            .start();
                }
            }
        } else {
            Log.e("Message", "Invalid Url");
        }
    }


    private void firstUpdateSchema() {
//        if (dataManager.getScheme() != null && dataManager.getScheme().getPickers() != null) {
//
//            Order order = new Order();
//            List<String> pickers = new ArrayList<>();
//
//            for (Picker picker : dataManager.getScheme().getPickers()) {
//
//                pickers.add(picker.getPicker());
//
//                switch (picker.getPicker()) {
//
//                    case PICKER_ACCOUNT:
//
//                        if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs
//                                () != null && dataManager.getUser().getAccountIDs().size() > 0) {
//
//                            Picker picker1 = new Picker(picker.getPicker(), picker.getBytes(),
//                                    picker.getValues(), picker.getTitle(), picker.getLabel());
//
//                            List<Value> values = new ArrayList<>();
//                            for (AccountID accountID : dataManager.getUser().getAccountIDs()) {
//
//                                Value value = new Value(accountID.getIdentifier(), accountID
//                                        .getType());
//                                values.add(value);
//
//                            }
//                            picker1.setValues(values);
//
//                            if (dataManager.defaultAccountSelected) {
//
//                                picker1.setEnableDefault(true);
//                                picker1.setDefaultIndex(dataManager.defaultAccountIndex);
//
//                                dataManager.defaultAccountSelected = false;
//
//                            }
//
//                            dataManager.setAccountPicker(picker1);
//
//
//                        } else {
//
//                            dataManager.setAccountPicker(picker);
//
//                        }
//
//                        break;
//
//                    case PICKER_INDUSTRY:
//                        dataManager.setIndustryPicker(picker);
//                        break;
//
//                    case PICKER_LOCATION_STATE:
//                        dataManager.setLocationPicker(picker);
//                        break;
//
//                    case PICKER_LOCATION_COUNTRY:
//                        dataManager.setCountryPicker(picker);
//                        break;
//
//                    case PICKER_TIME:
//                        dataManager.setTimePicker(AuthoritiUtils_.getInstance_(mContext)
//                                .getDefaultTimePicker(picker));
//                        break;
//
//                }
//            }
//
//            order.setPickers(pickers);
//            dataManager.setPickerOrder(order);
//        }
//
//
//        if (dataManager.getScheme() != null && dataManager.getScheme().getPickers2() != null) {
//
//            Order order = new Order();
//            List<String> pickers = new ArrayList<>();
//
//            for (Picker picker : dataManager.getScheme().getPickers2()) {
//
//                pickers.add(picker.getPicker());
//
//                switch (picker.getPicker()) {
//
//                    case PICKER_ACCOUNT:
//
//                        break;
//
//                    case PICKER_GEO:
//                        dataManager.setGeoPicker(picker);
//                        break;
//
//                    case PICKER_REQUEST:
//                        dataManager.setRequestPicker(picker);
//                        break;
//
//                    case PICKER_DATA_TYPE:
//                        dataManager.setDataTypePicker(picker);
//                        break;
//
//                    case PICKER_TIME:
//
//                        break;
//
//                }
//            }
//
//            order.setPickers(pickers);
//            dataManager.setPickerOrder2(order);
//        }
    }

    private void updateSchema() {
//        if (dataManager.getScheme() != null && dataManager.getScheme().getPickers() != null) {
//
//            Order order = new Order();
//            List<String> pickers = new ArrayList<>();
//
//            for (Picker picker : dataManager.getScheme().getPickers()) {
//
//                pickers.add(picker.getPicker());
//
//                switch (picker.getPicker()) {
//
//                    case PICKER_ACCOUNT:
//
//                        if (dataManager.getAccountPicker() != null) {
//
//                            Picker temp = new Picker();
//                            temp.setPicker(dataManager.getAccountPicker().getPicker());
//                            temp.setBytes(dataManager.getAccountPicker().getBytes());
//                            temp.setValues(dataManager.getAccountPicker().getValues());
//                            temp.setTitle(picker.getTitle());
//                            temp.setLabel(picker.getLabel());
//                            temp.setEnableDefault(dataManager.getAccountPicker()
// .isEnableDefault());
//                            temp.setDefaultIndex(dataManager.getAccountPicker().getDefaultIndex
// ());
//
//                            dataManager.setAccountPicker(temp);
//                        }
//
//                        break;
//
//                    case PICKER_INDUSTRY:
//
//                        if (dataManager.getIndustryPicker() != null) {
//
//                            Picker temp = new Picker();
//                            temp.setPicker(picker.getPicker());
//                            temp.setBytes(picker.getBytes());
//                            temp.setValues(picker.getValues());
//                            temp.setTitle(picker.getTitle());
//                            temp.setLabel(picker.getLabel());
//                            temp.setEnableDefault(dataManager.getIndustryPicker().isEnableDefault
//                                    ());
//                            temp.setDefaultIndex(dataManager.getIndustryPicker()
// .getDefaultIndex());
//
//                            dataManager.setIndustryPicker(temp);
//
//
//                        } else {
//
//                            dataManager.setIndustryPicker(picker);
//                        }
//
//                        break;
//
//                    case PICKER_LOCATION_STATE:
//
//                        if (dataManager.getLocationPicker() != null) {
//
//                            Picker temp = new Picker();
//                            temp.setPicker(picker.getPicker());
//                            temp.setBytes(picker.getBytes());
//                            temp.setValues(picker.getValues());
//                            temp.setTitle(picker.getTitle());
//                            temp.setLabel(picker.getLabel());
//                            temp.setEnableDefault(dataManager.getLocationPicker().isEnableDefault
//                                    ());
//                            temp.setDefaultIndex(dataManager.getLocationPicker()
// .getDefaultIndex());
//
//                            dataManager.setLocationPicker(temp);
//
//
//                        } else {
//
//                            dataManager.setLocationPicker(picker);
//                        }
//
//                        break;
//
//                    case PICKER_LOCATION_COUNTRY:
//
//                        dataManager.setCountryPicker(picker);
//
//                        break;
//
//                    case PICKER_TIME:
//
//                        if (dataManager.getTimePicker() != null) {
//
//                            Picker temp = new Picker();
//                            temp.setPicker(picker.getPicker());
//                            temp.setBytes(picker.getBytes());
//                            temp.setValues(dataManager.getTimePicker().getValues());
//                            temp.setTitle(picker.getTitle());
//                            temp.setLabel(picker.getLabel());
//                            temp.setEnableDefault(dataManager.getTimePicker().isEnableDefault());
//                            temp.setDefaultIndex(dataManager.getTimePicker().getDefaultIndex());
//
//                            dataManager.setTimePicker(temp);
//
//                        }
//
//                        break;
//
//                }
//            }
//
//            order.setPickers(pickers);
//            dataManager.setPickerOrder(order);
//
//        }
//
//        if (dataManager.getScheme() != null && dataManager.getScheme().getPickers2() != null) {
//
//            Order order = new Order();
//            List<String> pickers = new ArrayList<>();
//
//            for (Picker picker : dataManager.getScheme().getPickers2()) {
//
//                pickers.add(picker.getPicker());
//
//                switch (picker.getPicker()) {
//
//                    case PICKER_ACCOUNT:
//
//                        break;
//
//                    case PICKER_GEO:
//
//                        if (dataManager.getGeoPicker() != null) {
//
//                            Picker temp = new Picker();
//                            temp.setPicker(picker.getPicker());
//                            temp.setBytes(picker.getBytes());
//                            temp.setValues(picker.getValues());
//                            temp.setTitle(picker.getTitle());
//                            temp.setLabel(picker.getLabel());
//                            temp.setEnableDefault(dataManager.getGeoPicker().isEnableDefault());
//                            temp.setDefaultIndex(dataManager.getGeoPicker().getDefaultIndex());
//
//                            dataManager.setGeoPicker(temp);
//
//
//                        } else {
//
//                            dataManager.setGeoPicker(picker);
//                        }
//
//                        break;
//
//                    case PICKER_REQUEST:
//
//                        if (dataManager.getRequestPicker() != null) {
//
//                            Picker temp = new Picker();
//                            temp.setPicker(picker.getPicker());
//                            temp.setBytes(picker.getBytes());
//                            temp.setValues(picker.getValues());
//                            temp.setTitle(picker.getTitle());
//                            temp.setLabel(picker.getLabel());
//                            temp.setEnableDefault(dataManager.getRequestPicker()
// .isEnableDefault());
//                            temp.setDefaultIndex(dataManager.getRequestPicker().getDefaultIndex
// ());
//
//                            dataManager.setRequestPicker(temp);
//
//
//                        } else {
//
//                            dataManager.setRequestPicker(picker);
//                        }
//
//                        break;
//
//                    case PICKER_DATA_TYPE:
//
//                        if (dataManager.getDataTypePicker() != null) {
//
//                            Picker temp = new Picker();
//                            temp.setPicker(picker.getPicker());
//                            temp.setBytes(picker.getBytes());
//                            temp.setValues(picker.getValues());
//                            temp.setTitle(picker.getTitle());
//                            temp.setLabel(picker.getLabel());
//                            temp.setEnableDefault(dataManager.getDataTypePicker().isEnableDefault
//                                    ());
//                            temp.setDefaultIndex(dataManager.getDataTypePicker()
// .getDefaultIndex());
//
//                            dataManager.setDataTypePicker(temp);
//
//
//                        } else {
//
//                            dataManager.setDataTypePicker(picker);
//                        }
//
//                        break;
//
//                    case PICKER_TIME:
//
//                        break;
//
//                }
//            }
//
//            order.setPickers(pickers);
//            dataManager.setPickerOrder2(order);
//
//        }
    }


}
