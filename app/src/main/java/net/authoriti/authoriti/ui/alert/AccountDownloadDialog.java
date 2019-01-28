package net.authoriti.authoriti.ui.alert;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.utils.AuthoritiUtils;
import net.authoriti.authoriti.utils.AuthoritiUtils_;
import net.authoriti.authoriti.utils.crypto.CryptoUtil;

/**
 * Created by mac on 1/24/18.
 */

public class AccountDownloadDialog extends AppCompatDialog {

    private TextInputLayout tiInvitationCode, tiUserName, tiUserPassword;
    private EditText etInvitationCode, etUsername, etPasswrd;
    CardView cvCancel, cvOk;
    private AccountDownloadDialogListener listener;

    public AccountDownloadDialog(Context context) {
        super(context);
    }

    public AccountDownloadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setListener(AccountDownloadDialog.AccountDownloadDialogListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setContentView(R.layout.dialog_account_download);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        tiInvitationCode = (TextInputLayout) findViewById(R.id.tiInvitationCode);
        tiUserName = (TextInputLayout) findViewById(R.id.tiUserName);
        tiUserPassword = (TextInputLayout) findViewById(R.id.tiUserPassword);
        tiUserPassword.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Oswald_Regular.ttf"));

        etInvitationCode = (EditText) findViewById(R.id.etInvitationCode);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPasswrd = (EditText) findViewById(R.id.etPasswrd);
        cvCancel = (CardView) findViewById(R.id.cvCancel);
        cvOk = (CardView) findViewById(R.id.cvOk);


        CardView cvCancel = findViewById(R.id.cvCancel);
        CardView cvOk = findViewById(R.id.cvOk);

        final AuthoritiUtils utils = AuthoritiUtils_.getInstance_(getContext());

        assert cvOk != null;
        cvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etInvitationCode.getText())) {
                    tiInvitationCode.setError(utils.getSpannableStringForEditTextError("This field is required", getContext()));
                } else if (TextUtils.isEmpty(etUsername.getText())) {
                    tiUserName.setError(utils.getSpannableStringForEditTextError("This field is required", getContext()));
                } else if (TextUtils.isEmpty(etPasswrd.getText())) {
                    tiUserPassword.setError(utils.getSpannableStringForEditTextError("This field is required", getContext()));
                } else {
                    listener.accountDownloadDialogOKButtonClicked(etInvitationCode.getText().toString(), etUsername.getText().toString(), etPasswrd.getText().toString());
                }
            }
        });

        assert cvCancel != null;
        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.accountDownloadDialogCancelButtonClicked();
            }
        });

    }

    public void init() {
        tiInvitationCode = (TextInputLayout) findViewById(R.id.tiInvitationCode);
        tiUserName = (TextInputLayout) findViewById(R.id.tiUserName);
        tiUserPassword = (TextInputLayout) findViewById(R.id.tiUserPassword);

        if (tiInvitationCode != null)
            tiInvitationCode.setError(null);
        if (tiUserName != null)
            tiUserName.setError(null);
        if (tiUserPassword != null)
            tiUserPassword.setError(null);
    }

    public interface AccountDownloadDialogListener {
        void accountDownloadDialogOKButtonClicked(String inviteCode, String userName, String password);

        void accountDownloadDialogCancelButtonClicked();
    }
}
