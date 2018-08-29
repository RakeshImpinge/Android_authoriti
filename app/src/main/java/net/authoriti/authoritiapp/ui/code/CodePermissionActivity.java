package net.authoriti.authoritiapp.ui.code;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.Group;
import net.authoriti.authoritiapp.api.model.Picker;
import net.authoriti.authoritiapp.core.BaseActivity;
import net.authoriti.authoritiapp.ui.items.CodeEditItem;
import net.authoriti.authoritiapp.ui.items.CodeItem;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import net.authoriti.authoritiapp.utils.Constants;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.stringcare.library.SC;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

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

    @AfterViews
    void callAfterViewInjection() {
        adapter = new FastItemAdapter<CodeItem>();
        rvPermission.setLayoutManager(new LinearLayoutManager(mContext));
        rvPermission.setAdapter(adapter);
        adapter_input = new FastItemAdapter<CodeEditItem>();
        rvEditFields.setLayoutManager(new LinearLayoutManager(mContext));
        rvEditFields.setAdapter(adapter_input);
        group = dataManager.getPurposes().get(purposeIndex).getGroups().get(purposeIndexItem);

        showSchema();
    }

    private void showSchema() {
        adapter.clear();
        adapter_input.clear();
        List<Picker> pickersList = dataManager.getScheme().get("" + group.getSchemaIndex());
        for (Picker picker : pickersList) {
            if (picker.getUi()) {
                if (picker.getPicker().equals(PICKER_DATA_INPUT_TYPE)) {
                    adapter_input.add(new CodeEditItem(picker));
                } else {
                    adapter.add(new CodeItem(picker));
                }
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

        int count = rvEditFields.getChildCount();
        String errorMessage = "";
        for (int i = 0; i < count; i++) {
            View childAt = rvEditFields.getChildAt(i);
            AppCompatEditText etCode = ((AppCompatEditText) childAt.findViewById(R.id.etCode));
            if (etCode.getText().toString().trim().length() == 0) {
                errorMessage = "Pleae enter " + adapter_input.getAdapterItem(i).picker.getLabel();
                break;
            }
        }
        if (errorMessage.length() != 0) {
            showAlert("", errorMessage);
        } else {
            CodeGenerateActivity_.intent(mContext).purposeIndex(purposeIndex).codeExtra("").start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
