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
public interface dao_for_chat_massages {
        @Insert
        Completable insert(massagemodel massagemodel);

    @Update
    Completable update(massagemodel massagemodel);

    @Query("Select * from chat_massages ORDER BY `key` ASC")
    Observable<List<massagemodel>> getchats();

    @Query("DELETE FROM chat_massages WHERE phone = :phone")
    Completable delete(String phone);

}
