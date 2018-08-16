package com.milk.milkcollection.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.milk.milkcollection.Activity.MainActivity;

import java.io.IOException;


public class FSSession {
    SharedPreferences preferences;

    public FSSession(Context context) {
        preferences = context.getSharedPreferences("MilkCollection", Context.MODE_PRIVATE);

    }


    public void saveData(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public void saveDataBoolen(String key, Boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public void saveDataInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }




    public String getSData(String key) {

        return preferences.getString(key, "");

    }


    public Boolean getBData(String key) {

        return preferences.getBoolean(key, false);

    }

    public int getIntData(String key) {

        return preferences.getInt(key,0);

    }



    public void removeData() {

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

    }


    public void print(String print) {
        try {
            MainActivity.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ///  sendDataToPort(printString);

    }



}





