package com.example.myapplication15;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class data_save {
   static SharedPreferences sharedPreferences;


    public static void getinstance(Context context){
                if (sharedPreferences==null) {
                    sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                }
    }

    public static Boolean  registeruser(Context context,String name,String phone,String Gender ,String Uri_pic,String birthdate,String bio,String nickname){
    sharedPreferences.edit()
     .putString("name",name)
            .putString("phone",phone)
            .putString("gender",Gender)
            .putString("Uri_pic",Uri_pic)
            .putString("birthdate",birthdate)
            .putString("bio",bio)
            .putString("nickname",nickname).apply();

        return true;
    }
    public static HashMap<String,String>  getchatsdata(int i){
        HashMap<String,String> userdata=new HashMap<>();
        userdata.put("name",sharedPreferences.getString("name"+i,""));
        userdata.put("phone",sharedPreferences.getString("phone"+i,""));
        userdata.put("Uri_pic",sharedPreferences.getString("Uri_pic"+i,""));
        userdata.put("bio",sharedPreferences.getString("bio"+i,""));
        return userdata;
    }
    public static Boolean  chatsdata(String name,String phone,String Uri_pic,String bio,int i){
        sharedPreferences.edit()
                .putString("name"+i,name)
                .putString("phone"+i,phone)
                .putString("Uri_pic"+i,Uri_pic)
                .putString("bio"+i,bio).apply();
        return true;
    }
    public static HashMap<String,String> getuserdata(Context context){
        HashMap<String,String> userdata=new HashMap<>();
        userdata.put("name",sharedPreferences.getString("name",""));
        userdata.put("phone",sharedPreferences.getString("phone",""));
        userdata.put("gender",sharedPreferences.getString("gender",""));
        userdata.put("Uri_pic",sharedPreferences.getString("Uri_pic",""));
        userdata.put("birthdate",sharedPreferences.getString("birthdate",""));
        userdata.put("bio",sharedPreferences.getString("bio",""));
        userdata.put("nickname",sharedPreferences.getString("nickname",""));
        return userdata;
    }

    public  static  void logout(){
        sharedPreferences.edit().putBoolean("signin",false).putString("name","")
                .putString("phone","")
                .putString("gender","")
                .putString("Uri_pic","")
                .putString("birthdate","").
                putString("bio","").apply();
    }

    public static void signin(){
        sharedPreferences.edit().putBoolean("signin",true).apply();

    }
    public static Boolean getsignstate(){
   Boolean retval=false;
   if(sharedPreferences.getBoolean("signin",false)){
       retval=true;

   }

return retval;

    }

}
