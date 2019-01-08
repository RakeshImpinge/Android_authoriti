package net.authoriti.authoriti.ui.share;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.api.model.User;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.WebAppInterface;
import net.glxn.qrgen.android.QRCode;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

import static com.tozny.crypto.android.AesCbcWithIntegrity.BASE64_FLAGS;


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
        displayProgressDialog("Please Wait");
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

        String privateKey = "";
        String password = "";
        String salt = "";

        AesCbcWithIntegrity.SecretKeys keys;
        String keyStr = dataManager.getUser().getEncryptKey();
        try {
            keys = AesCbcWithIntegrity.keys(keyStr);
            AesCbcWithIntegrity.CipherTextIvMac civPrivateKey = new AesCbcWithIntegrity.CipherTextIvMac(user.getEncryptPrivateKey());
            AesCbcWithIntegrity.CipherTextIvMac civPassword = new AesCbcWithIntegrity.CipherTextIvMac(user.getEncryptPassword());
            AesCbcWithIntegrity.CipherTextIvMac civSalt = new AesCbcWithIntegrity.CipherTextIvMac(user.getEncryptSalt());

            privateKey = AesCbcWithIntegrity.decryptString(civPrivateKey, keys);
            password = AesCbcWithIntegrity.decryptString(civPassword, keys);
            salt = AesCbcWithIntegrity.decryptString(civSalt, keys);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        user.setEncryptPassword(password);
        user.setEncryptPrivateKey(encrypt(privateKey, password));
        user.setEncryptSalt(encrypt(salt, password));
        String identifier = Constants.IDENTIFIER;
        String encrypted_id = encrypt(identifier, password);
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(gson.toJson(user));
            jsonObject.remove("encryptPassword");
            jsonObject.put("identifier", encrypted_id);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String encrypt(String value, String password) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword
                    (password, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt
                    (value, keys);
            return cipherTextIvMac.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    public void resultData(final String data) {
        dismissProgressDialog();
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
