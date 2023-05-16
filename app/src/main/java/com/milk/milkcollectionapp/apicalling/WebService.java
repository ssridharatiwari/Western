package com.milk.milkcollectionapp.apicalling;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.milk.milkcollectionapp.R;

import java.util.HashMap;
import java.util.Map;

public class WebService {


    public static String PREURL = "http://wokosoftware.com/western/";
    public static final String[] SENDDETAIL = {"index6.php", "Loading Data..."};
    public static final String[] VERIFYDETAILS = {"index6.php", "Loading Data..."};
    public static final String[] GETOLDUSERDETAIL = {"index6.php", "Loading Data..."};
    private static final boolean ISTESTING = true;

    private Context context;
    private String[] ApiUrl;
    private WebServiceListener listener;
    private CustomeProgressDialog customeProgressDialog;
    private Map<String, String> params;
    private boolean isShowText = false;
    private boolean isDialogShow = true;
    private int method;

    public WebService(Context context, String[] postUrl, Map<String, String> params, WebServiceListener listener,
                      boolean isDialogShow, int method) {
        this.context = context;
        this.ApiUrl = postUrl;
        this.params = params;
        this.listener = listener;
        this.isDialogShow = isDialogShow;
        this.method = method;
    }

    public void LoadData() {
        if (ISTESTING) {
            for (Map.Entry<String,String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                System.out.println(key + "=====>" + value );
            }
        }

        if (isDialogShow) {
            customeProgressDialog = new CustomeProgressDialog(context, R.layout.lay_customprogessdialog);
            TextView textView = (TextView) customeProgressDialog.findViewById(R.id.loader_showtext);
            if (isShowText) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(ApiUrl[1]);
            } else {
                textView.setVisibility(View.GONE);
            }

            customeProgressDialog.setCancelable(false);
            customeProgressDialog.show();
        }

        String finalurl = PREURL+ApiUrl[0];
        if (method == Request.Method.GET) {
            finalurl = PREURL + ApiUrl[0] + convertMapToUrlParams(params);
            params = null;
        }

        if (isConnectingToInternet()) {
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(method, finalurl, response -> {
                Log.d(ApiUrl[0]+">>>", response);
                if (null != customeProgressDialog && customeProgressDialog.isShowing()) {
                    customeProgressDialog.dismiss();
                }
                listener.onWebServiceActionComplete(response, ApiUrl[0]);
            }, error -> {
                Log.d("error", error.toString());
                if (null != customeProgressDialog && customeProgressDialog.isShowing()) {
                    customeProgressDialog.dismiss();
                }
                listener.onWebServiceError(error.toString(), ApiUrl[0]);
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(request);
        } else {
            if (null != customeProgressDialog && customeProgressDialog.isShowing()) {
                customeProgressDialog.dismiss();
            }
            listener.onWebServiceError("Internet not available", ApiUrl[0]);
        }
    }

    public String convertMapToUrlParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        if (sb.length() > 0) {
            sb.insert(0, "?");
        }
        return sb.toString();
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }  else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                        return true;
                    }
                }
            }else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        if (ISTESTING) {
                            Log.i("CheckInternet_status", "Network is available");
                        }
                        return true;
                    }
                } catch (Exception e) {
                    if (ISTESTING) {
                        Log.i("CheckInternet_error", "" + e.getMessage());
                    }
                }
            }
        }
        if (ISTESTING) {
            Log.i("CheckInternet_status", "Network is not available");
        }
        return false;
    }

}
