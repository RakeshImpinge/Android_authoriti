package net.authoriti.authoritiapp.ui.alert;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import net.authoriti.authoritiapp.utils.AuthoritiUtils_;

/**
 * Created by mac on 1/25/18.
 */

public class AccountConfirmDialog extends AppCompatDialog {

    private TextInputLayout tiNumber;
    private EditText etNumber;
    private CheckBox checkBox;

    AccountConfirmDialogListener listener;

    public void setListener(AccountConfirmDialogListener listener){
        this.listener = listener;
    }

    public AccountConfirmDialog(Context context) {
        super(context);
    }

    public AccountConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setContentView(R.layout.dialog_account_confirm);

        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        tiNumber = findViewById(R.id.tiNumber);
        etNumber = findViewById(R.id.etNumber);
        checkBox = findViewById(R.id.checkbox);

        CardView cvCancel = findViewById(R.id.cvCancel);
        CardView cvOk = findViewById(R.id.cvOk);

        final AuthoritiUtils utils = AuthoritiUtils_.getInstance_(getContext());

        assert cvOk != null;
        cvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assert etNumber != null;
                if (TextUtils.isEmpty(etNumber.getText())){

                    assert tiNumber != null;
                    tiNumber.setError(utils.getSpannableStringForEditTextError("This field is required", getContext()));

                }

                if (!TextUtils.isEmpty(etNumber.getText())){

                    if (listener != null){
                        assert checkBox != null;
                        listener.accountConfirmDialogOKButtonClicked(etNumber.getText().toString(), checkBox.isChecked());
                    }
                }

            }
        });

        assert cvCancel != null;
        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){

                    listener.accountConfirmDialogCancelButtonClicked();
                }

            }
        });

        assert etNumber != null;
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etNumber.getText())){
                    assert tiNumber != null;
                    tiNumber.setError(null);
                }
            }
        });

    }

    public void init(){
        if (tiNumber != null)
            tiNumber.setError(null);
        if (etNumber != null)
            etNumber.setText("");
        if (checkBox != null)
            checkBox.setChecked(false);
    }

    public interface AccountConfirmDialogListener{
        void accountConfirmDialogOKButtonClicked(String id, boolean setDefault);
        void accountConfirmDialogCancelButtonClicked();
    }
}
