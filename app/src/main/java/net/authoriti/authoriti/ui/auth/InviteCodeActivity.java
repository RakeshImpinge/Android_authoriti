package net.authoriti.authoriti.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mac on 12/12/17.
 */

@EActivity(R.layout.activity_invite_code)
public class InviteCodeActivity extends BaseActivity {

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

    @ViewById(R.id.scrollView)
    NestedScrollView scrollView;


    @AfterViews
    void callAfterViewInjection() {

        if (showBack) {
            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.INVISIBLE);
        }

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {

                if (isOpen) {
                    scrollView.scrollTo(0, (int) ViewUtils.convertDpToPixel(50, mContext));

                } else {
                    scrollView.scrollTo(0, 0);
                }
            }
        });

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
        if (TextUtils.isEmpty(etCode.getText())) {
            if (isSyncRequired) {
                tiCode.setError(utils.getSpannableStringForEditTextError("This field is required",
                        this));
            } else {
                StartupActivity_.intent(mContext).start();
            }
        } else {
            hideKeyboard();
            checkInviteCode(true);
        }
    }

    @Click(R.id.cvNeed)
    void needButtonClicked() {

        if (TextUtils.isEmpty(etCode.getText())) {
            StartupActivity_.intent(mContext).start();
        } else {
            hideKeyboard();
            checkInviteCode(false);
        }
    }


    @Click(R.id.ivHelp)
    void helpButtonClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HELP_BASE +
                TOPIC_INVITE));
        startActivity(browserIntent);
    }

    private void checkInviteCode(final boolean isNextClick) {
        if (isNextClick) {
            displayProgressDialog("Validating Password...");
        } else {
            displayProgressDialog("Please Wait...");
        }
        AuthoritiAPI.APIService().checkInviteCodeValidate(etCode.getText().toString()).enqueue
                (new Callback<ResponseInviteCode>() {
                    @Override
                    public void onResponse(Call<ResponseInviteCode> call,
                                           Response<ResponseInviteCode>
                                                   response) {
                        dismissProgressDialog();
                        if (response.code() == 200 && response.body() != null) {
                            fetchInviteCodeResult(response.body(), isNextClick);
                        } else {
                            if (isNextClick) {
                                showAlert("", "Invalid Password.");
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
