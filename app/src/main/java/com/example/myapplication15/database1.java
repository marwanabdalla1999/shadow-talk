package com.example.myapplication15;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = massagemodel.class,version = 3)
public abstract class database1 extends RoomDatabase {

    private static database1 instanse;

    public abstract dao_for_chat_massages getmassages();


    public static synchronized database1 getinsance(Context context){

        if (instanse==null){
            instanse= Room.databaseBuilder(context.getApplicationContext(), database1.class,"shadow_talk1")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instanse;

    }




}
