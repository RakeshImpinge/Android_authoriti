package net.authoriti.authoriti.ui.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
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
import android.widget.Toast;

import com.google.zxing.Result;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import net.authoriti.authoriti.R;
import net.authoriti.authoriti.core.BaseActivity;
import net.authoriti.authoriti.ui.alert.AccountPasswordDialog;
import net.authoriti.authoriti.ui.auth.LoginActivity_;
import net.authoriti.authoriti.utils.AuthoritiData;
import net.authoriti.authoriti.utils.AuthoritiData_;
import net.authoriti.authoriti.utils.Constants;
import net.authoriti.authoriti.utils.WebAppInterface;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.tozny.crypto.android.AesCbcWithIntegrity.BASE64_FLAGS;


public class ImportActivity extends BaseActivity implements ZXingScannerView.ResultHandler, WebAppInterface.DataInterface, View.OnClickListener, AccountPasswordDialog.AccountPasswordDialogListener {

    AuthoritiData authoritiData;
    private ZXingScannerView mScannerView;
    WebView webView;
    AccountPasswordDialog dialog;
    ImageView ivClose;
    String imported_data = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        mScannerView = (ZXingScannerView) findViewById(R.id.ZXingScannerView);
        authoritiData = AuthoritiData_.getInstance_(getApplicationContext());
        webView = (WebView) findViewById(R.id.webview);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        ivClose.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


    @Override
    public void handleResult(final Result rawResult) {
        if (rawResult.getText().length() > 10) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayProgressDialog("Importing data");
//                    initWebView("%21N%27G%28%3DWg%24aSO%25%5DqBCFVO%5C%3DcG95Q%3BguI%5BQ4Np%5C1D.j%29ejRF8u%3Ad4V%23%2BoQ8%23YZ1%21lBb7%3A%3DAg3lgoK_5%27HlFXF%29B%2B-%4075Dh%25%3B%5C%5BHg%24Fi%5E5%25p3%5CS_%28%21%5DkV9UW-SGkl%28M4FDKn%28Fgo%3FrO7i%40ZQ%28Uh%3Cl*Y33%3D%3Aqb14bd9M-H5M04SF%2Bk3LIFSMLlpG24%3Aj%3CCJ%3C%5B%40n%25RYPq%23m%3Dd0%23MD6%3AQkXf%2B4IBd%5Cf%2CYcLs%40A%3Fj%24%26%5EKn_%21_Y%24Is1la%5CC%3BThlYGFoCUS%2F6GAr%29%22d%3AE%3B%28I%28*uAs_UQkCuMpo%3CK.Sg%603uY%22%23%3DrrqUO%3FP5%3E3*3%25L%22DF79%3Bdrgt_A%3EWL%22%22%60%60NjGG%3EtXIn%3Dm%40AR_%5C*%5Bd%3B%5Em7t%3BflGgFEMOX0JPNe%21%2665%2F%2B14ZPQY%26R%5C%5C%5Eo%6017.%29%21W%3EI8Gs3s%28fqi%24EL%22RbE%40%22G%5E%22mkeIboJI%26%60VBV6d%2C%26Nc%28F4");
                    System.out.println("Raw Result: " + rawResult.getText());
                    initWebView(Base64.encodeToString(rawResult.getText().getBytes(), Base64.DEFAULT));
                }
            });
        } else {
            Toast.makeText(mContext, "Unable to scan. Trying Again", Toast.LENGTH_SHORT).show();
            mScannerView.resumeCameraPreview(this);
        }
    }

    public String myEncode(String result) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result = URLEncoder.encode(result, "UTF-8");
            } else {
                result = URLEncoder.encode(result);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    private void initWebView(final String result) {
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
                webView.loadUrl("javascript:getJsonData('" + result + "')");
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

    @Override
    public void resultData(String data) {
        dismissProgressDialog();
        if (data != null && !data.equals("")) {
            imported_data = data;
            showAccountPasswordDialog();
        } else {
            Toast.makeText(mContext, "Unable to scan please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivClose:
                onBackPressed();
                break;
        }
    }

    private void showAccountPasswordDialog() {
        if (dialog == null) {
            dialog = new AccountPasswordDialog(ImportActivity.this);
            dialog.setListener(this);
        } else {
            dialog.init();
        }
        if (!isFinishing() && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void hideAccountPasswordDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    @Override
    public void accountPasswordDialogOKButtonClicked(String password) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(imported_data);
            if (jsonObject != null) {
                String identifier = jsonObject.getString("identifier");
                identifier = decode(identifier, password);
                if (identifier.equals(Constants.IDENTIFIER)) {
                    String privateKey = decode(jsonObject.getString("encryptPrivateKey"), password);
                    String userSalt = decode(jsonObject.getString("encryptSalt"), password);
                    String salt = AesCbcWithIntegrity.saltString(AesCbcWithIntegrity.generateSalt());
                    AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword(password, salt);
                    jsonObject.put("encryptPrivateKey", AesCbcWithIntegrity.encrypt(privateKey, keys).toString());
                    jsonObject.put("encryptPassword", AesCbcWithIntegrity.encrypt(password, keys).toString());
                    jsonObject.put("encryptKey", AesCbcWithIntegrity.keyString(keys));
                    authoritiData.setUserJson(jsonObject);
                    LoginActivity_.intent(getApplicationContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).start();
                } else {
                    Toast.makeText(mContext, "Invalid Qr Code", Toast.LENGTH_SHORT).show();
                    hideAccountPasswordDialog();
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            } else {
                Toast.makeText(mContext, "Unable to scan please try again", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Unable to scan please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private String decode(String ciphervalue, String password) {
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKeyFromPassword
                    (password, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac_d = new AesCbcWithIntegrity
                    .CipherTextIvMac(ciphervalue);
            return AesCbcWithIntegrity.decryptString(cipherTextIvMac_d, keys);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void accountAddDialogCancelButtonClicked() {
        hideAccountPasswordDialog();
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
