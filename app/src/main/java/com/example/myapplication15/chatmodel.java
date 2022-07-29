package com.example.myapplication15;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_heads")
public class chatmodel {

    @NonNull
    @PrimaryKey()
String phonenumber;
    Boolean type;
    String img;
    String name;
    String bio;
    int number_of_unseen_massage;
    public chatmodel(){

    }

    public chatmodel(String phonenumber, Boolean type, String img, String name, String bio,int number_of_unseen_massages) {
        this.phonenumber = phonenumber;
        this.type = type;
        this.img = img;
        this.name = name;
        this.bio = bio;
        this.number_of_unseen_massage=number_of_unseen_massages;
    }

    public int getNumber_of_unseen_massage() {
        return number_of_unseen_massage;
    }

    public void setNumber_of_unseen_massage(int number_of_unseen_massage) {
        this.number_of_unseen_massage = number_of_unseen_massage;
    }



    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
