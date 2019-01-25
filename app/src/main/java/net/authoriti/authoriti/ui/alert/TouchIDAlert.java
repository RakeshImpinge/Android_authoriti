package net.authoriti.authoriti.ui.alert;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.utils.ConstantUtils;

import org.androidannotations.annotations.ViewById;

/**
 * Created by mac on 12/29/17.
 */

public class TouchIDAlert extends AppCompatDialog {

    private TextView tvBody;

    private TouchIDAlertDialogListener listener;

    public TouchIDAlert(@NonNull Context context) {
        super(context, R.style.FullScreenDialogStyle);
    }

    public TouchIDAlert(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setListener(TouchIDAlertDialogListener listener){
        this.listener = listener;
    }

    @ViewById(R.id.touch_id_label)
    TextView touchIdLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        setContentView(R.layout.touch_id_alert);
        if (getWindow() != null){
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (ConstantUtils.isBuildFlavorVnb()) {
            touchIdLabel.setText("Touch ID for \"Valley Auth\"");
        }

        tvBody = findViewById(R.id.tvBody);
        assert tvBody != null;
        tvBody.setText("Please scan your touch ID.");

        Button btnCancel = findViewById(R.id.btnCancel);
        assert btnCancel != null;
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){

                    listener.touchIDAlertDialogCancelButtonClicked();

                }
            }
        });
    }

    public TextView getTvBody(){
        return this.tvBody;
    }

    public interface TouchIDAlertDialogListener{

        void touchIDAlertDialogCancelButtonClicked();

    }
}
