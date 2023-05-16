package com.milk.milkcollectionapp.helper;

/**
 * Created by sanjay on 27/05/18.
 */

//


import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.milk.milkcollectionapp.Database.MilkDBHelpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;

import static com.milk.milkcollectionapp.Activity.MainActivity.dismiss;
import static com.milk.milkcollectionapp.Activity.MainActivity.instace;


public  class DownloadFile extends AsyncTask<String, String, String> {

    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // showDialog(progress_bar_type);
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {


            URL url = new URL(f_url[0] + "western");
            URLConnection conection = url.openConnection();
            conection.connect();

            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);


            String outputFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator + "western.db";

            OutputStream output = new FileOutputStream(outputFile);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;

                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }

            dismiss();
            output.flush();

            // closing streams
            output.close();
            input.close();

            Log.e("Foile downloaded: ", outputFile);
            importDB();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            dismiss();
        }

        return null;
    }


    public void importDB() {


        String outputFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + File.separator + "western.db";
        File sourceFile = new File(outputFile);



        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;

        String db_name = MilkDBHelpers.DATABASE_NAME;
        String backupDBPath = "/data/" + instace.getPackageName() + "/databases/" + db_name;
        File backupDB = new File(data, backupDBPath);

        Log.e("path-11",backupDBPath);

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(instace, "Downloaded Old data", Toast.LENGTH_SHORT).show();

            Log.e("path-12",backupDBPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        //pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        // dismissDialog(progress_bar_type);

    }

}

