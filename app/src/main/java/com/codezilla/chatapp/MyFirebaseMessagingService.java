package com.codezilla.chatapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Message;
import android.view.textclassifier.ConversationActions;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String title=message.getData().get("Title");
        String messagex = message.getData().get("Message");

        if(!UserActive.isActive || (UserActive.isActive&&(!title.equals(UserActive.chattingWith))) ) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "0")
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle(title)
                    .setContentText(messagex)
                    .setPriority(Notification.PRIORITY_HIGH);
            // NotificationManager nm= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
            nm.notify(0, builder.build());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        MainActivity.tkn=token;
        FirebaseDatabase.getInstance().getReference().child("Token").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }
}
