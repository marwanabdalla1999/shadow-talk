package com.example.myapplication15;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

@Dao
public interface chatdatainterface {
        @Insert
        Completable insert(chatmodel chatmodel);

    @Delete
    Completable delete(chatmodel chatmodel);

    @Query("DELETE FROM chat_heads WHERE phonenumber = :phone")
    Completable delete_using_phone(String phone);

    @Update
  Completable update(chatmodel chatmodel);




    @Query("Select * from chat_heads ")
    Observable<List<chatmodel>> getchats();



}
