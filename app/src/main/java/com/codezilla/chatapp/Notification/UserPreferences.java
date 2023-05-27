package com.codezilla.chatapp.Notification;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ConcurrentModificationException;

public class UserPreferences {
    public static SharedPreferences sharedPreferences;
    final static String PREFERENCE_KEY="ChatApplication";
    private static UserPreferences instance=null;

       private  UserPreferences(){}

       public static UserPreferences getInstance(Context context){
           if(instance==null) {
               instance = new UserPreferences();
               instance.sharedPreferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
           }
           return  instance;
       }

       public boolean isEncryptionEnabled(){
           boolean result=sharedPreferences.getBoolean("isEncrypted",false);
           return result;
       }

       public void enableEncryption()
       {
           SharedPreferences.Editor editor=sharedPreferences.edit();
           editor.putBoolean("isEncrypted",true);
           editor.apply();
       }
       public void disableEncryption()
       {
           SharedPreferences.Editor editor=sharedPreferences.edit();
           editor.putBoolean("isEncrypted",false);
           editor.apply();
       }
}
