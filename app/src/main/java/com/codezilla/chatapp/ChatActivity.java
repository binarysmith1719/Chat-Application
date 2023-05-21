package com.codezilla.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codezilla.chatapp.Notification.APIService;
import com.codezilla.chatapp.Notification.Client;
import com.codezilla.chatapp.Notification.Data;
import com.codezilla.chatapp.Notification.MyResponse;
import com.codezilla.chatapp.Notification.NotificationSender;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rview;
    private EditText edtx;
    private ImageView imgv;
    private MessageAdapter msgAdapter;
    private ArrayList<Message> chatList;
    private String senderroom;
    private String recieverroom;
    private DatabaseReference mDbRef;
    private APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        UserActive.isActive=true;
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Intent intent = getIntent();
        String reciever = intent.getStringExtra("NAME_KEY");
        String Recieverid = intent.getStringExtra("UID_KEY");

        //Changing status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        //Color.parseColor("#FF0000")
        ColorDrawable cd= new ColorDrawable(getResources().getColor(R.color.black));
        getSupportActionBar().setTitle(reciever);
        getSupportActionBar().setBackgroundDrawable(cd);

        mDbRef = FirebaseDatabase.getInstance().getReference();

        edtx= findViewById(R.id.edt_write);
        imgv= findViewById(R.id.img_send);
        rview= findViewById(R.id.rvchat);
        rview.setHasFixedSize(true);
        rview.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<Message>();
        Log.d("bug","here1");
        msgAdapter = new MessageAdapter(this,chatList);
        rview.setAdapter(msgAdapter);

        String Senderid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        senderroom = Senderid + Recieverid;
        recieverroom = Recieverid + Senderid;

        //****************** Populating RecyclerView ***************

        mDbRef.child("chats").child(senderroom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("bug","here addvalueevent");
                chatList.clear();
                for(DataSnapshot snap:snapshot.getChildren()) {
                    Message msgobj = snap.getValue(Message.class);
                    chatList.add(msgobj);
//                    Log.d("bug","here addvalueevent2");
                }
                msgAdapter.notifyDataSetChanged();
                rview.scrollToPosition(chatList.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //**********************************************************

        //******************* Updating Chatroom ********************
          imgv.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Log.d("bug","here2");
                  String msg = edtx.getText().toString();
                  if(msg.equals(""))
                    return;

                  Calendar c = Calendar.getInstance();
                  int hr =c.get(Calendar.HOUR_OF_DAY);
                  int min=c.get(Calendar.MINUTE);
                  Toast.makeText(ChatActivity.this, "hr->"+hr+" min->"+min, Toast.LENGTH_SHORT).show();
                  String sendingtime= hr+":"+min+"";
               Message msgObject = new Message(msg,Senderid,sendingtime);
               mDbRef.child("chats").child(senderroom).child("messages").push().setValue(msgObject).
                       addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Log.d("bug","here3");
                               mDbRef.child("chats").child(recieverroom).child("messages").push().setValue(msgObject);
                           }
                       });
               //***************  Sending the Notification ******************************************
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       mDbRef.child("Token").child(Recieverid).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               String reciever_token = snapshot.getValue(String.class);
                               Sending_Notification(reciever_token, UserDetails.uname, msg);
                           }
                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {
                           }
                       });
                   }
               }).start();
               edtx.setText("");
              }
          });


        //****************************************************************

    }


    public void Sending_Notification(String usertoken, String title, String message)
    {
           Data data = new Data(title, message);
           NotificationSender sender = new NotificationSender(data, usertoken);
           apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
//                        Toast.makeText(SendNotif.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {}
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserActive.isActive=false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        UserActive.isActive=true;
    }
}