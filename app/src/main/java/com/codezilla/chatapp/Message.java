package com.codezilla.chatapp;

import java.util.Objects;

public class Message {
   private String message;
   private String senderId;
   public String id="";
   public String date="";
   public String publickey=""; // means the data is encrypted

    public Message(String message, String senderId,String date,String id) {
        this.message = message;
        this.senderId = senderId;
        this.date=date;
        this.id=id;
    }
    public Message(){}

    public String getDate() {
        return date;
    }

    public void setId(String id) {this.id = id;}

    public String getId() {return id;}

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getPublickey() {
        return publickey;
    }
    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return Objects.equals(message, message1.message) && Objects.equals(senderId, message1.senderId) && Objects.equals(id, message1.id) && Objects.equals(date, message1.date) && Objects.equals(publickey, message1.publickey);
    }

}
