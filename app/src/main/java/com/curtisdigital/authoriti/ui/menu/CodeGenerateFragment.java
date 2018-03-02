package com.curtisdigital.authoriti.ui.menu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.Order;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Scheme;
import com.curtisdigital.authoriti.api.model.Value;
import com.curtisdigital.authoriti.core.BaseFragment;
import com.curtisdigital.authoriti.ui.code.CodeGenerateActivity_;
import com.curtisdigital.authoriti.ui.items.CodeItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;
import com.curtisdigital.authoriti.utils.AuthoritiUtils_;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

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

    @Bean
    AuthoritiUtils utils;

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

            Order order = new Order();
            List<String> pickers = new ArrayList<>();

            for (Picker picker : dataManager.getScheme().getPickers()) {

                pickers.add(picker.getPicker());

                switch (picker.getPicker()) {

                    case PICKER_ACCOUNT:

                        if (dataManager.getUser() != null && dataManager.getUser().getAccountIDs() != null && dataManager.getUser().getAccountIDs().size() > 0){

                            Picker picker1 = new Picker(picker.getPicker(), picker.getBytes(), picker.getValues(), picker.getTitle(), picker.getLabel());

                            List<Value> values = new ArrayList<>();
                            for (AccountID accountID : dataManager.getUser().getAccountIDs()){

                                Value value = new Value(accountID.getIdentifier(), accountID.getType());
                                values.add(value);

                            }
                            picker1.setValues(values);

                            if (dataManager.defaultAccountSelected){

                                picker1.setEnableDefault(true);
                                picker1.setDefaultIndex(dataManager.defaultAccountIndex);

                                dataManager.defaultAccountSelected = false;

                            }

                            dataManager.setAccountPicker(picker1);


                        } else {

                            dataManager.setAccountPicker(picker);

                        }

                        break;

                    case PICKER_INDUSTRY:
                        dataManager.setIndustryPicker(picker);
                        break;

                    case PICKER_LOCATION_STATE:
                        dataManager.setLocationPicker(picker);
                        break;

                    case PICKER_LOCATION_COUNTRY:
                        dataManager.setCountryPicker(picker);
                        break;

                    case PICKER_TIME:
                        dataManager.setTimePicker(AuthoritiUtils_.getInstance_(mContext).getDefaultTimePicker(picker));
                        break;

                }
            }

            order.setPickers(pickers);
            dataManager.setPickerOrder(order);

            showPickers();
        }
    }

    private void showPickers(){

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
                            adapter.add(new CodeItem(dataManager.getAccountPicker()));
                        }
                        break;

                    case PICKER_INDUSTRY:
                        if (dataManager.getIndustryPicker() != null){
                            adapter.add(new CodeItem(dataManager.getIndustryPicker()));
                        }
                        break;
                    case PICKER_LOCATION_STATE:
                        if (dataManager.getLocationPicker() != null){
                            adapter.add(new CodeItem(dataManager.getLocationPicker()));
                        }
                        break;
                    case PICKER_TIME:
                        if (dataManager.getTimePicker() != null){
                            adapter.add(new CodeItem(dataManager.getTimePicker()));
                        }
                        break;
                }

            }

        }

    }

    @Click(R.id.cvGenerate)
    void generateButtonClicked(){
        CodeGenerateActivity_.intent(mContext).start();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (dataManager.getScheme() != null && adapter != null){

            if(mActivity != null && !mActivity.isFinishing()){

                showPickers();

            }

        }
    }
}
