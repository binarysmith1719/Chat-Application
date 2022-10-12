package com.codezilla.chatapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
}