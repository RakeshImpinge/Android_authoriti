package com.curtisdigital.authoriti.ui.auth;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.curtisdigital.authoriti.MainActivity_;
import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.api.AuthoritiAPI;
import com.curtisdigital.authoriti.api.model.AccountID;
import com.curtisdigital.authoriti.api.model.AuthLogIn;
import com.curtisdigital.authoriti.api.model.User;
import com.curtisdigital.authoriti.api.model.request.RequestSignUp;
import com.curtisdigital.authoriti.api.model.response.ResponseSignUpChase;
import com.curtisdigital.authoriti.core.BaseActivity;
import com.curtisdigital.authoriti.utils.AuthoritiData;
import com.curtisdigital.authoriti.utils.AuthoritiUtils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.simbio.encryption.Encryption;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_chase)
public class ChaseActivity extends BaseActivity {

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;


    @Extra
    String customer;

    @ViewById(R.id.tvTitle)
    TextView tvTitle;

    @ViewById(R.id.tiIdentifier)
    TextInputLayout tiIdentifier;

    @ViewById(R.id.etIdentifier)
    EditText etIdentifier;

    @ViewById(R.id.tiPassword)
    TextInputLayout tiPassword;

    @ViewById(R.id.etPassword)
    EditText etPassword;

    @AfterViews
    void callAfterViewInjection(){

        tvTitle.setText(customer + " is a partner of Authority. Please enter your " + customer + " password so we can authorize you.");

    }

    private void signUp(){

        AccountID accountID = new AccountID("", etIdentifier.getText().toString());
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);

        RequestSignUp requestSignUp = new RequestSignUp(etPassword.getText().toString(), dataManager.key, dataManager.salt, dataManager.inviteCode, accountIDs);

        displayProgressDialog("Sign Up...");

        AuthoritiAPI.APIService().signUpChase(requestSignUp).enqueue(new Callback<ResponseSignUpChase>() {
            @Override
            public void onResponse(Call<ResponseSignUpChase> call, Response<ResponseSignUpChase> response) {

                dismissProgressDialog();

                if (response.code() == 200 && response.body() != null){

                    fetchSignUpInfo(response.body());


                } else {

                    showAlert("","Sign Up Failed. Try Again Later.");

                }

            }

            @Override
            public void onFailure(Call<ResponseSignUpChase> call, Throwable t) {

                dismissProgressDialog();
                showAlert("","Sign Up Failed. Try Again Later.");
            }
        });

    }

    private void fetchSignUpInfo(ResponseSignUpChase responseSignUpChase){

        User user = new User();
        user.setUserId(responseSignUpChase.getId());
        user.setToken(responseSignUpChase.getToken());
        user.setPassword(etPassword.getText().toString());
        user.setInviteCode(dataManager.inviteCode);
        user.setSalt(dataManager.salt);
        user.setPrivateKey(dataManager.key);

        Encryption encryption = Encryption.getDefault(dataManager.key, dataManager.salt, dataManager.iv);

        user.setEncryptKey(encryption.encryptOrNull(dataManager.key));
        user.setEncryptSalt(encryption.encryptOrNull(dataManager.salt));
        user.setEncryptPassword(encryption.encryptOrNull(etPassword.getText().toString()));

        AccountID accountID = new AccountID(responseSignUpChase.getAccountName(), etIdentifier.getText().toString());
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);

        user.setAccountIDs(accountIDs);

        if (responseSignUpChase.getAccounts() != null && responseSignUpChase.getAccounts().size() > 0){

            List<AccountID> accountIDs1 = new ArrayList<>();

            for (int i = 0 ; i < responseSignUpChase.getAccounts().size() ; i ++){

                AccountID accountID1 = new AccountID();
                accountID1.setConfirmed(false);
                accountID1.setType(responseSignUpChase.getAccounts().get(i));
                accountID1.setIdentifier("");

                accountIDs1.add(accountID1);

            }

            user.setUnconfirmedAccountIDs(accountIDs1);
        }

        dataManager.setUser(user);

        if (responseSignUpChase.getAccounts() != null && responseSignUpChase.getAccounts().size() > 0){

            AccountConfirmActivity_.intent(this).start();

        } else {

            updateLoginState();
            goHome();

        }


    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateLoginState(){

        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    @AfterTextChange(R.id.etIdentifier)
    void identifierChanged(){
        if (!TextUtils.isEmpty(etIdentifier.getText())){
            tiIdentifier.setError(null);
        } else {
            tiIdentifier.setError(utils.getSpannableStringForEditTextError("This field is required", this));
        }
    }

    @AfterTextChange(R.id.etPassword)
    void passwordChanged(){
        if (!TextUtils.isEmpty(etPassword.getText())){
            tiPassword.setError(null);
        } else {
            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is required", this));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked(){
        finish();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked(){

        if (TextUtils.isEmpty(etIdentifier.getText())){

            tiIdentifier.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (TextUtils.isEmpty(etPassword.getText())){

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is required", this));

        }

        if (!TextUtils.isEmpty(etIdentifier.getText()) && !TextUtils.isEmpty(etPassword.getText())){

            signUp();

        }
    }
}
