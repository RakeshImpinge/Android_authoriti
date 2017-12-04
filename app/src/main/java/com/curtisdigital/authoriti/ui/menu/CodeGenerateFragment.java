package com.curtisdigital.authoriti.ui.menu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Scheme;
import com.curtisdigital.authoriti.core.BaseFragment;
import com.curtisdigital.authoriti.ui.code.CodeGenerateActivity_;
import com.curtisdigital.authoriti.ui.items.CodeItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils_;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 11/30/17.
 */

@EFragment(R.layout.fragment_code_generate)
public class CodeGenerateFragment extends BaseFragment {

    FastItemAdapter<CodeItem> adapter;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.rvPermission)
    RecyclerView rvPermission;

    @AfterViews
    void callAfterViewInjection(){

        adapter = new FastItemAdapter<CodeItem>();
        rvPermission.setLayoutManager(new LinearLayoutManager(mContext));
        rvPermission.setAdapter(adapter);

        if (dataManager.getScheme() == null){
            loadScheme();
        } else {
            showPickers();
        }

    }

    private void loadScheme(){
        displayProgressDialog("Loading...");
        AuthoritiAPI.APIService().getScheme().enqueue(new Callback<Scheme>() {
            @Override
            public void onResponse(Call<Scheme> call, Response<Scheme> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null){
                    dataManager.setScheme(response.body());
                    updatePickers();
                }
            }

            @Override
            public void onFailure(Call<Scheme> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void updatePickers(){

        if (dataManager.getScheme() != null && dataManager.getScheme().getPickers() != null) {
            for (Picker picker : dataManager.getScheme().getPickers()) {
                switch (picker.getPicker()) {
                    case PICKER_ACCOUNT:
                        dataManager.setAccountPicker(picker);
                        break;
                    case PICKER_INDUSTRY:
                        dataManager.setIndustryPicker(picker);
                        break;
                    case PICKER_LOCATION_STATE:
                        dataManager.setLocationPicker(picker);
                        break;
                    case PICKER_TIME:
                        dataManager.setTimePicker(AuthoritiUtils_.getInstance_(mContext).getDefaultTimePicker(picker));
                        break;
                }
            }

            showPickers();
        }
    }

    private void showPickers(){

        if (adapter == null){
            adapter = new FastItemAdapter<CodeItem>();
        } else {
            adapter.clear();
        }

        if (dataManager.getAccountPicker() != null){
            adapter.add(new CodeItem(dataManager.getAccountPicker()));
        }

        if (dataManager.getIndustryPicker() != null){
            adapter.add(new CodeItem(dataManager.getIndustryPicker()));
        }

        if (dataManager.getLocationPicker() != null){
            adapter.add(new CodeItem(dataManager.getLocationPicker()));
        }

        if (dataManager.getTimePicker() != null){
            adapter.add(new CodeItem(dataManager.getTimePicker()));
        }

    }

    @Click(R.id.cvGenerate)
    void generateButtonClicked(){
        CodeGenerateActivity_.intent(mContext).start();
    }

}
