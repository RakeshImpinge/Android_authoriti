package net.authoriti.authoritiapp.ui.code;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.AccountID;
import net.authoriti.authoritiapp.api.model.DefaultValue;
import net.authoriti.authoritiapp.api.model.Group;
import net.authoriti.authoritiapp.api.model.Picker;
import net.authoriti.authoritiapp.api.model.Value;
import net.authoriti.authoritiapp.core.BaseActivity;
import net.authoriti.authoritiapp.ui.items.CodeEditItem;
import net.authoriti.authoritiapp.ui.items.CodeItem;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import net.authoriti.authoritiapp.utils.Constants;
import net.authoriti.authoritiapp.utils.crypto.Crypto;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.stringcare.library.SC;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    HashMap<String, DefaultValue> defaultPickerMap = new HashMap<>();

    public static int INTENT_REQUEST_PICK_VALUE = 1;

    int schemaIndex = -1;

    @AfterViews
    void callAfterViewInjection() {
        adapter = new FastItemAdapter<CodeItem>();
        rvPermission.setLayoutManager(new LinearLayoutManager(mContext));
        rvPermission.setAdapter(adapter);
        adapter_input = new FastItemAdapter<CodeEditItem>();
        rvEditFields.setLayoutManager(new LinearLayoutManager(mContext));
        rvEditFields.setAdapter(adapter_input);
        group = dataManager.getPurposes().get(purposeIndex).getGroups().get(purposeIndexItem);
        defaultPickerMap = dataManager.getDefaultValues().get("" + group.getSchemaIndex());
        schemaIndex = group.getSchemaIndex();
        showSchema();
        createPickerList();
        showSchema();
    }

    List<Picker> pickersList = new ArrayList<>();

    private void createPickerList() {
        pickersList = dataManager.getScheme().get("" + group.getSchemaIndex());
        for (int i = 0; i < pickersList.size(); i++) {

            // Adding default values of Picker is of Time
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
                if (defaultPickerMap.containsKey(PICKER_REQUEST)) {
                    values = dataManager.getValuesFromDataType(Integer.valueOf(defaultPickerMap.get
                            (PICKER_REQUEST).getValue().toString()));
                } else {
                    values = dataManager.getValuesFromDataType(schemaIndex);
                }
                pickersList.get(i).setValues(values);
                pickersList.set(i, pickersList.get(i));
            }

            // updateDefaultValuesFromGroup
            if (group.getPickerName() != null && !group.getPickerName().equals("")) {
                if (group.getPickerName().equals(pickersList.get(i).getPicker())) {
                    int index = getIndexOfValue(pickersList.get(i).getValues(), group.getValue());
                    if (index != -1) {
                        DefaultValue defaultValue = new DefaultValue(pickersList.get(i).getValues
                                ().get
                                (index)
                                .getTitle(), pickersList.get(i).getValues().get(index).getValue()
                                , false);
                        defaultPickerMap.put(group.getPickerName(), defaultValue);
                    }
                }
            }

            // Updating the default values from the poll schema url like this :
            // authoriti://purpose/purpose-name?picker1=value&picker2=value
            if (defParamFromUrl != null && !defParamFromUrl.isEmpty()) {
                String key = pickersList.get(i).getPicker();
                if (key.equals(PICKER_DATA_INPUT_TYPE)) {
                    key = pickersList.get(i).getInput();
                    DefaultValue defaultValue = new DefaultValue(key
                            , defParamFromUrl.get(key), false);
                    defaultPickerMap.put(key, defaultValue);
                } else {
                    if (defParamFromUrl.containsKey(key)) {
                        ArrayList<String> title = new ArrayList<>();
                        ArrayList<String> value = new ArrayList<>();
                        for (int k = 0; k < pickersList.get(i).getValues().size(); k++) {
                            if ((defParamFromUrl.get
                                    (pickersList.get(i).getPicker())).contains(pickersList.get(i)
                                    .getValues().get(k).getValue())) {
                                title.add(pickersList.get(i).getValues
                                        ().get
                                        (k)
                                        .getTitle());
                                value.add(pickersList.get(i).getValues
                                        ().get
                                        (k)
                                        .getValue());
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

        Log.e("List", pickersList.toString());
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
                                .get(pickersList.get(i).getInput()).getValue()));
                    } else {
                        adapter_input.add(new CodeEditItem(pickersList.get(i), ""));
                    }
                } else {
                    adapter.add(new CodeItem(pickersList.get(i), defaultPickerMap, group
                            .getSchemaIndex()));
                }
            } else {
                uiFlaseListIndex.add(i);
            }
        }
//        if (order != null && order.getPickers() != null && order.getPickers().size() > 0) {
//
//            for (String picker : order.getPickers()) {
//
//                switch (picker) {
//
//                    case PICKER_ACCOUNT:
//                        if (dataManager.getAccountPicker() != null) {
//                            if (group.getPickerName() == null || !group.getPickerName()
//                                    .equals(PICKER_ACCOUNT)) {
//                                adapter.add(new CodeItem(dataManager.getAccountPicker()));
//                            }
//                        }
//                        break;
//
//                    case PICKER_INDUSTRY:
//                        if (dataManager.getIndustryPicker() != null) {
//                            if (group.getPickerName() == null || !group.getPickerName()
//                                    .equals(PICKER_INDUSTRY)) {
//                                adapter.add(new CodeItem(dataManager.getIndustryPicker()));
//                            }
//                        }
//                        break;
//                    case PICKER_LOCATION_STATE:
//                        if (dataManager.getLocationPicker() != null) {
//                            if (group.getPickerName() == null || !group.getPickerName()
//                                    .equals(PICKER_LOCATION_STATE)) {
//                                adapter.add(new CodeItem(dataManager.getLocationPicker()));
//                            }
//                        }
//                        break;
//                    case PICKER_TIME:
//                        if (dataManager.getTimePicker() != null) {
//                            if (group.getPickerName() == null || !group.getPickerName()
//                                    .equals(PICKER_TIME)) {
//                                adapter.add(new CodeItem(dataManager.getTimePicker()));
//                            }
//                        }
//                        break;
//                    case PICKER_GEO:
//                        if (dataManager.getGeoPicker() != null) {
//                            if (group.getPickerName() == null || !group.getPickerName()
//                                    .equals(PICKER_GEO)) {
//                                adapter.add(new CodeItem(dataManager.getGeoPicker()));
//                            }
//                        }
//                        break;
//                    case PICKER_REQUEST:
//                        if (dataManager.getRequestPicker() != null) {
//                            if (group.getPickerName() == null || !group.getPickerName()
//                                    .equals(PICKER_REQUEST)) {
//                                adapter.add(new CodeItem(dataManager.getRequestPicker()));
//                            }
//                        }
//                        break;
//                    case PICKER_DATA_TYPE:
//                        if (dataManager.getDataTypePicker() != null) {
//                            if (group.getPickerName() == null || !group.getPickerName()
//                                    .equals(PICKER_DATA_TYPE)) {
//                                adapter.add(new CodeItem(dataManager.getDataTypePicker()));
//                            }
//                        }
//                        break;
//                }
//            }
//        }
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SC.decryptString
                (Constants.HELP_BASE) + TOPIC_PURPOSE_DETAIL));
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
                errorMessage = "Pleae enter " + adapter_input.getAdapterItem(i).picker
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
            int data_type_length = 0;
            if (defaultPickerMap.containsKey(PICKER_REQUEST) && defaultPickerMap.containsKey
                    (PICKER_REQUEST)) {
                data_type_length = dataManager.getValuesFromDataType(Integer.valueOf
                        (defaultPickerMap
                                .get(PICKER_REQUEST).getValue().toString())).size();
            } else {
                data_type_length = dataManager.getValuesFromDataType(Integer.valueOf(group
                        .getSchemaIndex())).size();
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

                // For account name
                if (adapterPicker.getPicker().equals(PICKER_ACCOUNT)) {
                    hashMap.put("value", defaultPickerMap.get(adapterPicker.getPicker())
                            .getValue());

                } else {
                    hashMap.put("value", defaultPickerMap.get(adapterPicker.getPicker())
                            .getValue
                                    ());
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

            CodeGenerateActivity_.intent(mContext).schemaIndex("" + group.getSchemaIndex())
                    .finalPickersList(finalPickersList).isPollingRequest(defParamFromUrl != null)
                    .start();
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
        if (resultCode == RESULT_OK) {
            defaultPickerMap = (HashMap<String, DefaultValue>) data.getExtras().get
                    ("selected_values");

            // This is the case to update the data type values on basis of selected picker request
            if (data.getBooleanExtra("isPickerRequestType", false)) {
                for (int i = 0; i < pickersList.size(); i++) {
                    if (pickersList.get(i).getPicker().equals(PICKER_DATA_TYPE)) {
                        List<Value> values;
                        if (defaultPickerMap.containsKey(PICKER_REQUEST)) {
                            values = dataManager.getValuesFromDataType(Integer.valueOf
                                    (defaultPickerMap.get
                                            (PICKER_REQUEST).getValue().toString()));
                        } else {
                            values = dataManager.getValuesFromDataType(schemaIndex);
                        }
                        pickersList.get(i).setValues(values);
                        pickersList.set(i, pickersList.get(i));
                    }
                }
            }
            showSchema();
        }
    }
}

