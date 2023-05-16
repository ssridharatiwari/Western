package com.milk.milkcollectionapp.retrofit;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;


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


