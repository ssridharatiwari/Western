package com.milk.milkcollectionapp;

import android.app.Application;
import android.content.Context;

import com.milk.milkcollectionapp.utils.blutoothcommunication.SimplePreference;
import com.milk.milkcollectionapp.utils.AppUrl;

public class AppApplication extends Application {
    static AppApplication application;
    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        preference = new SimplePreference(this);
        GetBaseUrl();
    }

    public static AppApplication getInstance() {
        return application;
    }

    private void GetBaseUrl() {
        if (BuildConfig.FLAVOR.equals("milkcollection")) {
            AppUrl.mainUrl = "http://wokosoftware.com/western/index6.php?";
        }else {
            AppUrl.mainUrl = "http://wokosoftware.com/western/index43.php?";
        }
    }

    private static SimplePreference preference;
    public static SimplePreference getPreference() {
        return preference;
    }
}