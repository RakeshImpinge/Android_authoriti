package net.authoriti.authoriti.ui.share;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.sjl.foreground.Foreground;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.AuthoritiAPI;
import net.authoriti.authoriti.api.model.AccountID;
import net.authoriti.authoriti.api.model.AuthLogIn;
import net.authoriti.authoriti.api.model.DefaultValue;
import net.authoriti.authoriti.api.model.Group;
import net.authoriti.authoriti.api.model.Picker;
import net.authoriti.authoriti.api.model.Purpose;
import net.authoriti.authoriti.api.model.SchemaGroup;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.api.model.Value;
import net.authoriti.authoriti.api.model.response.ResponsePolling;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.ui.auth.LoginActivity_;
import net.authoriti.authoriti.ui.code.CodePermissionActivity_;
import net.authoriti.authoriti.ui.menu.AccountChaseFragment_;
import net.authoriti.authoriti.ui.menu.AccountFragment_;
import net.authoriti.authoriti.ui.menu.PurposeFragment_;
import net.authoriti.authoriti.ui.menu.WipeFragment_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.ConstantUtils;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.Log;
import net.authoriti.authoriti.utils.WebAppInterface;
import net.glxn.qrgen.android.QRCode;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


@EActivity(R.layout.activity_export)
public class ExportActivity extends BaseActivity implements WebAppInterface.DataInterface {

    @Bean
    AuthoritiData dataManager;

    @ViewById(R.id.ivQRCode)
    ImageView ivQRCode;

    @ViewById(R.id.webview)
    WebView webView;


    @AfterViews
    void callAfterViewInjection() {
        initWebView();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initWebView() {
        webView.addJavascriptInterface(new WebAppInterface(this, this), "Android");
        //Tell the WebView to enable javascript execution.
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.parseColor("#808080"));

        //Set whether the DOM storage API is enabled.
        webView.getSettings().setDomStorageEnabled(true);

        //setBuiltInZoomControls = false, removes +/- controls on screen
        webView.getSettings().setBuiltInZoomControls(false);

        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);

        webView.getSettings().setAppCacheMaxSize(1024 * 8);
        webView.getSettings().setAppCacheEnabled(true);


        webView.getSettings().setUseWideViewPort(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:getEncodedData('" + userData() + "')");
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }
        });

        // these settings speed up page load into the webview
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.loadUrl("file:///android_asset/index.html");
    }


    @Click(R.id.ivClose)
    void closeButtonClicked() {
        finish();
    }


    private String userData() {
        User user = dataManager.getUser();
        Gson gson = new Gson();
        String userData = gson.toJson(user);
        System.out.println("User Data: " + userData);
        return userData;
    }

    @Override
    public void resultData(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int width = ivQRCode.getMeasuredWidth();
                int height = ivQRCode.getMeasuredHeight();
                if (width > height) {
                    width = height;
                } else {
                    height = width;
                }
                ivQRCode.setImageBitmap(QRCode.from(data).withSize(width, height).bitmap());
            }
        });
    }
}
