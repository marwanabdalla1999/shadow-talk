package com.example.myapplication15;

public class chatsdata {
    String name;
    String bio;
    String pic;

    public chatsdata(String name, String bio, String pic) {
        this.name = name;
        this.bio = bio;
        this.pic = pic;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
