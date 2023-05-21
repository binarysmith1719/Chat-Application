package com.codezilla.chatapp.Notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createChannels();
    }
   public void createChannels()
   {
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
           NotificationChannel nc = new NotificationChannel("0", "channel_0", NotificationManager.IMPORTANCE_HIGH);
            nc.setDescription("This is channel 0");

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(nc);
       }
   }
}
