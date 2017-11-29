package com.curtisdigital.authoriti;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

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
        Fabric.with(this, new Crashlytics());

        context = getApplicationContext();
    }
}
