package net.authoriti.authoriti.ui.code;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.authoriti.authoriti.utils.Log;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.DefaultValue;
import net.authoriti.authoriti.api.model.Group;
import net.authoriti.authoriti.api.model.Picker;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.ui.items.CodeEditItem;
import net.authoriti.authoriti.ui.items.CodeItem;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.ConstantUtils;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by movdev on 3/1/18.
 */

@EActivity(R.layout.activity_code_permission)
public class CodePermissionActivity extends BaseActivity {

    private Group group;
    FastItemAdapter<CodeItem> adapter;
    FastItemAdapter<CodeEditItem> adapter_input;
    //    private Order order;
    @Extra
    int purposeIndex;

    @Extra
    int purposeIndexItem;

    @Extra
    HashMap<String, String> defParamFromUrl;

    @Bean
    AuthoritiData dataManager;

    @Bean
    AuthoritiUtils utils;

    @ViewById(R.id.rvPermission)
    RecyclerView rvPermission;

    @ViewById(R.id.rvEditFields)
    RecyclerView rvEditFields;

    @ViewById(R.id.etCode)
    EditText etCode;

    @ViewById(R.id.tv_title)
    TextView tv_title;


    HashMap<String, DefaultValue> defaultPickerMap = new HashMap<>();

    public static int INTENT_REQUEST_PICK_VALUE = 1;

    int schemaIndex = -1;

    public boolean isRequestClickAvailable = false;

    @AfterViews
    void callAfterViewInjection() {
        adapter = new FastItemAdapter<>();
        rvPermission.setLayoutManager(new LinearLayoutManager(mContext));
        rvPermission.setAdapter(adapter);
        adapter_input = new FastItemAdapter<>();
        rvEditFields.setLayoutManager(new LinearLayoutManager(mContext));
        rvEditFields.setAdapter(adapter_input);
        group = dataManager.getPurposes().get(purposeIndex).getGroups().get(purposeIndexItem);
        defaultPickerMap = dataManager.getDefaultValues().get("" + group.getSchemaIndex());
        schemaIndex = group.getSchemaIndex();
        createPickerList();
        showSchema();
        tv_title.setText(group.getLabel());
    }

    List<Picker> pickersList = new ArrayList<>();

    private void createPickerList() {
        pickersList = dataManager.getScheme().get("" + group.getSchemaIndex());
        final int szPickerList = pickersList.size();
        for (int i = 0; i < szPickerList; i++) {
            // Adding default values of Picker is of TimePICKER_TIME
            if (pickersList.get(i).getPicker().equals(PICKER_TIME)) {
                pickersList.set(i, utils.getDefaultTimePicker(pickersList.get(i)));
            }
            // Adding default values of Picker is of Account Type
            else if (pickersList.get(i).getPicker().equals(PICKER_ACCOUNT)) {
                List<Value> values = new ArrayList<>();
                for (AccountID accountID : dataManager.getUser().getAccountIDs()) {
                    Value value = new Value(accountID.getIdentifier(), accountID
                            .getType());
                    values.add(value);
                }
                pickersList.get(i).setValues(values);
            }
            // Adding default values of Picker is of Data Type
            else if (pickersList.get(i).getPicker().equals(PICKER_DATA_TYPE)) {
                List<Value> values;
                if (schemaIndex == 8) {
                    values = dataManager.getValuesFromDataType("y");
                } else if (defaultPickerMap.containsKey(PICKER_REQUEST)) {
                    values = dataManager.getValuesFromDataType(defaultPickerMap.get
                            (PICKER_REQUEST).getValue());
                } else {
                    values = dataManager.getValuesFromDataType(schemaIndex);
                }

                pickersList.get(i).setValues(values);
                pickersList.set(i, pickersList.get(i));
            }

            String pickerKey = pickersList.get(i).getPicker();
            if (schemaIndex == 3 && pickerKey.equalsIgnoreCase(PICKER_TIME)) {
                defaultPickerMap.put(pickerKey, new DefaultValue(TIME_1_DAY, TIME_1_DAY, false));
            }

            // updateDefaultValuesFromGroup
            final String groupPickerName = group.getPickerName();
            if (groupPickerName != null && !group.getPickerName().equals("")) {
                if (groupPickerName.equals(pickersList.get(i).getPicker())) {
                    int index = getIndexOfValue(pickersList.get(i).getValues(), group.getValue());
                    if (index != -1) {
                        DefaultValue defaultValue = new DefaultValue(pickersList.get(i).getValues
                                ().get
                                (index)
                                .getTitle(), pickersList.get(i).getValues().get(index).getValue()
                                , false);
                        defaultPickerMap.put(groupPickerName, defaultValue);
                    }
                }
            }

            // Automate “Firm Holding Data” data if user if account ID belongs to a customer
            if (pickersList.get(i).getPicker().equals(PICKER_REQUEST)) {
                updateRequesterValueCustomer();
            }


            // Updating the default values from the poll schema url like this :
            // "authoriti://purpose/manage-an-account?accountId=2f434c9c9c1581d407d440d298e2407e2aaf64acc079abce90d9715f5e4dd8d1&schemaVersion=6&origin=Etrade&requestor_value=x&data_type=02%2C03"
            if (defParamFromUrl != null && !defParamFromUrl.isEmpty()) {
                String key = pickersList.get(i).getPicker();
                if (key.equals(PICKER_DATA_INPUT_TYPE)) {
                    key = pickersList.get(i).getInput();
                    String value_decoded = "";
                    try {
                        value_decoded = URLDecoder.decode(defParamFromUrl.get(key), "UTF-8");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DefaultValue defaultValue = new DefaultValue(key
                            , value_decoded, false);
                    defaultPickerMap.put(key, defaultValue);
                } else if (key.equals(PICKER_REQUEST)) {
                    try {
                        String customer = URLDecoder.decode(defParamFromUrl.get("customer"), "UTF-8");
                        String code = defParamFromUrl.get("customer_code");
                        DefaultValue defaultValue = new DefaultValue(customer, code, false);
                        defaultPickerMap.put(key, defaultValue);
                    } catch (Exception ignore) {

                    }
                } else if (key.equals(PICKER_DATA_TYPE)) {
                    try {
                        String requestorValue = defParamFromUrl.get("requestor_value");
                        List<Value> possibleValues = dataManager.getValuesFromDataType(requestorValue);
                        HashMap<String, String> possibleValuesMap = new HashMap<>();
                        for (Value val : possibleValues) {
                            possibleValuesMap.put(val.getValue(), val.getTitle());
                        }

                        String[] data_types = URLDecoder.decode(defParamFromUrl.get(key), "UTF-8").split(",");
                        int len = data_types.length;

                        StringBuilder values = new StringBuilder("");
                        StringBuilder titles = new StringBuilder("");

                        for (int k = 0; k < len; k++) {
                            if (data_types[k].length() < 2) {
                                data_types[k] = "0" + data_types[k];
                            }

                            if (k != 0) {
                                values.append(",");
                                titles.append(",");
                            }

                            values.append(data_types[k]);
                            titles.append(possibleValuesMap.get(data_types[k]));
                        }


                        defaultPickerMap.put(key, new DefaultValue(titles.toString(), values.toString(), false));
                    } catch (Exception ignore) {
                    }
                } else {
                    if (defParamFromUrl.containsKey(key)) {
                        ArrayList<String> title = new ArrayList<>();
                        ArrayList<String> value = new ArrayList<>();
                        for (int k = 0; k < pickersList.get(i).getValues().size(); k++) {
                            if ((defParamFromUrl.get
                                    (pickersList.get(i).getPicker())).contains(pickersList.get(i)
                                    .getValues().get(k).getValue())) {
                                String titleVal = pickersList.get(i).getValues
                                        ().get
                                        (k)
                                        .getTitle();

                                String valueVal = pickersList.get(i).getValues
                                        ().get
                                        (k)
                                        .getValue();

                                title.add(titleVal);
                                value.add(valueVal);
                            }
                        }
                        if (title.size() > 0) {
                            DefaultValue defaultValue = new DefaultValue(title.toString().replace
                                    ("[", "").replace("]", "")
                                    , value.toString().replace
                                    ("[", "").replace("]", ""), false);
                            defaultPickerMap.put(pickersList.get(i).getPicker(), defaultValue);
                        }
                    }
                }
            }
        }
    }


    ArrayList<Integer> uiFlaseListIndex = new ArrayList<>();

    private void showSchema() {
        adapter.clear();
        adapter_input.clear();
        uiFlaseListIndex.clear();
        for (int i = 0; i < pickersList.size(); i++) {
            // Adding Picker to UI
            if (pickersList.get(i).getUi()) {
                if (pickersList.get(i).getPicker().equals(PICKER_DATA_INPUT_TYPE)) {
                    if (defaultPickerMap.containsKey(pickersList.get(i).getInput())) {
                        adapter_input.add(new CodeEditItem(pickersList.get(i), defaultPickerMap
                                .get(pickersList.get(i).getInput()).getValue(), schemaIndex));
                    } else {
                        adapter_input.add(new CodeEditItem(pickersList.get(i), "", schemaIndex));
                    }
                } else {
                    adapter.add(new CodeItem(pickersList.get(i), defaultPickerMap, group
                            .getSchemaIndex(), this));
                }
            } else {
                uiFlaseListIndex.add(i);
            }
        }
    }

    private void updateSchema() {
        adapter.clear();
        uiFlaseListIndex.clear();
        for (int i = 0; i < pickersList.size(); i++) {
            if (pickersList.get(i).getUi()) {
                if (pickersList.get(i).getPicker().equals(PICKER_DATA_INPUT_TYPE)) {

                } else {
                    adapter.add(new CodeItem(pickersList.get(i), defaultPickerMap, group
                            .getSchemaIndex(), this));
                }
            } else {
                uiFlaseListIndex.add(i);
            }
        }
    }

    public int getIndexOfValue(List<Value> values, String picker_def_value) {
        int index = -1;

        if (values == null) return index;
        if (picker_def_value == null) return index;

        for (int k = 0; k < values.size(); k++) {
            if (values.get(k).getValue().equals(picker_def_value)) {
                index = k;
                break;
            }
        }
        return index;
    }


    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        String endPoint = "";
        if (group.getLabel().equalsIgnoreCase("Manage an account")) {
            endPoint = TOPIC_PURPOSE_MANAGE_MY_AC;
        } else if (group.getLabel().equalsIgnoreCase("Transfer funds")) {
            endPoint = TOPIC_PURPOSE_MOVE_MONEY;
        } else if (group.getLabel().equalsIgnoreCase("Open new account")) {
            endPoint = TOPIC_PURPOSE_OPEN_NEW_AC;
        } else if (group.getLabel().equalsIgnoreCase("Remotely withdraw cash")) {
            endPoint = TOPIC_PURPOSE_SEND_MONEY;
        } else if (group.getLabel().equalsIgnoreCase("Trade stocks")) {
            endPoint = TOPIC_PURPOSE_EQUIDITY_TRADE;
        } else if (group.getLabel().equalsIgnoreCase("Share personal information")) {
            endPoint = TOPIC_PURPOSE_SHARE_PERSONAL_DATA;
        } else if (group.getLabel().equalsIgnoreCase("File insurance claim")) {
            endPoint = TOPIC_PURPOSE_INSURENCE_CLAIM;
        } else if (group.getLabel().equalsIgnoreCase("File tax return")) {
            endPoint = TOPIC_PURPOSE_TEX_RETURN;
        } else if (group.getLabel().equalsIgnoreCase("Manage escrow account")) {
            endPoint = TOPIC_PURPOSE_ESCROW;
        }
        if (endPoint.equals("")) return;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ConstantUtils.getHelpUrl
                (endPoint)));
        startActivity(browserIntent);
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.cvGenerate)
    void generateButtonClicked() {
        hideKeyboard();

        String errorMessage = "";

        for (int i = 0; i < rvEditFields.getChildCount(); i++) {
            View childAt = rvEditFields.getChildAt(i);
            AppCompatEditText etCode = ((AppCompatEditText) childAt.findViewById(R.id.etCode));
            if (etCode.getText().toString().trim().length() == 0) {
                if (adapter_input.getAdapterItem(i).picker
                        .getLabel().equalsIgnoreCase("amount")) {
                    errorMessage = "Please enter a valid amount.";
                } else errorMessage = "Please enter " + adapter_input.getAdapterItem(i).picker
                        .getLabel();
                break;
            } else {
                DefaultValue defaultValue = new DefaultValue(adapter_input.getAdapterItem(i)
                        .picker.getInput(),
                        etCode.getText().toString().trim(),
                        false);

                // Adding input values with key input_(input type) so that hash map not override
                // values & adding default values from edit text
                defaultPickerMap.put(PICKER_DATA_INPUT_TYPE + "_" + adapter_input.getAdapterItem
                                (i).picker.getInput(),
                        defaultValue);
            }
        }


        if (errorMessage.length() != 0) {
            showAlert("", errorMessage);
        } else {
            // data_type List length
            int data_type_length;
            if (schemaIndex == 8) {
                data_type_length = dataManager.getValuesFromDataType("y").size();
            } else if (defaultPickerMap.containsKey(PICKER_REQUEST) && defaultPickerMap.containsKey
                    (PICKER_REQUEST)) {
                data_type_length = dataManager.getValuesFromDataType(defaultPickerMap
                        .get(PICKER_REQUEST).getValue()).size();
            } else {
                data_type_length = dataManager.getValuesFromDataType(group.getSchemaIndex()).size();
            }

            ArrayList<HashMap<String, String>> finalPickersList = new ArrayList<HashMap<String,
                    String>>();
            for (int i = 0; i < rvPermission.getChildCount(); i++) {
                Picker adapterPicker = adapter.getAdapterItem(i).picker;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("picker", adapterPicker.getPicker());

                // Send length of data type list
                if (adapterPicker.getPicker().equals(PICKER_DATA_TYPE)) {
                    hashMap.put("key", "" + data_type_length);
                } else {
                    hashMap.put("key", "" + adapterPicker.getPicker());
                }

                // For custom time & Custom date sending mins in value
                if (adapterPicker.getPicker().equals(PICKER_TIME)) {
                    String timeValue = defaultPickerMap.get(adapterPicker.getPicker())
                            .getValue();
                    if (defaultPickerMap.get(adapterPicker.getPicker()).getValue().equals(TIME_CUSTOM_TIME) || defaultPickerMap.get(adapterPicker.getPicker()).getValue().equals(TIME_CUSTOM_DATE)) {
                        timeValue = defaultPickerMap.get(adapterPicker.getPicker()).getTitle();
                    }
                    hashMap.put("value", timeValue);
                } else {
                    hashMap.put("value", defaultPickerMap.get(adapterPicker.getPicker())
                            .getValue());
                }
                finalPickersList.add(hashMap);
            }
            for (int i = 0; i < rvEditFields.getChildCount(); i++) {
                View childAt = rvEditFields.getChildAt(i);
                AppCompatEditText etCode = ((AppCompatEditText) childAt.findViewById(R.id
                        .etCode));
                Picker adapterPicker = adapter_input.getAdapterItem(i).picker;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("picker", adapterPicker.getPicker());
                hashMap.put("key", adapter_input.getAdapterItem(i).picker.getInput());
                hashMap.put("value", etCode.getText().toString());
                finalPickersList.add(hashMap);
            }


            for (int i = 0; i < uiFlaseListIndex.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("picker", pickersList.get(uiFlaseListIndex.get(i)).getPicker());
                hashMap.put("key", defaultPickerMap.get(pickersList.get(uiFlaseListIndex.get(i))
                        .getPicker()).getTitle());
                hashMap.put("value", defaultPickerMap.get(pickersList.get(uiFlaseListIndex.get(i)
                ).getPicker())
                        .getValue());
                finalPickersList.add(uiFlaseListIndex.get(i), hashMap);
            }


            // Default value for any state for schema index 8 i.e clain insurance
            if (schemaIndex == 8) {
                for (int i = 0; i < finalPickersList.size(); i++) {
                    HashMap<String, String> finalPicker = finalPickersList.get(i);
                    if (finalPicker.get("picker").equals("requestor")) {
                        HashMap<String, String> requestor = finalPickersList.get(i);
                        requestor.put("value", "y");
                        requestor.put("key", "requestor");
                        finalPickersList.set(i, requestor);
                    } else if (finalPicker.get("picker").equals("any_state")) {
                        HashMap<String, String> anystate = finalPickersList.get(i);
                        anystate.put("value", "99");
                        anystate.put("key", "any_state");
                        finalPickersList.set(i, anystate);
                    }
                }
            }

            CodeGenerateActivity_.intent(mContext).schemaIndex("" + group.getSchemaIndex()).callAuthorization(group.getCallAuthorization())
                    .finalPickersList(finalPickersList).isPollingRequest(defParamFromUrl != null)
                    .startForResult(CodeGenerateActivity.CODE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyAdapterDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodeGenerateActivity.CODE) {
            finish();
        } else if (resultCode == RESULT_OK) {
            defaultPickerMap = (HashMap<String, DefaultValue>) data.getExtras().get
                    ("selected_values");

            // This is the case to update the data type values on basis of selected picker request
            if (data.getBooleanExtra("isPickerRequestType", false)) {
                for (int i = 0; i < pickersList.size(); i++) {
                    if (pickersList.get(i).getPicker().equals(PICKER_DATA_TYPE)) {
                        List<Value> values;
                        if (defaultPickerMap.containsKey(PICKER_REQUEST)) {
                            values = dataManager.getValuesFromDataType(defaultPickerMap.get
                                    (PICKER_REQUEST).getValue());
                        } else {
                            values = dataManager.getValuesFromDataType(schemaIndex);
                        }
                        pickersList.get(i).setValues(values);
                        pickersList.set(i, pickersList.get(i));
                    }
                }
            }

            updateRequesterValueCustomer();

            updateSchema();
        }
    }

    private void updateRequesterValueCustomer() {
        List<Picker> pickersList = dataManager.getScheme().get("" + group.getSchemaIndex());
        for (int i = 0; i < pickersList.size(); i++) {
            if (pickersList.get(i).getPicker().equals(PICKER_REQUEST)) {
                AccountID accountID = dataManager.getUser().getAccountFromID(defaultPickerMap.get(PICKER_ACCOUNT).getValue());
                String accountCustomerName = accountID.getCustomer();
                Value newRequestorValue = null;
                if (accountCustomerName != null && !accountCustomerName.equals("")) {
                    for (Value value : pickersList.get(i).getValues()) {
                        if (value.getTitle().equalsIgnoreCase(accountCustomerName)) {
                            newRequestorValue = value;
                            break;
                        }
                    }
                }
                if (newRequestorValue != null) {
                    DefaultValue defaultValue = new DefaultValue(newRequestorValue.getTitle(), newRequestorValue.getValue(), false);
                    defaultPickerMap.put(PICKER_REQUEST, defaultValue);
                    isRequestClickAvailable = false;
                } else {
                    isRequestClickAvailable = true;
                }
            }

        }

        if (adapter != null) {
            adapter.notifyAdapterDataSetChanged();
        }
    }

}

