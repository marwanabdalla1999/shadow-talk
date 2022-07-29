package com.example.myapplication15;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;

public class converter {
    
@TypeConverter
public String from_massage_to_Gson(massage massage){

        return new Gson().toJson(massage);
    }

@TypeConverter
    public massage from_Gson_to_massage(String massages){

        return new Gson().fromJson(massages,massage.class);
    }
}
