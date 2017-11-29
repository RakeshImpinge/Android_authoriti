package com.curtisdigital.authoriti;

import android.app.Application;
import android.content.Context;

/**
 * Created by mac on 11/25/17.
 */

public class AuthoritiApplication extends Application {

    private Context context;

    public Context getContext() {
        return context;
    }

    @Override public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }
}
