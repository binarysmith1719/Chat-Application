package com.codezilla.chatapp;

public class Message {
   private String message;
   private String senderId;

    public Message(String message, String senderId) {
        this.message = message;
        this.senderId = senderId;
    }
    public Message(){}

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }
}
