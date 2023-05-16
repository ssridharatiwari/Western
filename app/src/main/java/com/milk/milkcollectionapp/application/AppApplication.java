package com.milk.milkcollectionapp.application;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.milk.milkcollectionapp.retrofit.RestAdapter;
import com.milk.milkcollectionapp.retrofit.RestInterface;


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

       /* registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });*/
    }

    public static AppApplication getAppInstance() {
        return application;
    }

    /**
     * get ApiHelper Component for this application,
     */
    private RestAdapter apiHelper;

    private synchronized RestAdapter getServerBackend() {
        if (apiHelper != null)
            return apiHelper;
        try {
            return (apiHelper = new RestAdapter());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("oopp", "Invalid Api Definitions");
            throw new RuntimeException("Invalid Api Definitions");
        }
    }

    public RestInterface getServerBackend(RestAdapter.AuthType type) {
        if (type.equals(RestAdapter.AuthType.AUTHARIZED))
            return getServerBackend().getAuthRestInterface();
            //   else (type.equals(RestAdapter.AuthType.UNAUTHARIZED))
        else
            return getServerBackend().getRestInterface();
    }

}