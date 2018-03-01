package com.curtisdigital.authoriti.ui.code;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.model.Purpose;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.ui.items.CodeItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
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

    private Purpose purpose;
    FastItemAdapter<CodeItem> adapter;

    @Extra
    int purposeIndex;

    @Bean
    AuthoritiData dataManager;

    @Bean
    AuthoritiUtils utils;

    @ViewById(R.id.rvPermission)
    RecyclerView rvPermission;

    @AfterViews
    void callAfterViewInjection(){

        adapter = new FastItemAdapter<CodeItem>();
        rvPermission.setLayoutManager(new LinearLayoutManager(mContext));
        rvPermission.setAdapter(adapter);

        purpose = dataManager.getPurposes().getPurposes().get(purposeIndex);

    }

    private void showSchema1(){

        if (adapter == null){
            adapter = new FastItemAdapter<CodeItem>();
        } else {
            adapter.clear();
        }

        if (dataManager.getPickerOrder() != null && dataManager.getPickerOrder().getPickers() != null && dataManager.getPickerOrder().getPickers().size() > 0){

            for (String picker : dataManager.getPickerOrder().getPickers()){

                switch (picker){

                    case PICKER_ACCOUNT:
                        if (dataManager.getAccountPicker() != null){
                            if (purpose.getPickerName() == null || !purpose.getPickerName().equals(PICKER_ACCOUNT)){
                                adapter.add(new CodeItem(dataManager.getAccountPicker()));
                            }
                        }
                        break;

                    case PICKER_INDUSTRY:
                        if (dataManager.getIndustryPicker() != null){
                            if (purpose.getPickerName() == null || !purpose.getPickerName().equals(PICKER_INDUSTRY)){
                                adapter.add(new CodeItem(dataManager.getIndustryPicker()));
                            }
                        }
                        break;
                    case PICKER_LOCATION_STATE:
                        if (dataManager.getLocationPicker() != null){
                            if (purpose.getPickerName() == null || !purpose.getPickerName().equals(PICKER_LOCATION_STATE)){
                                adapter.add(new CodeItem(dataManager.getLocationPicker()));
                            }
                        }
                        break;
                    case PICKER_TIME:
                        if (dataManager.getTimePicker() != null){
                            if (purpose.getPickerName() == null || !purpose.getPickerName().equals(PICKER_TIME)){
                                adapter.add(new CodeItem(dataManager.getTimePicker()));
                            }
                        }
                        break;
                }

            }

        }

    }


    @Click(R.id.ivBack)
    void backButtonClicked(){

        finish();
    }

    @Click(R.id.cvGenerate)
    void generateButtonClicked(){
        CodeGenerateActivity_.intent(mContext).start();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (purpose != null && purpose.getSchemaIndex() == 1 && dataManager.getScheme() != null && adapter != null){

            if(!isFinishing()){

                showSchema1();

            }

        }
    }

}
