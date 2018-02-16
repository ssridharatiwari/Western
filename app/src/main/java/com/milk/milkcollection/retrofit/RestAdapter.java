package com.milk.milkcollection.retrofit;

import com.milk.milkcollection.helper.DBConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAdapter {
    //main
     String API_BASE_URL = DBConstants.HOST;

    private Retrofit restAdapter;

    public enum AuthType {
        AUTHARIZED, UNAUTHARIZED
    }

    private RestInterface restInterface;

    public RestAdapter() {}

    public RestInterface getRestInterface() {
        restAdapter = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(getRequestHeader())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        restInterface = restAdapter.create(RestInterface.class);
        return restInterface;
    }

    //Have to decide about the sessionId
    public RestInterface getAuthRestInterface() {

        return restInterface;
    }

    private OkHttpClient getRequestHeader() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(160, TimeUnit.SECONDS)
                .connectTimeout(160, TimeUnit.SECONDS)
                .build();

        return okHttpClient;
    }

}