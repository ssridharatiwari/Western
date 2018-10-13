package com.milk.milkcollection.helper;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.milk.milkcollection.Activity.MainActivity;
import com.milk.milkcollection.Database.MilkDBHelpers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

import static com.milk.milkcollection.Activity.MainActivity.dismiss;
import static com.milk.milkcollection.Activity.MainActivity.instace;
import static com.milk.milkcollection.Activity.MainActivity.makeToast;

/**
 * Created by sanjay on 27/05/18.
 */

 public  class UploadFile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {

                String db_name = "western";

                File data = Environment.getDataDirectory();
                FileChannel source = null;
                FileChannel destination = null;

                String currentDBPath = "/data/" + instace.getPackageName() + "/databases/" + MilkDBHelpers.DATABASE_NAME;

                File sourceFile = new File(data, currentDBPath);
                Log.e("url file" , String.valueOf(sourceFile.getPath()));


                if (sourceFile.exists()) {

                    try {

                        HttpURLConnection conn = null;
                        DataOutputStream dos = null;
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary = "*****";
                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 1 * 1024 * 1024;

                        Log.e("url file" , String.valueOf(sourceFile.getPath()));

                        SharedPreferencesUtils unit = new SharedPreferencesUtils(instace);


                        String upLoadServerUri =  AppUrl.mainUrl + "action=11&id=" + unit.getUserID();

                        Log.e("url" , upLoadServerUri);

                        // open a URL connection to the Servlet
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL(upLoadServerUri);

                        // Open a HTTP connection to the URL
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true); // Allow Inputs
                        conn.setDoOutput(true); // Allow Outputs
                        conn.setUseCaches(false); // Don't use a Cached Copy
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("userfile", db_name);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\""
                                + db_name + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        // read file and write it into form...
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);

                        }

                        // send multipart form data necesssary after file
                        // data...
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);

                        // Responses from the server (code and message)
                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn
                                .getResponseMessage();

                        Log.e("responc message" ,serverResponseMessage);
                        Log.e("responc message" , String.valueOf(serverResponseCode));


                        if (serverResponseCode == 200) {

                            makeToast("Data Uploaded ");

                        }

                        // close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();

                    } catch (Exception e) {


                       MainActivity.getInstace().dismiss();

                        e.printStackTrace();

                    }

                    MainActivity.getInstace().dismiss();

                } else{
                    MainActivity.getInstace().dismiss();
                }


            } catch (Exception ex) {
                MainActivity.getInstace().dismiss();

                ex.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


