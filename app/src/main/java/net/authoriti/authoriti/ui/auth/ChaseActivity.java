package net.authoriti.authoriti.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.BuildConfig;
import net.authoriti.authoriti.MainActivity_;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.request.RequestSignUpChase;
import net.authoriti.authoriti.api.model.response.ResponseSignUpChase;
import net.authoriti.authoriti.core.SecurityActivity;
import net.authoriti.authoriti.ui.help.HelpActivity_;
import net.authoriti.authoriti.ui.share.ImportActivity;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.ViewUtils;
import net.authoriti.authoriti.utils.crypto.CryptoKeyPair;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 12/13/17.
 */

@EActivity(R.layout.activity_chase)
public class ChaseActivity extends SecurityActivity implements SecurityActivity
        .TouchIDEnableAlertListener {

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

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;

    private CryptoKeyPair keyPair;

    private boolean saveSuccess = false;
    public static final int PERMISSIONS_REQUEST_CAMERA = 0;

    @Extra
    boolean isSyncRequired = false;

    @AfterViews
    void callAfterViewInjection() {
        etIdentifier.setHint(customer.toUpperCase() + " USERNAME");
        tvTitle.setText(customer + " is a partner of Authority. Please enter your " + customer +
                " password so we can authorize you.");
        tiPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Oswald_Regular.ttf"));
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (isOpen) {
                    scrollView.scrollTo(0, (int) ViewUtils.convertDpToPixel(100, mContext));

                } else {
                    scrollView.scrollTo(0, 0);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startActivity(new Intent(this, ImportActivity.class));
                    }
                    break;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void signUp() {

        AccountID accountID = new AccountID("", etIdentifier.getText().toString(), false);
        List<AccountID> accountIDs = new ArrayList<>();
        accountIDs.add(accountID);

        keyPair = dataManager.getCryptoKeyPair(etIdentifier.getText().toString(), "");

        RequestSignUpChase requestSignUp = new RequestSignUpChase(etPassword.getText().toString(), keyPair
                .getPublicKey(), keyPair.getSalt(), dataManager.inviteCode, accountIDs);

        displayProgressDialog("Sign Up...");

        AuthoritiAPI.APIService().signUpChase(requestSignUp).enqueue(new Callback<ResponseSignUpChase>() {
            @Override
            public void onResponse(Call<ResponseSignUpChase> call, Response<ResponseSignUpChase>
                    response) {
                dismissProgressDialog();
                if (response.code() == 200 && response.body() != null) {
                    fetchSignUpInfo(response.body());
                } else {
                    showAlert("", "Sign Up Failed. Try Again Later.");
                }
            }

            @Override
            public void onFailure(Call<ResponseSignUpChase> call, Throwable t) {

                dismissProgressDialog();
                showAlert("", "Sign Up Failed. Try Again Later.");
            }
        });

    }

    private void fetchSignUpInfo(ResponseSignUpChase responseSignUpChase) {
        if (isSyncRequired) {
            User user = dataManager.getUser();
            user.setToken(responseSignUpChase.getToken());
            List<AccountID> savedAccountIDs = user.getAccountIDs();
            List<AccountID> newAccountIDs = responseSignUpChase.getAccounts();
            List<AccountID> newIds = new ArrayList<>();

            for (int i = 0; i < newAccountIDs.size(); i++) {
                boolean isContained = false;
                newAccountIDs.get(i).setCustomer(responseSignUpChase.getCustomerName());
                newAccountIDs.get(i).setCustomer_ID(responseSignUpChase.getId());

                for (int k = 0; k < savedAccountIDs.size(); k++) {
                    if (savedAccountIDs.get(k).getIdentifier().equals(newAccountIDs.get(i)
                            .getIdentifier())
                            && savedAccountIDs.get(k).getType().equals(newAccountIDs.get(i)
                            .getType())) {
                        isContained = true;
                        break;
                    }
                }
                if (!isContained) {
                    newAccountIDs.get(i).setHashed(true);
                    newIds.add(newAccountIDs.get(i));
                }
            }
            savedAccountIDs.addAll(newIds);
            user.setAccountIDs(savedAccountIDs);
            dataManager.setUser(user);
            finish();
        } else {
            User user = new User();
            user.setUserId(responseSignUpChase.getId());
            user.setToken(responseSignUpChase.getToken());
            user.setInviteCode(dataManager.inviteCode);
            user.setChaseType(true);

            List<AccountID> UserAccountsList = responseSignUpChase.getAccounts();
            final int sz = UserAccountsList.size();
            for (int i = 0; i < sz; i++) {
                UserAccountsList.get(i).setHashed(true);
            }
            for (AccountID accountID : UserAccountsList) {
                accountID.setCustomer(customer);
                accountID.setCustomer_ID(responseSignUpChase.getId());
            }
            user.setAccountIDs(UserAccountsList);

            try {

                AesCbcWithIntegrity.SecretKeys keys;

                String salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt());
                keys = AesCbcWithIntegrity.generateKeyFromPassword(etPassword.getText().toString(),
                        salt);

                String keyStr = AesCbcWithIntegrity.keyString(keys);

                user.setEncryptKey(keyStr);

                try {
                    user.setEncryptPrivateKey(AesCbcWithIntegrity.encrypt(keyPair.getPrivateKey(),
                            keys).toString());
                    user.setEncryptSalt(AesCbcWithIntegrity.encrypt(keyPair.getSalt(), keys)
                            .toString
                                    ());
                    user.setEncryptPassword(AesCbcWithIntegrity.encrypt(etPassword.getText()
                            .toString
                                    (), keys).toString());


                    dataManager.setUser(user);

                    if (responseSignUpChase.getAccounts() != null && responseSignUpChase.getAccounts
                            ().size() > 0) {

//                        AccountConfirmActivity_.intent(this).start();
                        mFingerPrintAuthHelper.startAuth();
                        hideKeyboard();
                        checkFingerPrintAuth();

                    } else {

                        saveSuccess = true;
                        mFingerPrintAuthHelper.startAuth();

                        hideKeyboard();
                        checkFingerPrintAuth();
                    }


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkFingerPrintAuth() {
        if (isBelowMarshmallow || fingerPrintHardwareNotDetected) {
            updateLoginState();
            goHome();
        } else {
            setListener(this);
            showTouchIDEnableAlert();
        }
    }

    private void goHome() {
        dataManager.setScheme(null);
        Intent intent = new Intent(this, MainActivity_.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void enableFingerPrintAndGoHome() {
        removeListener();

        if (dataManager != null && dataManager.getUser() != null) {

            User user = dataManager.getUser();
            user.setFingerPrintAuthEnabled(true);
            dataManager.setUser(user);

            updateLoginState();
            goHome();
        }

    }

    private void updateLoginState() {
        AuthLogIn logIn = new AuthLogIn();
        logIn.setLogin(true);
        dataManager.setAuthLogin(logIn);
    }

    @AfterTextChange(R.id.etIdentifier)
    void identifierChanged() {
        if (!TextUtils.isEmpty(etIdentifier.getText())) {
            tiIdentifier.setError(null);
        } else {
            tiIdentifier.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
        }
    }

    @AfterTextChange(R.id.etPassword)
    void passwordChanged() {
        if (!TextUtils.isEmpty(etPassword.getText())) {
            tiPassword.setError(null);
        } else {
            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        onBackPressed();
    }

    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HELP_BASE + "/" +
                TOPIC_PASSWORD));
        startActivity(browserIntent);
    }


    @Click(R.id.cvNext)
    void nextButtonClicked() {

        if (TextUtils.isEmpty(etIdentifier.getText())) {

            tiIdentifier.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));

        }

        if (TextUtils.isEmpty(etPassword.getText())) {

            tiPassword.setError(utils.getSpannableStringForEditTextError("This field is " +
                    "required", this));

        }

        if (!TextUtils.isEmpty(etIdentifier.getText()) && !TextUtils.isEmpty(etPassword.getText())) {
            hideKeyboard();
            signUp();

        }
    }

    @Click(R.id.cvImport)
    void importButtonClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(this, ImportActivity.class));
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        } else {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse
                    ("package:" + BuildConfig.APPLICATION_ID)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void allowButtonClicked() {
        hideTouchIDEnabledAlert();

        if (fingerPrintNotRegistered) {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        } else {
            enableFingerPrintAndGoHome();
        }


    }

    @Override
    public void dontAllowButtonClicked() {

        hideTouchIDEnabledAlert();

        updateLoginState();
        goHome();

    }


    @Override
    public void onBackPressed() {
        if (isSyncRequired) {
            InviteCodeActivity_.intent(getApplicationContext()).showBack(true).isSyncRequired
                    (true).start();
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
