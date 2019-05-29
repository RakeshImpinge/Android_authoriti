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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import net.authoriti.authoriti.BuildConfig;
import net.authoriti.authoriti.ui.share.ImportActivity;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.Log;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.response.ResponseInviteCode;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.ui.help.HelpActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.ViewUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/12/17.
 */

@EActivity(R.layout.activity_invite_code)
public class InviteCodeActivity extends BaseActivity {
    private static final String TAG = "InviteCodeActivity";

    @Extra
    boolean showBack = true;

    @Extra
    boolean isSyncRequired = false;

    @Bean
    AuthoritiUtils utils;

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.tiCode)
    TextInputLayout tiCode;

    @ViewById(R.id.etCode)
    EditText etCode;

    @ViewById(R.id.ivBack)
    ImageView ivBack;

//    @ViewById(R.id.scrollView)
//    NestedScrollView scrollView;

    public static final int PERMISSIONS_REQUEST_CAMERA = 0;

    @AfterViews
    void callAfterViewInjection() {

        tiCode.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Oswald_Regular.ttf"));

        if (showBack) {
            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.INVISIBLE);
        }

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

//                if (isOpen) {
//                    scrollView.scrollTo(0, (int) ViewUtils.convertDpToPixel(50, mContext));
//
//                } else {
//                    scrollView.scrollTo(0, 0);
//                }
            }
        });

        if (ConstantUtils.isBuildFlavorVnb()) {
            etCode.setText("VB131");
            nextButtonClicked();
        }

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

    @AfterTextChange(R.id.etCode)
    void codeChanged() {
        if (!TextUtils.isEmpty(etCode.getText())) {
            tiCode.setError(null);
        } else {
            tiCode.setError(utils.getSpannableStringForEditTextError("This field is required",
                    this));
        }
    }

    @Click(R.id.ivBack)
    void backButtonClicked() {
        finish();
    }

    @Click(R.id.cvNext)
    void nextButtonClicked() {
        System.out.println("InviteCodeActivity: Next clicked");
        if (TextUtils.isEmpty(etCode.getText())) {
            if (isSyncRequired) {
                tiCode.setError(utils.getSpannableStringForEditTextError("This field is required",
                        this));
            } else {
                dataManager.showSkip = true;
                StartupActivity_.intent(mContext).start();
            }
        } else {
            hideKeyboard();
            checkInviteCode(true);
        }
    }


    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HELP_BASE + "/" +
                TOPIC_INVITE));
        startActivity(browserIntent);
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


    private void checkInviteCode(final boolean isNextClick) {
        if (isNextClick) {
            displayProgressDialog("Validating\nPassword");
        } else {
            displayProgressDialog("Please Wait");
        }
        AuthoritiAPI.APIService().checkInviteCodeValidate(etCode.getText().toString().trim()).enqueue
                (new Callback<ResponseInviteCode>() {
                    @Override
                    public void onResponse(Call<ResponseInviteCode> call,
                                           Response<ResponseInviteCode>
                                                   response) {
                        dismissProgressDialog();

                        System.out.println("Response.body: " + response.body());

                        if (response.code() == 200 && response.body() != null) {
                            fetchInviteCodeResult(response.body(), isNextClick);
                        } else {
                            if (isNextClick) {
                                if (response.message() != null && !response.message().equals("")) {
                                    showAlert("", response.message());
                                } else {
                                    showAlert("", "Invalid Password.");
                                }
                            } else {
                                StartupActivity_.intent(mContext).start();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseInviteCode> call, Throwable t) {
                        dismissProgressDialog();
                        showAlert("", t.getLocalizedMessage());
                    }
                });
    }

    private void fetchInviteCodeResult(ResponseInviteCode responseInviteCode, boolean isNextClick) {
        if (!responseInviteCode.isValid()) {
            if (isNextClick) {
                showAlert("", "Invalid Password.");
            } else {
                StartupActivity_.intent(mContext).start();
            }
        } else {
            dataManager.inviteCode = etCode.getText().toString();
            dataManager.showSkip = responseInviteCode.isSkipDLV();
            dataManager.ignoreAcuant = responseInviteCode.ignoreAcuant();
            if (isSyncRequired) {
                if (responseInviteCode.getCustomer() != null) {
                    ChaseActivity_.intent(mContext).customer(responseInviteCode.getCustomer())
                            .isSyncRequired(isSyncRequired).start();
                    finish();
                } else {
                    showAlert("", "Invalid Password.");
                }
            } else {
                if (responseInviteCode.getCustomer() != null) {
                    ChaseActivity_.intent(mContext).customer(responseInviteCode.getCustomer())
                            .start();
                } else {
                    StartupActivity_.intent(mContext).start();
                }
            }
        }
    }
}
