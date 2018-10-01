package net.authoriti.authoriti;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.sjl.foreground.Foreground;

import net.authoriti.authoriti.utils.ConstantUtils;

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

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        context = getApplicationContext();
        Foreground.init(this);

//        System.out.println("BASE_URL: " + ConstantUtils.encrypt("https://api.authoriti.net"));
    }
}
