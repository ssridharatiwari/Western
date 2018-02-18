package com.milk.milkcollection.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
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
import com.milk.milkcollection.helper.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PinActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 123;

    private String match;
    private EditText pinCode1;
    private TextView btnVerify, emi, btnSend;
    ProgressDialog dialog;
    EditText title, mobile;
    String settitle;
    SharedPreferencesUtils sharedPreferencesUtils;
    String pin1, mobile_string, verifypin, imeiNumber = "",sim_no = "", deviceID = "", RandomNumber = "", AndroidID = "" , User_id = "";
    int tagLogin = 0;

    public static String deviceEmiNumber = "9114517502176099";

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        sharedPreferencesUtils = new SharedPreferencesUtils(PinActivity.this);

        getDeviceIDS();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (preferences.getBoolean("isLogin", false)) {

            startActivity(new Intent(PinActivity.this, MainActivity.class));
            finish();
        } else {

            genRandomNo();
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


            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pin1 = pinCode1.getText().toString();
                    settitle = title.getText().toString().trim();
                    mobile_string = mobile.getText().toString();
                    verifypin = pin1;

                    if (settitle.length() > 0 && mobile_string.length() > 0) {

                        if (mobile_string.length() > 9) {

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


    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    private void getDeviceIDS() {
        try {

            String ts = PinActivity.this.TELEPHONY_SERVICE;
            TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(ts);
            AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            try {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    imeiNumber = "0";
                    return;
                }

                switch (imeiNumber = mTelephonyMgr.getSubscriberId()) {
                }
                switch (sim_no = mTelephonyMgr.getLine1Number()) {
                }


            } catch (Exception e) {e.printStackTrace();}
            } catch (Exception e) {
               // Toast.makeText(PinActivity.this, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLogin", true);
        editor.commit();
        finish();

    }

    private void PinLength() {
        pinCode1 = (EditText) findViewById(R.id.pinCode1);
    }


    public void showLoading(String message){
        progress = new ProgressDialog(this);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.show();
    }

    private void makeRequest() throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://wokosoftware.com/western/index.php?name=" + settitle +
                     "&sim_id=" + sim_no +
                     "&android_id=" + AndroidID +
                     "&iemi_id=" + imeiNumber +
                     "&mobile=" + mobile_string +
                     "&action=1"  ;

        Log.e("final ulr" , url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            if (response.length()>0) {
                                JSONObject jsonObject = new JSONObject(response);
                                User_id =  jsonObject.getString("id").toString();
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
                makeToast("Detail sending failed : network or server error");

                Log.e("Not working ",error.getMessage());
            }
        });
        queue.add(stringRequest);
    }


    private void verifyDetailApi(){

    if (AndroidID.length() > 0) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://wokosoftware.com/western/index.php?android_id=" + AndroidID + "&action=2";

        Log.e("final ulr", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            if (response.length() > 0) {
                                Log.e("responce ",response);
                                JSONObject jsonObject = new JSONObject(response);
                                jsonObject = jsonObject.getJSONObject("details");
                                String status = jsonObject.getString("status").toString();
                                settitle = jsonObject.getString("name").toString();
                                mobile_string = jsonObject.getString("mobile").toString();


                                if (status.equals("1")){
                                    makeToast("Thank you : registration successful");
                                    startNewActivity();
                                }
                                else
                                    makeToast("You are not verified");
                            } else {
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
                makeToast("Detail sending failed : network or server error");
                Log.e("Not working ", error.getMessage());
            }
        });
        queue.add(stringRequest);

    }else{
        progress.dismiss();
        makeToast("Send Detail First");
    }
    }





    private void postRequest(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://wokosoftware.com/western/index.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();

                try {
                    if (response.length()>0) {
                        JSONObject jsonObject = new JSONObject(response);
                        makeToast(jsonObject.getString("message").toString());
                    }else{
                        makeToast("Detail sending failed : network or server error");
                    }
                } catch (JSONException e) {
                    makeToast("Detail sending failed : network or server error");
                    e.printStackTrace();
                }


                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.dismiss();
                makeToast("Detail sending failed : network or server error");
            }
        }) {
            @Override
            public String getBodyContentType() {
                progress.dismiss();

                return "application/json; charset=utf-8";
            }


            protected Map<String, String> getParams()
            {
                Map<String, String>  jsonBody = new HashMap<String, String>();

                jsonBody.put("name", settitle);
                jsonBody.put("sim_id", sim_no);
                jsonBody.put("android_id", AndroidID);
                jsonBody.put("iemi_id", imeiNumber);
                jsonBody.put("mobile", mobile_string);
                jsonBody.put("action","1");
                return jsonBody;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(stringRequest);
    }

    private void GetCaledarDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int ampm = calendar.get(Calendar.AM_PM);
        String date = String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);

        int sssd = String.valueOf(year).length();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog.dismiss();
    }

    public static boolean isPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) &&
                    (activity.checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)) {
                Log.e("Permission", "Permission is granted");
                return true;
            } else {

                Log.e("Permission", "Permission is revoked");
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS}, REQUEST_CODE);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startNewActivity();
            }
        }
    }

    public void genRandomNo() {

        long gett = (long) Math.floor(Math.random() * (999999999 - 111111111 + 1)) + 111111000;
        gett = gett * 9;
        RandomNumber = String.valueOf(gett);
        final String imsiSIM1 = String.valueOf(gett);
        long matchvale = Long.parseLong(imsiSIM1) / 111615;
        matchvale = matchvale * 99;
        matchvale = matchvale / 23;
        matchvale = matchvale * 74;
        matchvale = matchvale + 453453;
        matchvale = matchvale * 3;

        int val = (int) matchvale;
        String value = String.valueOf(val);

        if (value.length() > 7) {
            for (int i = 0; i < (value.length() - 7); i++) {
                val = val / 10;
            }
        }

        match = String.valueOf(val);

        dialog = new ProgressDialog(PinActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        GetCaledarDate();
        PinLength();

        emi = (TextView) findViewById(R.id.emi);
        emi.setText("Device Number :- " + gett);

        btnVerify = (TextView) findViewById(R.id.btnVerify);
        btnSend = (TextView) findViewById(R.id.btnSend);

        title = (EditText) findViewById(R.id.title);
        mobile = (EditText) findViewById(R.id.mobile);

    }

    public void makeToast(String message){
        Toast.makeText(PinActivity.this, message, Toast.LENGTH_LONG).show();
    }
}



