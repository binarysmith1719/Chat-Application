package com.codezilla.chatapp;

public class AppUser {
    public String name;
    public String email;
    public String uid;
    public String NodeKeyForDeletion="";  // DB-->Friends-->Userid--> Node_KEY_of_each_friend
    public AppUser(String name,String email,String uid)
    {
        this.name=name;
        this.email=email;
        this.uid=uid;
    }
//    public AppUser(String name,String email)
//    {
//        this.name=name;
//        this.email=email;
//    }
//    public AppUser(String name)
//    {
//        this.name=name;
//    }
    public AppUser()
    {}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
