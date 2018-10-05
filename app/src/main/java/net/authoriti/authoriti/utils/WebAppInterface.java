package net.authoriti.authoriti.utils;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {


    Context mContext;
    DataInterface dataInterface;

    public WebAppInterface(Context mContext, DataInterface dataInterface) {
        this.mContext = mContext;
        this.dataInterface = dataInterface;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public void Data(String data) {
        dataInterface.resultData(data);
    }

    public interface DataInterface {
        public void resultData(String data);
    }

}
