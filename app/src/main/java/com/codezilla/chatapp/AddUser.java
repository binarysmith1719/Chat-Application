package com.codezilla.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddUser extends AppCompatActivity {
    EditText edtx;
    Button btn;
    DatabaseReference mDbRef;
    FirebaseAuth mAuth;
    String OwnId;
    AppUser apu;
    ProgressBar pgbr;
    int enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        getSupportActionBar().setTitle("ADD FRIEND");

        btn= findViewById(R.id.btnlst);
        edtx =findViewById(R.id.edtxlst);
        pgbr = findViewById(R.id.pgbr);
        pgbr.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        OwnId = mAuth.getCurrentUser().getUid();
        mDbRef = FirebaseDatabase.getInstance().getReference();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                pgbr.setVisibility(View.VISIBLE);

             //GOING TO ID_TO_USERid ---> GETTING FRIEND'S_UID
             mDbRef.child("AvailableId").child(edtx.getText().toString()).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) { //SNAPSHOT IS THE FRIENDS_UID
                     if(snapshot.exists())
                     {
                         String str = snapshot.getValue(String.class);

                         //CHECKING IF THIS ID IS ALREADY A FRIEND
                         enter=1;
                         mDbRef.child("Friends").child(OwnId).addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {

                                 for(DataSnapshot dsp: snapshot.getChildren() )//GOING THROUGH EACH FRIEND
                                 {
                                     apu = dsp.getValue(AppUser.class);
                                     if(apu.getUid().equals(str))//MATCHING WITH EACH FRIEND'S UID
                                     {Toast.makeText(AddUser.this, "Already a friend !!", Toast.LENGTH_SHORT).show();enter=0;break;}
                                 }
                                 //IF INSERTED ID IS NOT OWN_ID
                                 if( !(str.equals(OwnId)) && enter==1 )
                                 {
                                     mDbRef.child("User").child(str).addValueEventListener(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             apu = snapshot.getValue(AppUser.class);
                                             mDbRef.child("Friends").child(OwnId).push().setValue(apu);
                                             Toast.makeText(AddUser.this, "Friend Added", Toast.LENGTH_SHORT).show();

                                         }
                                         @Override
                                         public void onCancelled(@NonNull DatabaseError error) {}});
                                 }
                                 else
                                 {   if(str.equals(OwnId)) Toast.makeText(AddUser.this, "Wanna Talk to yourself ???", Toast.LENGTH_SHORT).show();}

                             }
                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {}});



                     }
                     else {Toast.makeText(AddUser.this, "User Do Not Exists", Toast.LENGTH_SHORT).show();}
                     pgbr.setVisibility(View.INVISIBLE);
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {}});
            }
        });
    }

}