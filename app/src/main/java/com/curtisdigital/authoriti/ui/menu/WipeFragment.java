package com.curtisdigital.authoriti.ui.menu;

import android.content.Intent;
import android.graphics.Paint;
import android.widget.TextView;

import com.curtisdigital.authoriti.BuildConfig;
import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.core.BaseFragment;
import com.curtisdigital.authoriti.ui.auth.InviteCodeActivity_;
import com.curtisdigital.authoriti.ui.auth.LoginActivity_;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.google.gson.JsonObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 11/30/17.
 */

@EFragment(R.layout.fragment_wipe)
public class WipeFragment extends BaseFragment {

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.tvVersion)
    TextView tvVersion;

    @ViewById(R.id.tvWipe)
    TextView tvWipe;

    @AfterViews
    void callAfterViewInjection(){

        tvVersion.setText("Version " + BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);

        tvWipe.setPaintFlags(tvWipe.getPaintFlags() |Paint.UNDERLINE_TEXT_FLAG);
    }

    private void deleteAccount(){

        dataManager.wipeSetting();

    }

    private void logOut(){

        AuthLogIn logIn = dataManager.loginStatus();
        logIn.setLogin(false);
        logIn.setWipe(true);
        dataManager.setAuthLogin(logIn);

        LoginActivity_.intent(mContext).flags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK).start();
    }

    private void wipeLogOut(){

        deleteAccount();

        AuthLogIn logIn = dataManager.loginStatus();
        logIn.setLogin(false);
        logIn.setWipe(true);
        dataManager.setAuthLogin(logIn);

        InviteCodeActivity_.intent(mContext).flags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK).showBack(false).start();

    }

    @Click(R.id.tvWipe)
    void wipe(){

        wipeLogOut();

//        String token = "Bearer " + dataManager.getUser().getToken();
//
//        displayProgressDialog("");
//
//        AuthoritiAPI.APIService().wipe(token).enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//
//                dismissProgressDialog();
//                if (response.code() == 200){
//
//                    wipeLogOut();
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                dismissProgressDialog();
//
//            }
//        });

    }
}
