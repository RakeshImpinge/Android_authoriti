package net.authoriti.authoriti.ui.alert;

import android.content.Context;
import android.graphics.Color;
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

public class AccountPasswordDialog extends AppCompatDialog {

    private TextInputLayout tiPassword;
    private EditText etPassword;

    private AccountPasswordDialogListener listener;

    public void setListener(AccountPasswordDialogListener listener) {
        this.listener = listener;
    }

    public AccountPasswordDialog(Context context) {
        super(context);
    }

    public AccountPasswordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setContentView(R.layout.dialog_account_password);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        tiPassword = findViewById(R.id.tiPassword);
        etPassword = findViewById(R.id.etPassword);

        CardView cvCancel = findViewById(R.id.cvCancel);
        CardView cvOk = findViewById(R.id.cvOk);

        final AuthoritiUtils utils = AuthoritiUtils_.getInstance_(getContext());

        assert cvOk != null;
        cvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etPassword.getText())) {
                    assert etPassword != null;
                    etPassword.setError(utils.getSpannableStringForEditTextError("This field is required", getContext()));
                }
                if (!TextUtils.isEmpty(etPassword.getText())) {
                    if (listener != null) {
                        listener.accountPasswordDialogOKButtonClicked(etPassword.getText().toString());
                    }
                }
            }
        });

        assert cvCancel != null;
        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.accountAddDialogCancelButtonClicked();
                }
            }
        });


        assert etPassword != null;
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etPassword.getText())) {
                    assert tiPassword != null;
                    tiPassword.setError(null);
                }
            }
        });
    }

    public void init() {
        if (tiPassword != null)
            tiPassword.setError(null);
        if (etPassword != null)
            etPassword.setText("");
    }

    public interface AccountPasswordDialogListener {
        void accountPasswordDialogOKButtonClicked(String password);

        void accountAddDialogCancelButtonClicked();
    }
}
