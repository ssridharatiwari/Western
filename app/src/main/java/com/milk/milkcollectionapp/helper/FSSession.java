package com.milk.milkcollectionapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.milk.milkcollectionapp.Activity.MainActivity;
import com.milk.milkcollectionapp.Database.MilkDBHelpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static com.milk.milkcollectionapp.Activity.MainActivity.instace;


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
        MainActivity.getInstace().print("");
        ///  sendDataToPort(printString);

    }



    static public String reverceDate (String date){

        Log.e("date =--",date);

        String replaceDate = date.replace("/", "");

        String dd = replaceDate.substring(0, 2);
        String mm = replaceDate.substring(2, 4);
        String yy = replaceDate.substring(4, 8);

        return yy + mm + dd;

    }


    static public void importDB() {


        String outputFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + File.separator + "western.db";
        File sourceFile = new File(outputFile);

        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;

        String db_name = MilkDBHelpers.DATABASE_NAME;
        String backupDBPath =  "/data/" + instace.getPackageName() + "/databases/" + db_name;

        File backupDB = new File(data, backupDBPath);



        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(instace, "Downloaded Old data", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


