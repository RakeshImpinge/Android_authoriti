package net.authoriti.authoritiapp.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import net.authoriti.authoritiapp.utils.Constants;

/**
 * Created by mac on 11/25/17.
 */

public class BaseFragment extends Fragment implements Constants{

    public AppCompatActivity mActivity;
    protected Context mContext;

    protected ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity)getActivity();
        mContext = getActivity();
    }

    @UiThread
    protected void displayProgressDialog(String message) {
        progress = new ProgressDialog(mActivity);
        progress.setMessage(message);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        if (!mActivity.isFinishing()){
            progress.show();
        }
    }

    @UiThread
    protected void dismissProgressDialog() {
        if (progress != null && progress.isShowing()) {

            try {
                progress.dismiss();
            } catch (Exception e) {

            }

            progress = null;
        }
    }

    @UiThread
    protected void showAlert(String title, String message) {
        if(!mActivity.isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            try {
                alertDialog.show();
            } catch (Exception e) {

            }
        }
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
