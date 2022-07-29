package com.example.myapplication15;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_massages")
public class massagemodel {
@PrimaryKey
@NonNull
    String key;
    String phone;
    String massage;

    int type;


    public massagemodel(String key,String phone,int type, String massage) {
        this.key=key;
        this.phone=phone;
        this.type = type;
        this.massage = massage;

    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
