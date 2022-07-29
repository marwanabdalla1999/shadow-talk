package com.example.myapplication15;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = chatmodel.class,version = 3)
public abstract class database  extends RoomDatabase {

    private static database instanse;
    public abstract chatdatainterface chatsduo();



    public static synchronized database getinsance(Context context){

        if (instanse==null){
            instanse= Room.databaseBuilder(context.getApplicationContext(),database.class,"shadow_talk")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instanse;

    }




}
