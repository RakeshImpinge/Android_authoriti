package com.curtisdigital.authoriti.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by mac on 11/25/17.
 */

public class BaseFragment extends Fragment {

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

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
