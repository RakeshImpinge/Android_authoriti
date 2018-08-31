package net.authoriti.authoritiapp.ui.alert;

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

import net.authoriti.authoritiapp.R;
import net.authoriti.authoritiapp.utils.AuthoritiUtils;
import net.authoriti.authoritiapp.utils.AuthoritiUtils_;

/**
 * Created by mac on 1/24/18.
 */

public class AccountAddDialog extends AppCompatDialog {

    private TextInputLayout tiName;
    private TextInputLayout tiNumber;
    private EditText etName;
    private EditText etNumber;
    private CheckBox checkBox;

    private AccountAddDialogListener listener;

    public void setListener(AccountAddDialogListener listener){
        this.listener = listener;
    }

    public AccountAddDialog(Context context) {
        super(context);
    }

    public AccountAddDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setContentView(R.layout.dialog_account_add);

        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

        tiName = findViewById(R.id.tiName);
        tiNumber = findViewById(R.id.tiNumber);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);
        checkBox = findViewById(R.id.checkbox);

        CardView cvCancel = findViewById(R.id.cvCancel);
        CardView cvOk = findViewById(R.id.cvOk);

        final AuthoritiUtils utils = AuthoritiUtils_.getInstance_(getContext());

        assert cvOk != null;
        cvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assert etName != null;
                if (TextUtils.isEmpty(etName.getText())){

                    assert tiName != null;
                    tiName.setError(utils.getSpannableStringForEditTextError("This field is required", getContext()));

                }

                assert etNumber != null;
                if (TextUtils.isEmpty(etNumber.getText())){

                    assert tiNumber != null;
                    tiNumber.setError(utils.getSpannableStringForEditTextError("This field is required", getContext()));

                }

                if (!TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etNumber.getText())){

                    if (listener != null){
                        assert checkBox != null;
                        listener.accountAddDialogOKButtonClicked(etName.getText().toString(), etNumber.getText().toString(), checkBox.isChecked());
                    }
                }

            }
        });

        assert cvCancel != null;
        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){

                    listener.accountAddDialogCancelButtonClicked();
                }

            }
        });

        assert etName != null;
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etName.getText())){
                    assert tiName != null;
                    tiName.setError(null);
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
        if (tiName != null)
        tiName.setError(null);
        if (tiNumber != null)
        tiNumber.setError(null);
        if (etNumber != null)
        etNumber.setText("");
        if (etName != null)
        etName.setText("");
        if (checkBox != null)
        checkBox.setChecked(false);
    }

    public interface AccountAddDialogListener{

        void accountAddDialogOKButtonClicked(String name, String id, boolean setDefault);
        void accountAddDialogCancelButtonClicked();
    }
}
