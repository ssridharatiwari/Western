package com.milk.milkcollection.retrofit;


import com.google.gson.JsonObject;
import com.milk.milkcollection.helper.DBConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface RestInterface {
/*
    //Login User
    @FormUrlEncoded
    @POST(DBConstants.Login_Url)
    Call<JsonObject> userLogin(@Field("username") String username,
                               @Field("password") String password);*/

   //get port
    @GET("")
    Call<JsonObject> getPort();

 /*
    //get Attendance Report Url
    @GET(DBConstants.AttendanceReport_Url + "{user_id}")
    Call<JsonObject> getAttendance(@Path("user_id") String user_id);
*/

}


