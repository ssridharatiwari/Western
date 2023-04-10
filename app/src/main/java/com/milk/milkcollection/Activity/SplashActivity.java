package com.milk.milkcollection.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.FSSession;
import com.milk.milkcollection.helper.SharedPreferencesUtils;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    private FSSession fsSession;

    private TextView label;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        label = (TextView) findViewById(R.id.textView);



        label.setText("sanjay");

        showValue();


        fsSession = new FSSession(SplashActivity.this);
        if(fsSession.getSData("language").equals("hi")){

            Log.e(" hi ", "hi");

            setLocale("hi");
        }

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(4000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActivity.this,PinActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
                }
            }
        };
        timerThread.start();

    }

    public void setLocale(String lang) {
        Log.d("lang1", lang);
        Locale myLocale = new Locale(lang);
        Log.d("lang", lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    void showValue() {


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override

            public void run() {
                SharedPreferencesUtils shared= new SharedPreferencesUtils(SplashActivity.this);

                label.setText(shared.getVenderName() + "\n" + shared.getVPhone() );
             }

        }, 100);

    }
}