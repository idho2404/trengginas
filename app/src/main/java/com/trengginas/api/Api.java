package com.trengginas.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface Api {
        @FormUrlEncoded
        @POST("mysql.php")
        Call<String> getListHome(@Field("query") String query, @Field("key") String key);

        @FormUrlEncoded
        @POST("mysql.php")
        Call<String> getKecamatanList(
                @Field("key") String key,
                @Field("query") String query
        );

        @FormUrlEncoded
        @POST("mysql.php")
        Call<String> getDesaList(
                @Field("key") String key,
                @Field("query") String query
        );

        @FormUrlEncoded
        @POST("mysql.php")
        Call<String> getJudulList(
                @Field("key") String key,
                @Field("query") String query
        );

        @FormUrlEncoded
        @POST("mysql.php")
        Call<String> getTahunList(
                @Field("key") String key,
                @Field("query") String query
        );
}

