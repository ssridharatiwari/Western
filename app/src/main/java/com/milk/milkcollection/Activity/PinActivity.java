package com.milk.milkcollection.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.milk.milkcollection.R;
import com.milk.milkcollection.helper.AppString;
import com.milk.milkcollection.helper.AppUrl;
import com.milk.milkcollection.helper.DownloadFile;
import com.milk.milkcollection.helper.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.milk.milkcollection.Activity.MainActivity.dismiss;
import static com.milk.milkcollection.Activity.MainActivity.instace;
import static java.lang.System.exit;


public class PinActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 123;

    private String match;
    private EditText pinCode1;
    private TextView btnVerify, emi, btnSend,btnOldCustomer;
    ProgressDialog dialog;
    EditText title, mobile;
    String settitle;
    SharedPreferencesUtils sharedPreferencesUtils;
    public String pin1, mobile_string, verifypin, imeiNumber = "", sim_no = "0", RandomNumber = "0", AndroidID = "0", User_id = "";

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;


    public static PinActivity instace;

    public static PinActivity getInstace(){
        if(instace == null){
            instace = new PinActivity();
        }
        return instace;
    }

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);


        instace = PinActivity.this;

        sharedPreferencesUtils = new SharedPreferencesUtils(PinActivity.this);

        getDeviceIDS();

        genRandomNo();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
         String isLogin = preferences.getString("isLogin", "");






         if (isLogin.equals("1") || isLogin.equals("2") ){

            startActivity(new Intent(PinActivity.this, MainActivity.class));
            finish();
        }
        else {

            getDeviceIDS();

            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showLoading("Wait...");
                    hideSoftKeyBoard();
                    PinActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            verifyDetailApi();
                        }
                    });

                }
            });

             btnOldCustomer.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     OldCustomer();
                 }
             });


            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pin1 = pinCode1.getText().toString();
                    settitle = title.getText().toString().trim();
                    mobile_string = mobile.getText().toString();
                    verifypin = pin1;

                    if (settitle.length() > 0 && mobile_string.length() > 0) {

                        if (mobile_string.length() > 9) {

                            if (!AndroidID.equals("0")){
                                showLoading("Sending...");
                                hideSoftKeyBoard();
                                PinActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            makeRequest();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                            else {
                                Toast.makeText(PinActivity.this, "Device ID NOT genrated", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(PinActivity.this, "सही मोबाइल नंबर भरें", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(PinActivity.this, "कृपया नाम और मोबाइल भरें ", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    public void showDialoag() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PinActivity.this);
        builder.setTitle("Send Detail For Activation");

        builder.setItems(new CharSequence[]
                        {"Whats App", "SMS", "Cancel"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                sendWhatsApp();
                                btnSend.setText("Recend SMS");
                                break;
                            case 1:
                                sendSMS();
                                btnSend.setText("Recend SMS");
                                break;
                            case 2:

                                break;
                        }
                    }
                });
        builder.create().show();
    }

    @SuppressLint("HardwareIds")
    private void getDeviceIDS() {
        try {

            try {

                if (isPermissionGranted(PinActivity.this)){

                    AndroidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

                    // Check if the READ_PHONE_STATE permission is already available.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_PHONE_STATE)) {
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    } else {

                        AndroidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

                        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        assert mngr != null;
                        imeiNumber = mngr.getDeviceId();

                        emi.setText("Device - " + AndroidID);

                        // READ_PHONE_STATE permission is already been granted.
                        //  Toast.makeText(this, "Alredy granted", Toast.LENGTH_SHORT).show();
                    }
                }




            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            // Toast.makeText(PinActivity.this, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


                TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                assert mngr != null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                AndroidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                imeiNumber = mngr.getDeviceId();
                emi.setText("Device - " + AndroidID);
                getDeviceIDS();


    }

    private void sendSMS() {
        try {
            String message = settitle + "\n" + mobile_string + "\nIMEI " + imeiNumber + "\nAndroid ID " + AndroidID + "\nCalcutaion No. " + RandomNumber;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+919829240615", null, message, null, null);
            Toast.makeText(PinActivity.this, "SMS Sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(PinActivity.this, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void sendWhatsApp() {
        try {

            String message = settitle + "\n" + mobile_string + "\nIMEI " + imeiNumber + "\nAndroid ID " + AndroidID + "\nCalcutaion No. " + RandomNumber;

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            PackageManager pm = PinActivity.this.getPackageManager();

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (Exception e) {
            Toast.makeText(PinActivity.this, "Whats App Not installed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void startNewActivity(){

        startActivity(new Intent(PinActivity.this, MainActivity.class));
        sharedPreferencesUtils.setTitle(settitle);
        sharedPreferencesUtils.setMobile(mobile_string);
        sharedPreferencesUtils.printBy("wifi");

        finish();

    }


    public void showLoading(String message){
        progress = new ProgressDialog(this);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.show();
    }



    private void makeRequest() throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url =  AppUrl.mainUrl + "name=" + settitle +
                     "&sim_id=" + sim_no +
                     "&android_id=" + AndroidID +
                     "&iemi_id=" + imeiNumber +
                     "&mobile=" + mobile_string +
                     "&action=1"  ;


        url = url.replaceAll(" ", "%20");

        Log.e("final ulr" , url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            Log.e("resonce", response);

                            if (response.length()>0) {
                                JSONObject jsonObject = new JSONObject(response);
                                // User_id =  jsonObject.getString("id").toString();
                                makeToast(jsonObject.getString("message").toString());
                            }else{
                                makeToast("Responce Error");
                            }
                        } catch (JSONException e) {
                            makeToast("Responce Error");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progress.dismiss();
                makeToast("Network Error");

                // Log.e("Not working ",error.getMessage());
            }
        });
        queue.add(stringRequest);
    }


    private void verifyDetailApi(){


        if (!isNetworkConnected()){
            makeToast("Internet Required");
            progress.dismiss();
            return;
        }


        if (AndroidID.length() > 0) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppUrl.mainUrl + "android_id=" + AndroidID + "&action=2";

        Log.e("final ulr", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            Log.e("resonce", response);

                            if (response.length() > 0) {

                                JSONObject jsonObject = new JSONObject(response);

                                String jsonStatus = jsonObject.getString("status").toString();

                                if (jsonStatus.equals("401")){

                                    makeToast("Submit Your Detail first");


                                }else if (jsonStatus.equals("200")) {

                                    jsonObject = jsonObject.getJSONObject("details");

                                    String userStatus = jsonObject.getString("status").toString();

                                    String userID = jsonObject.getString("id").toString();

                                    if (userID != "") {
                                        sharedPreferencesUtils.setUserId(userID);
                                    }
                                    if (!userStatus.equals("")){
                                        setStatus(userStatus);
                                    }

                                    if (userStatus.equals("1")){
                                        makeToast("Thank you : registration successful");
                                        settitle = jsonObject.getString("name").toString();
                                        mobile_string = jsonObject.getString("mobile").toString();

                                        startNewActivity();


                                    }else if (userStatus.equals("2")){

                                        sharedPreferencesUtils.setIsDemoTrue();

                                        String demo_date = jsonObject.getString("demo_date").toString();
                                        sharedPreferencesUtils.setDemoDate(demo_date);
                                        makeToast("Demo App");
                                        settitle = "DEMO";
                                        mobile_string = "DEMO";
                                        startNewActivity();

                                    }else {
                                        makeToast("You are not verified");
                                    }

                                }else{

                                }
                            } else {
                                makeToast("Network Error");
                            }
                        } catch (JSONException e) {
                            makeToast("Network Error");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                makeToast("Detail sending failed : network or server error");
               // Log.e("Not working ", error.getMessage());
            }
        });
        queue.add(stringRequest);

    }else{
        progress.dismiss();
        makeToast("Send Detail First");
    }
    }


    public void setStatus (String status){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isLogin", status);
        editor.commit();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    public static boolean isPermissionGranted(Activity activity) {

        if (Build.VERSION.SDK_INT >= 23) {




            if ((activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)  &&
                    (activity.checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED)){
                Log.e("Permission", "Permission is granted");
                return true;
            } else {

                Log.e("Permission", "Permission is revoked");
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET}, REQUEST_CODE);


                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.e("Permission", "Permission is granted");
            return true;
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */


    public void genRandomNo() {


        RandomNumber = "12344353";

        match = RandomNumber;


        emi = (TextView) findViewById(R.id.emi);
        btnVerify = (TextView) findViewById(R.id.btnVerify);
        btnSend = (TextView) findViewById(R.id.btnSend);
        // pinCode1 = (EditText) findViewById(R.id.pinCode1);
        btnOldCustomer = (TextView) findViewById(R.id.btnOldCustomer);


        title = (EditText) findViewById(R.id.title);
        mobile = (EditText) findViewById(R.id.mobile);

    }

    public void makeToast(String message){
        Toast.makeText(PinActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void OldCustomer(){



        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(PinActivity.this);

        final EditText edittext = new EditText(PinActivity.this);

        edittext.setInputType( InputType.TYPE_NUMBER_FLAG_SIGNED );
        edittext.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        alert.setMessage("");
        alert.setTitle("Customer Code");

        alert.setView(edittext);

        final android.support.v7.app.AlertDialog.Builder ok = alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public String toString() {
                return "$classname{}";
            }

            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value


                String YouEditTextValue = edittext.getText().toString();
                userID =  YouEditTextValue;

                if ( Float.parseFloat(YouEditTextValue) > 0.0) {

                    verifyOLDUSER();

                } else {


                }


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();


    }


    String userID = "";




    private void verifyOLDUSER(){


        if (!isNetworkConnected()){
            makeToast("Internet Required");
            return;
        }

        showLoading("Wait...");

        RequestQueue queue = Volley.newRequestQueue(PinActivity.this);
        String url =  AppUrl.mainUrl + "uid=" + userID + "&action=9&android_id=" + AndroidID;

        Log.e("final ulr", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                          progress.dismiss();

                        try {

                            Log.e("resonce", response);

                            if (response.length() > 0) {


                                Log.e("responce ",response);


                                JSONObject jsonObject = new JSONObject(response);

                                String jsonStatus = jsonObject.getString("status").toString();

                                if (jsonStatus.equals("200")) {

                                    jsonObject = jsonObject.getJSONObject("details");
                                    // sharedPreferencesUtils.setIsDownloaded();


                                    String userStatus = jsonObject.getString("status").toString();

                                    String userID = jsonObject.getString("id").toString();

                                    if (userID != "") {
                                        sharedPreferencesUtils.setUserId(userID);
                                    }
                                    if (!userStatus.equals("")){
                                        setStatus("1");
                                    }

                                    makeToast("Welcome Back");
                                    settitle = jsonObject.getString("name").toString();
                                    mobile_string = jsonObject.getString("mobile").toString();

                                    startNewActivity();

                                }else{

                                    makeToast(jsonObject.getString("message").toString());

                                }




                            } else {
                                //   MainActivity.showToast("Network Error");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();

                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

    }


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}







