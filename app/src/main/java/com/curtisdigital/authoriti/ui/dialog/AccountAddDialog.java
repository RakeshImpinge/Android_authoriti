package com.curtisdigital.authoriti.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.curtisdigital.authoriti.R;

/**
 * Created by mac on 12/2/17.
 */

public class AccountAddDialog extends AppCompatDialog {

    private AccountAddDialogListener listener;

    public void setListener(AccountAddDialogListener listener){
        this.listener = listener;
    }

    public AccountAddDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_account_add);

        final TextInputLayout tiName = (TextInputLayout) findViewById(R.id.tiName);
        assert tiName != null;
        tiName.setEnabled(true);
        tiName.setError(null);

        final AppCompatEditText etName = (AppCompatEditText) findViewById(R.id.etName);
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
                if (etName.getText().toString().length() <= 0){
                    tiName.setError("This field is required");
                } else {
                    tiName.setError(null);
                }
            }
        });

        final TextInputLayout tiValue = (TextInputLayout) findViewById(R.id.tiValue);
        assert tiValue != null;
        tiValue.setEnabled(true);
        tiValue.setError(null);

        final AppCompatEditText etValue = (AppCompatEditText) findViewById(R.id.etValue);
        assert etValue != null;
        etValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etValue.getText().toString().length() <= 0){
                    tiValue.setError("This field is required");
                } else {
                    tiValue.setError(null);
                }
            }
        });

        CardView cvCancel = (CardView) findViewById(R.id.cvCancel);
        assert cvCancel != null;
        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.accountDialogCancelButtonClicked();
                }
            }
        });

        CardView cvOK = (CardView) findViewById(R.id.cvOK);
        assert cvOK != null;
        cvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etName.getText().toString().length() <= 0){
                    tiName.setError("This field is required");
                }

                if (etValue.getText().toString().length() <= 0){
                    tiValue.setError("This field is required");
                }

                if (etName.getText().toString().length()  > 0 && etValue.getText().toString().length() > 0){
                    if (listener != null){
                        listener.accountDialogOKButtonClicked(etName.getText().toString(), etValue.getText().toString());
                    }
                }
            }
        });
    }

}
