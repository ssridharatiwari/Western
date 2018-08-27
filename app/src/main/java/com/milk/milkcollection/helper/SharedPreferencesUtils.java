package com.milk.milkcollection.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.R;

import java.io.IOException;

/**
 * Created by Parasme on 4/21/2016.
 */
public class SharedPreferencesUtils {
private Context _context;


    public static SharedPreferencesUtils shared;

    public SharedPreferencesUtils() {

    }


    public SharedPreferencesUtils getInstace(){
        if(shared == null){
            shared = new SharedPreferencesUtils();
        }
        return shared;
    }

SharedPreferences prefs;
        public SharedPreferencesUtils(Context _context) {
            this._context = _context;
            prefs = this._context.getSharedPreferences("FindFriends",Context.MODE_PRIVATE);
        }


   public void setTitle(String settitle) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("scotitle", settitle);
        editor.commit();
    }


    public void printBy(String printBy) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("printBy", printBy);
        editor.commit();
    }


    public String getprintBy() {
        if(prefs.contains("printBy"))
            return prefs.getString("printBy", "");
        else
            return "wifi";
    }

    public String getprintByText() {
        if(prefs.contains("printBy"))
        {
            String printBefore =  prefs.getString("printBy", "");

            if (printBefore.equals("wifi"))
                return "Wifi Printer";
        else if (printBefore.equals("blutooth"))
            return "Blutooth Printer App";
        else if (printBefore.equals("pos"))
            return "Pos App";
        else
                return "Derect Wifi Printer";

        }
        else
            return "Derect Wifi Printer";
    }


    public void applyRateMethod(String rateBy) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("rateby", rateBy);
        editor.commit();
    }
  // 1 - manual fate rat
  // 2 -  fat snf rate chart
  // 3 - manula fate clr rate chart


    public String getRateMethodText() {
        if(prefs.contains("rateby"))
        {

            String dat = prefs.getString("rateby", "");

            if (dat.equals("1")) {
                dat = MainActivity.instace.getResources().getString(R.string.kgfat_rate);
            }

            else if (dat.equals("2")) {
                dat = "Fat Snf Chart";
            }
            else if (dat.equals("3")) {
                dat = "Fat Clr Chart";
            }

            return dat;
        }
        else
            return "Manual Fat Snf Rate";
    }


    public String getImported() {
        if(prefs.contains("imported"))
            return prefs.getString("imported", "");
        else
            return "0";
    }

    public void setImported()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("imported", "1");
        editor.commit();
    }



    public String getRateMethodCode() {
        if(prefs.contains("rateby"))
            return prefs.getString("rateby", "");
        else
            return "1";
    }

    public String getTitle() {
        if(prefs.contains("scotitle"))
            return prefs.getString("scotitle", "");
        else
            return "null";
    }




    public void setMobile(String settitle)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mobile", settitle);
        editor.commit();
    }

    public String getMobile()
    {
        if(prefs.contains("mobile"))
            return prefs.getString("mobile", "");
        else
            return "null";
    }


    public void setIsDemoTrue() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("isDemo", "1");
        editor.commit();
    }
    public void setIsDemoFalse() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("isDemo", "0");
        editor.commit();
    }


    public void setDemoDate(String date) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("demodate", date);
        editor.commit();
    }
    public String getDemoDate() {
        if(prefs.contains("isDemo"))
            return prefs.getString("demodate", "");
        else
            return "";
    }

    public String getISDemo() {
        if(prefs.contains("isDemo"))
            return prefs.getString("isDemo", "");
        else
            return "0";
    }




    public void setUserId(String userId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userID", userId);
        editor.commit();
    }

    public String getUserID() {
        if(prefs.contains("userID"))
            return prefs.getString("userID", "");
        else
            return "0";
    }



    public void setDmeoCount(int count) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("demoCount", count);
        editor.commit();
    }

    public int getDemoCount() {
        if(prefs.contains("demoCount"))
            return prefs.getInt("demoCount", 1);
        else
            return 0;
    }



    public void setFromDateData(String date) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("setFromDateData", date);
        editor.commit();
    }



    public void setLastDateData(String date) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("setLastDateData", date);
        editor.commit();
    }

    public String getfromDate () {

        if(prefs.contains("setFromDateData"))
            return prefs.getString("setFromDateData", "");
        else
            return "";
    }


    public String getLastDate () {

        if(prefs.contains("setLastDateData"))
            return prefs.getString("setLastDateData", "");
        else
            return "";
    }



    public void setDefaultSNF(float snf) {


        try {
            float value = Float.parseFloat(MainActivity.twoDecimalFloatToString(snf));
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("defaultSNF", value);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public float getDefaultSNF() {
        if(prefs.contains("defaultSNF"))
            return prefs.getFloat("defaultSNF", (float) 0.0);
        else
            return 0;
    }






    public void setIsDownloaded() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("isDownloaded", "1");
        editor.commit();
    }



    public String isDownloaded() {
        if(prefs.contains("isDownloaded"))
            return prefs.getString("isDownloaded", "0");
        else
            return "0";
    }
}