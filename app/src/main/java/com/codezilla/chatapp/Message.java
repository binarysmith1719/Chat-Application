package com.codezilla.chatapp;

public class Message {
   private String message;
   private String senderId;
   public String date="";

    public Message(String message, String senderId,String date ) {
        this.message = message;
        this.senderId = senderId;
        this.date=date;
    }
    public Message(){}

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }
}
