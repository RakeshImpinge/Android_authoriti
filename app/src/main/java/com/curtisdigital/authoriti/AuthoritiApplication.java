package com.curtisdigital.authoriti;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.sjl.foreground.Foreground;

import org.androidannotations.annotations.EApplication;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mac on 11/25/17.
 */

@EApplication
public class AuthoritiApplication extends Application {

    private Context context;

    public Context getContext() {
        return context;
    }

    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        context = getApplicationContext();

        Foreground.init(this);
    }
}
