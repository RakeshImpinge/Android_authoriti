package com.curtisdigital.authoriti.ui.menu;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.Picker;
import com.curtisdigital.authoriti.api.model.Scheme;
import com.curtisdigital.authoriti.core.BaseFragment;
import com.curtisdigital.authoriti.ui.items.CodeItem;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
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

    FastItemAdapter adapter;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.rvPermission)
    RecyclerView rvPermission;

    @AfterViews
    void callAfterViewInjection(){

        adapter = new FastItemAdapter();
        rvPermission.setLayoutManager(new LinearLayoutManager(mContext));
        rvPermission.setAdapter(adapter);

        if (dataManager.getPickers() == null){
            loadScheme();
        } else {
            showPermissions();
        }

    }

    private void loadScheme(){
        displayProgressDialog("Loading...");
        AuthoritiAPI.APIService().getScheme().enqueue(new Callback<Scheme>() {
            @Override
            public void onResponse(Call<Scheme> call, Response<Scheme> response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null){
                    Scheme scheme = response.body();
                    if (scheme.getPickers() != null){
                        dataManager.setPickers(scheme.getPickers());
                        showPermissions();
                    }
                }
            }

            @Override
            public void onFailure(Call<Scheme> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    private void showPermissions(){

        if (adapter == null){
            adapter = new FastItemAdapter();
        } else {
            adapter.clear();
        }

        for (Picker picker : dataManager.getPickers()){
            adapter.add(new CodeItem(picker));
        }


    }

}
