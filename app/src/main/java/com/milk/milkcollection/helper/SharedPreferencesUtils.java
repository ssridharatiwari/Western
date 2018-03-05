package com.milk.milkcollection.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Parasme on 4/21/2016.
 */
public class SharedPreferencesUtils {
private Context _context;
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
                return "Derect Wifi Printer";
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
                dat = "Manual Fat Snf Rate";
            }

            else if (dat.equals("2")) {
                dat = "Fat Snf Rate Chart";
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
}