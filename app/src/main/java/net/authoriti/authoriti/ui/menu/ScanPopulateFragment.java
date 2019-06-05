package net.authoriti.authoriti.ui.menu;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.Result;

import net.authoriti.authoriti.MainActivity;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.core.BaseFragment;
import net.authoriti.authoriti.ui.auth.ChangePasswordActivity_;
import net.authoriti.authoriti.ui.auth.InviteCodeActivity_;
import net.authoriti.authoriti.ui.share.ExportActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.Constants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_scan_populate)
public class ScanPopulateFragment extends BaseFragment implements ZXingScannerView.ResultHandler {
    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.ZXingScannerView)
    ZXingScannerView mScannerView;

    @AfterViews
    void callAfterViewInjection() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).updateMenuToolbar(Constants.MENU_SCAN_POPULATE);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(final Result rawResult) {
        if (rawResult.getText().length() > 10) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rawResult.getText().contains("authoriti://") && ((MainActivity) getActivity()).PermissionCodeRequest(rawResult.getText(), "")) {
                    } else {
                        Toast.makeText(mContext, "Unable to scan. Trying Again", Toast.LENGTH_SHORT).show();
                        mScannerView.resumeCameraPreview(ScanPopulateFragment.this);
                    }
                }
            });
        } else {
            Toast.makeText(mContext, "Unable to scan. Trying Again", Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }
    }
}
