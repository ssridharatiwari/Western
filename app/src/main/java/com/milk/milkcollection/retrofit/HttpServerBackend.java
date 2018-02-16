package com.milk.milkcollection.retrofit;

import android.util.Log;

import com.google.gson.JsonObject;
import com.milk.milkcollection.application.AppApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HttpServerBackend {

    private final AppApplication context;

    public HttpServerBackend(AppApplication context) {
        this.context = context;
    }

    public static class ResponseListener {
        public ResponseListener() {
        }

        public void onReturn(boolean success, Response<JsonObject> data) {
        }

    }

    public void getData(final Call<JsonObject> call, final ResponseListener back) {

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                boolean isSuccess = false;

                try {
                    if (response != null) {
                        isSuccess = true;

                    } else {
                        isSuccess = false;
                    }

                    back.onReturn(isSuccess, response);
                } catch (Exception e) {
                    back.onReturn(isSuccess, null);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                t.printStackTrace();
                //handleIt
                Log.e("onFailure  ", " fghfh");
                back.onReturn(false, null);//STATUS_FAILURE
            }
        });
    }

}
