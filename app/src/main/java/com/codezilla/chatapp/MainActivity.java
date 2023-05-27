package com.codezilla.chatapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codezilla.chatapp.RsaEncryption.MyKeyPair;
import com.codezilla.chatapp.RsaEncryption.RsaAlgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
public  RecyclerView RwView;
private UserAdapter userAdapter;
private ArrayList<AppUser> userList;
private DatabaseReference mDbRef;
private FirebaseAuth mAuth;
private String MAIN_KEY="main key";
private ProgressBar pgbr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.purple_200));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        //Changing status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.purple_200));
        }

        pgbr = findViewById(R.id.pgbr_m);
        RwView = findViewById(R.id.RwView);
        userList = new ArrayList<AppUser>();
//        Log.d("bug","got_response1");
        RwView.setHasFixedSize(true);
        RwView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(this,userList);
//        Log.d("bug","got_response12");
        RwView.setAdapter(userAdapter);

        //***************** ACCESSING DATABASE *********************
        mAuth = FirebaseAuth.getInstance();
        mDbRef= FirebaseDatabase.getInstance().getReference();

        //GETTING THE KEYPAIR (PUBLIC_KEY , PRIVATE_KEY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("tag","key initial");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MyKeyPair.initializeKeyPair();
                    mDbRef.child("PublicKeys").child(mAuth.getCurrentUser().getUid()).setValue(MyKeyPair.StrPublickey);
                }
            }).start();
        }

        //FRIENDS ----> CURRENT USER_ID ----> {LIST OF FRIENDS}
        mDbRef.child("Friends").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //SNAPSHOT IS THE LIST OF FRIENDS
//                Log.d("chk", "onDataChange: here1");
                pgbr.setVisibility(View.VISIBLE);

                if(snapshot.exists()) {
                   userList.clear();
                   for (DataSnapshot datasnapshot:snapshot.getChildren()) {//EACH FRIEND'S SNAPSHOT
                       AppUser apu = datasnapshot.getValue(AppUser.class); //EACH FRIEND'S OBJECT
                       apu.NodeKeyForDeletion=datasnapshot.getKey(); //INSERTING THE NODE KEY OF THE FRIEND FOR DELETION PURPOSE
                       //CHECKING IF THE DATA MATCHES THE CURRENT USER ID
                       if(!(apu.getUid().equals(mAuth.getCurrentUser().getUid())))
                       { userList.add(apu);}
                   }
                   userAdapter.notifyDataSetChanged();
               }
                pgbr.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        UpdateToken();
        //*******************************************************************
//        pgbr.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if(item.getItemId()==R.id.logout) {
           mAuth.signOut();
           SharedPreferences shpf = getSharedPreferences(MAIN_KEY, MODE_PRIVATE);
           SharedPreferences.Editor editor = shpf.edit();
           editor.putBoolean("loggeddk", false);
           editor.apply();

           Intent intent = new Intent(MainActivity.this, loginAct.class);
           finish();
           startActivity(intent);

       }
       else if(item.getItemId()==R.id.addUser)
       {
           //Add user activity
           Intent intent = new Intent(MainActivity.this, AddUser.class);
           startActivity(intent);
       }
       else
       {
           Intent intent = new Intent(MainActivity.this, MyId.class);
           startActivity(intent);
       }
        return super.onOptionsItemSelected(item);
    }

    //UPDATING THE USERS TOKEN AS IT FREQUENTLY GETS RENEWED BY THE SYSTEM
    public static String tkn="0";
    public void UpdateToken(){
        Log.d("tag",tkn);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        if (!task.isSuccessful()) {
                            Log.d("bug", "Fetching FCM registration token failed");
                            return;
                        }
                        // Get new FCM registration token
                        tkn = task.getResult();
                        mDbRef.child("Token").child(mAuth.getCurrentUser().getUid()).setValue(tkn);
                        // Toast.makeText(MainActivity.this, "messaging yey"+task.getResult(), Toast.LENGTH_SHORT).show();
                    }
                });

        mDbRef.child("User").child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails.uname=snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}