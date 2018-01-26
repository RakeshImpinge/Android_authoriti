package com.curtisdigital.authoriti.ui.help;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.curtisdigital.authoriti.R;
import com.curtisdigital.authoriti.core.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mac on 1/24/18.
 */

@EActivity(R.layout.activity_help)
public class HelpActivity extends BaseActivity {

    String url;

    @ViewById(R.id.webView)
    WebView webView;

    @AfterViews
    void callAfterViewInjection(){

        url = API_BASE_URL + "/hlep";

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                          }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                         }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                         }
        });

        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

    }

    @Click(R.id.ivClose)
    void closeButtonClicked(){
        finish();
    }
}
