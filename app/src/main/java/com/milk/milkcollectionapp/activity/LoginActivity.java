package com.milk.milkcollectionapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.milk.milkcollectionapp.R;
import com.milk.milkcollectionapp.utils.ShowCustomToast;
import com.milk.milkcollectionapp.utils.apicalling.WebService;
import com.milk.milkcollectionapp.utils.apicalling.WebServiceListener;
import com.milk.milkcollectionapp.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements WebServiceListener {
    private static final int REQUEST_CODE = 123;
    private TextView btnVerify, emi, btnSend, btnOldCustomer;
    private EditText title, mobile, venderCode;
    private String settitle;
    private String mobile_string, imeiNumber = "", sim_no = "0", RandomNumber = "0", loginStatus = "", userStatus, userID;
    public String AndroidID = "0";
    public static LoginActivity instace;
    private Context svContext;
    private ShowCustomToast customToast;
    private SharedPreferencesUtils sharedPreferencesUtils;

    public static LoginActivity getInstace() {
        if (instace == null) {
            instace = new LoginActivity();
        }
        return instace;
    }

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        svContext = this;
        customToast = new ShowCustomToast(LoginActivity.this);
        sharedPreferencesUtils = new SharedPreferencesUtils(LoginActivity.this);
        instace = LoginActivity.this;

        getDeviceIDS();
        RandomNumber = "12344353";
        emi = (TextView) findViewById(R.id.emi);
        btnVerify = (TextView) findViewById(R.id.btnVerify);
        btnSend = (TextView) findViewById(R.id.btnSend);
        btnOldCustomer = (TextView) findViewById(R.id.btnOldCustomer);
        title = (EditText) findViewById(R.id.title);
        mobile = (EditText) findViewById(R.id.mobile);
        venderCode = (EditText) findViewById(R.id.venderCode);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String isLogin = preferences.getString("isLogin", "");
        String vender_name = preferences.getString("vender_name", "");
        String vender_mobile = preferences.getString("vender_mobile", "");

        if (isLogin.equals("1") || isLogin.equals("2")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            getDeviceIDS();
            btnVerify.setOnClickListener(v -> {
                mobile_string = mobile.getText().toString().trim();
                if (mobile_string.length() < 10) {
                    makeToast("mobile number required");
                    return;
                }
                hideSoftKeyBoard();
                LoginActivity.this.runOnUiThread(() -> verifyDetailApi());
            });

            btnOldCustomer.setOnClickListener(v -> OldCustomer());

            btnSend.setOnClickListener(v -> {
                settitle = title.getText().toString().trim();
                mobile_string = mobile.getText().toString();

                if (settitle.length() > 0 && mobile_string.length() > 0) {
                    if (mobile_string.length() > 9) {
                        if (!AndroidID.equals("0")) {
                            hideSoftKeyBoard();
                            String vcode = "0";
                            if (venderCode.getText().toString().length() > 0) {
                                vcode = venderCode.getText().toString();
                            }

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("name", settitle);
                            params.put("sim_id", sim_no);
                            params.put("android_id", AndroidID);
                            params.put("iemi_id", imeiNumber);
                            params.put("mobile", mobile_string);
                            params.put("vender", vcode);
                            params.put("action", "1");
                            strAction = 1;
                            callWebService(WebService.SENDDETAIL, params);
                        } else {
                            Toast.makeText(LoginActivity.this, "Device ID NOT genrated", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "सही मोबाइल नंबर भरें", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "कृपया नाम और मोबाइल भरें ", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getDeviceIDS() {
        AndroidID = "123456";
        imeiNumber = "123456";
    }

    private void startNewActivity() {
        setStatus("1");
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        sharedPreferencesUtils.setTitle(settitle);
        sharedPreferencesUtils.setMobile(mobile_string);
        sharedPreferencesUtils.printBy("blutooth");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    private void callWebService(String[] postUrl, Map<String, String> params) {
        WebService webService = new WebService(LoginActivity.this, postUrl, params, this,
                true, Request.Method.GET);
        webService.LoadData();
    }

    private void verifyDetailApi() {
        if (AndroidID.length() > 0) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("android_id", AndroidID);
            params.put("mobile", mobile_string);
            params.put("action", "2");
            strAction = 2;
            callWebService(WebService.VERIFYDETAILS, params);
        } else {
            progress.dismiss();
            makeToast("Send Detail First");
        }
    }

    public void setStatus(String status) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isLogin", status);
        editor.commit();
    }

    public void setVender(String vname, String vphone, String vid) {
        sharedPreferencesUtils.setVenderName(vname);
        sharedPreferencesUtils.setVPhone(vphone);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void makeToast(String message) {
        customToast.showCustomToast(svContext, message, customToast.ToastyInfo);
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void OldCustomer() {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        final EditText edittext = new EditText(LoginActivity.this);
        edittext.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
        edittext.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setMessage("");
        alert.setTitle("Customer Code");
        alert.setView(edittext);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public String toString() {
                return "$classname{}";
            }

            public void onClick(DialogInterface dialog, int whichButton) {
                String YouEditTextValue = "";
                YouEditTextValue = edittext.getText().toString();
                userID = YouEditTextValue;

                if (Float.parseFloat(YouEditTextValue) > 0.0) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("uid", userID);
                    params.put("android_id", AndroidID);
                    params.put("action", "9");
                    strAction = 9;
                    callWebService(WebService.GETOLDUSERDETAIL, params);
                }
            }
        });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> dialog.dismiss());
        alert.show();
    }

    private int strAction = 1;
    @Override
    public void onWebServiceActionComplete(String result, String url) {
        if (url.contains(WebService.SENDDETAIL[0]) && strAction == 1) {
            try {
                if (result.length() > 0) {
                    JSONObject jsonObject = new JSONObject(result);
                    makeToast(jsonObject.getString("message"));
                } else {
                    makeToast("Responce Error");
                }
            } catch (JSONException e) {
                makeToast("Responce Error");
                e.printStackTrace();
            }
        } else if (url.contains(WebService.VERIFYDETAILS[0]) && strAction == 2) {
            try {
                if (result.length() > 0) {
                    JSONObject jsonObject = new JSONObject(result);
                    String jsonStatus = jsonObject.getString("status");
                    if (jsonStatus.equals("401")) {
                        makeToast("Submit Your Detail first");
                    } else if (jsonStatus.equals("200")) {
                        jsonObject = jsonObject.getJSONObject("details");
                        userStatus = jsonObject.getString("status");
                        loginStatus = jsonObject.getString("login_status_1");
                        if (userStatus.equals("1")) {
                            userID = jsonObject.getString("id").toString();
                            if (userID != "") {
                                sharedPreferencesUtils.setUserId(userID);
                            }

                            makeToast("Thank you : Registration successful");
                            settitle = jsonObject.getString("name");
                            mobile_string = jsonObject.getString("mobile");
                            String vname = jsonObject.getString("vname");
                            String vid = jsonObject.getString("vender_id");
                            String vphone = jsonObject.getString("vphone");
                            setVender(vname, vphone, vid);
                            startNewActivity();

                        } else if (userStatus.equals("2")) {
                            sharedPreferencesUtils.setIsDemoTrue();
                            String demo_date = jsonObject.getString("demo_date").toString();
                            sharedPreferencesUtils.setDemoDate(demo_date);
                            makeToast("Demo App");
                            settitle = "DEMO";
                            mobile_string = "DEMO";
                            startNewActivity();
                        } else {
                            makeToast("You are not verified");
                        }
                    }
                } else {
                    makeToast("Network Error");
                }
            } catch (JSONException e) {
                makeToast("Network Error");
                e.printStackTrace();
            }
        } else if (url.contains(WebService.GETOLDUSERDETAIL[0]) && strAction == 9) {
            try {
                if (result.length() > 0) {
                    JSONObject jsonObject = new JSONObject(result);
                    String jsonStatus = jsonObject.getString("status").toString();
                    if (jsonStatus.equals("200")) {
                        jsonObject = jsonObject.getJSONObject("details");
                        String userStatus = jsonObject.getString("status").toString();
                        String userID = jsonObject.getString("id").toString();
                        if (userID != "") {
                            sharedPreferencesUtils.setUserId(userID);
                        }
                        if (!userStatus.equals("")) {
                            setStatus("1");
                        }

                        makeToast("Welcome Back");
                        settitle = jsonObject.getString("name").toString();
                        mobile_string = jsonObject.getString("mobile").toString();
                        startNewActivity();
                    } else {
                        makeToast(jsonObject.getString("message").toString());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onWebServiceError(String result, String url) {
        customToast.showCustomToast(LoginActivity.this, result, customToast.ToastyError);
    }
}