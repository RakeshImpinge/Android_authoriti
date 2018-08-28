package net.authoriti.authoritiapp.ui.code;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.api.model.Group;
import net.authoriti.authoritiapp.api.model.Order;
import net.authoriti.authoritiapp.api.model.Purpose;
import net.authoriti.authoritiapp.core.BaseActivity;
import net.authoriti.authoritiapp.ui.items.CodeItem;
import net.authoriti.authoritiapp.utils.AuthoritiData;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * Created by movdev on 3/1/18.
 */

@EActivity(R.layout.activity_code_permission)
public class CodePermissionActivity extends BaseActivity {

    private Group group;
    FastItemAdapter<CodeItem> adapter;
    private Order order;

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

    @ViewById(R.id.codeView)
    View codeView;

    @ViewById(R.id.etCode)
    EditText etCode;

    @AfterViews
    void callAfterViewInjection() {
        adapter = new FastItemAdapter<CodeItem>();
        rvPermission.setLayoutManager(new LinearLayoutManager(mContext));
        rvPermission.setAdapter(adapter);
        group = dataManager.getPurposes().get(purposeIndex).getGroups().get(purposeIndexItem);
        if (group.getSchemaIndex() == 1) {
            order = dataManager.getPickerOrder();
        } else {
            order = dataManager.getPickerOrder2();
            codeView.setVisibility(View.VISIBLE);
        }
    }

    private void showSchema() {

        if (adapter == null) {
            adapter = new FastItemAdapter<CodeItem>();
        } else {
            adapter.clear();
        }

        if (order != null && order.getPickers() != null && order.getPickers().size() > 0) {

            for (String picker : order.getPickers()) {

                switch (picker) {

                    case PICKER_ACCOUNT:
                        if (dataManager.getAccountPicker() != null) {
                            if (group.getPickerName() == null || !group.getPickerName()
                                    .equals(PICKER_ACCOUNT)) {
                                adapter.add(new CodeItem(dataManager.getAccountPicker()));
                            }
                        }
                        break;

                    case PICKER_INDUSTRY:
                        if (dataManager.getIndustryPicker() != null) {
                            if (group.getPickerName() == null || !group.getPickerName()
                                    .equals(PICKER_INDUSTRY)) {
                                adapter.add(new CodeItem(dataManager.getIndustryPicker()));
                            }
                        }
                        break;
                    case PICKER_LOCATION_STATE:
                        if (dataManager.getLocationPicker() != null) {
                            if (group.getPickerName() == null || !group.getPickerName()
                                    .equals(PICKER_LOCATION_STATE)) {
                                adapter.add(new CodeItem(dataManager.getLocationPicker()));
                            }
                        }
                        break;
                    case PICKER_TIME:
                        if (dataManager.getTimePicker() != null) {
                            if (group.getPickerName() == null || !group.getPickerName()
                                    .equals(PICKER_TIME)) {
                                adapter.add(new CodeItem(dataManager.getTimePicker()));
                            }
                        }
                        break;
                    case PICKER_GEO:
                        if (dataManager.getGeoPicker() != null) {
                            if (group.getPickerName() == null || !group.getPickerName()
                                    .equals(PICKER_GEO)) {
                                adapter.add(new CodeItem(dataManager.getGeoPicker()));
                            }
                        }
                        break;
                    case PICKER_REQUEST:
                        if (dataManager.getRequestPicker() != null) {
                            if (group.getPickerName() == null || !group.getPickerName()
                                    .equals(PICKER_REQUEST)) {
                                adapter.add(new CodeItem(dataManager.getRequestPicker()));
                            }
                        }
                        break;
                    case PICKER_DATA_TYPE:
                        if (dataManager.getDataTypePicker() != null) {
                            if (group.getPickerName() == null || !group.getPickerName()
                                    .equals(PICKER_DATA_TYPE)) {
                                adapter.add(new CodeItem(dataManager.getDataTypePicker()));
                            }
                        }
                        break;
                }

            }

        }

    }

    @Click(R.id.ivBack)
    void backButtonClicked() {

        finish();
    }

    @Click(R.id.cvGenerate)
    void generateButtonClicked() {

        if (group.getSchemaIndex() == 1) {

            CodeGenerateActivity_.intent(mContext).purposeIndex(purposeIndex).start();

        } else if (group.getSchemaIndex() == 2) {

            hideKeyboard();

            if (etCode.getText().length() == 0) {

                showAlert("", "Please enter the code provided by the Company you wish to grant " +
                        "access.");

            } else {

                CodeGenerateActivity_.intent(mContext).purposeIndex(purposeIndex).codeExtra
                        (etCode.getText().toString()).start();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (group != null && dataManager.getScheme() != null && adapter != null) {

            if (!isFinishing()) {

                showSchema();
            }

        }
    }

}
