package net.authoriti.authoriti.ui.share;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.gson.Gson;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.WebAppInterface;
import net.glxn.qrgen.android.QRCode;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


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
