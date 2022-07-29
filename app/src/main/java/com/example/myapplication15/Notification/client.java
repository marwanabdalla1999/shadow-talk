package com.example.myapplication15.Notification;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class client {
    public static Retrofit retrofit=null;

    public static  Retrofit getclient(String url){

        if (retrofit==null){
            retrofit=new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();

        }

        return retrofit;
    }
}
