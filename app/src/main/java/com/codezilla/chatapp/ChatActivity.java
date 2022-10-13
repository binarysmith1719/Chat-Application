package com.codezilla.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rview;
    private EditText edtx;
    private ImageView imgv;
    private MessageAdapter msgAdapter;
    private ArrayList<Message> chatList;
    private String senderroom;
    private String recieverroom;
    private DatabaseReference mDbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String reciever = intent.getStringExtra("NAME_KEY");
        String Recieverid = intent.getStringExtra("UID_KEY");

        getSupportActionBar().setTitle(reciever);

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
//                  Log.d("bug", "Message on click : "+msg);
               Message msgObject = new Message(msg,Senderid);

               mDbRef.child("chats").child(senderroom).child("messages").push().setValue(msgObject).
                       addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Log.d("bug","here3");
                               mDbRef.child("chats").child(recieverroom).child("messages").push().setValue(msgObject);
                           }
                       });
               edtx.setText("");
              }
          });


        //****************************************************************

    }
}