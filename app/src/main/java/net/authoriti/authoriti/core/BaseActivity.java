package net.authoriti.authoriti.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.kaopiz.kprogresshud.KProgressHUD;

import net.authoriti.authoriti.MainActivity;
import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.ui.auth.LoginActivity;
import net.authoriti.authoriti.ui.auth.LoginActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.Log;

import org.androidannotations.annotations.Bean;

import java.util.Timer;
import java.util.TimerTask;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by mac on 11/25/17.
 */

public class BaseActivity extends AppCompatActivity implements Constants {

    private AppCompatActivity mActivity;
    protected Context mContext;

    protected ProgressDialog progress;
    KProgressHUD kProgressHUD;

    private Timer timer;

    AuthoritiData localDataManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity = this;
        mContext = this;
        localDataManager = AuthoritiData_.getInstance_(this);

    }

    @UiThread
    protected void displayProgressDialog(String message) {
//        progress = new ProgressDialog(mActivity);
//        progress.setMessage(message);
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.setIndeterminate(true);
//        progress.setCancelable(false);
//        if (!isFinishing()) {
//            progress.show();
//        }


        if (!isFinishing() && (kProgressHUD == null || !kProgressHUD.isShowing())) {
            kProgressHUD = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setBackgroundColor(Color.rgb(224, 230, 233))
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }
    }

    @UiThread
    protected void displayProgressDialog(Activity activity, String message) {
//        progress = new ProgressDialog(activity);
//        progress.setMessage(message);
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.setIndeterminate(true);
//        progress.setCancelable(false);
//        if (!isFinishing()) {
//            progress.show();
//        }

        if (!isFinishing() && (kProgressHUD == null || !kProgressHUD.isShowing())) {
            kProgressHUD = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setBackgroundColor(Color.rgb(224, 230, 233))
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
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

        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }

    @UiThread
    protected void showAlert(String title, String message) {
        if (!isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            try {
                alertDialog.show();
            } catch (Exception e) {

            }
        }
    }

    @UiThread
    protected void showAlert(Context context, String title, String message) {
        if (!isFinishing()) {
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            try {
                alertDialog.show();
            } catch (Exception e) {

            }
        }
    }

    @UiThread
    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @UiThread
    protected void setupUI(final View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    view.clearFocus();
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        startTimer();
        super.onResume();
    }

    @Override
    protected void onPause() {
        finishTimer();
        super.onPause();
    }

    @Override
    public void onUserInteraction() {
        restartTimer();
        super.onUserInteraction();
    }

    private class LogOutTimerTask extends TimerTask {
        @Override
        public void run() {
            if (!isFinishing())
                logOut();
        }
    }

    private void logOut() {
        if (localDataManager.loginStatus()!=null && localDataManager.loginStatus().isLogin()) {
            AuthLogIn logIn = localDataManager.loginStatus();
            logIn.setLogin(false);
            localDataManager.setAuthLogin(logIn);
            Intent intent = new Intent(this, LoginActivity_.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private void startTimer() {
        if (localDataManager.loginStatus()!=null && localDataManager.loginStatus().isLogin()) {
            timer = new Timer();
            LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
            timer.schedule(logoutTimeTask, INACTIVITY_TIME_OUT * 1000);
        }
    }

    private void finishTimer() {
        if (localDataManager.loginStatus()!=null && localDataManager.loginStatus().isLogin()) {
            if (timer != null) {
                timer.cancel();
                Log.i("Main", "cancel timer");
                timer = null;
            }
        }
    }

    private void restartTimer() {
        if (localDataManager.loginStatus()!=null && localDataManager.loginStatus().isLogin()) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();
            LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
            timer.schedule(logoutTimeTask, INACTIVITY_TIME_OUT * 1000);
        }
    }
}
