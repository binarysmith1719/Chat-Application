package com.codezilla.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupAct extends AppCompatActivity {
    private Button signupbutton;
    private EditText editTxtName;
    private EditText editTextEmail;
    private EditText editTextPass;
    private FirebaseAuth mAuth;                //BACKEND
    private DatabaseReference mDbRef;          //BACKEND
    private String name;
    private String MAIN_KEY="main key";
    String str2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        getSupportActionBar().hide();

        signupbutton= findViewById(R.id.btn_signup);
        editTextEmail=findViewById(R.id.edt_eml2);
        editTextPass=findViewById(R.id.edt_pass2);
        editTxtName=findViewById(R.id.edt_name);

        mAuth = FirebaseAuth.getInstance();

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= editTextEmail.getText().toString();
                String password= editTextPass.getText().toString();
                name= editTxtName.getText().toString();
            signup(email,password);
            }
        });
    }

    public void signup(String email,String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("TAG", "createUserWithEmail:success");

                            //GETTING CURRENT USER UID
                            String id = task.getResult().getUser().getUid();
//ANOTHER WAY TO GET UID    mAuth.getCurrentUser().getUid()
                            addinDatabase(name,email,id);

                            //ADDING IN AVAILABLE_ID(CHILD) AND ID_TO_UID(CHILD)
                            addin_AvailableId_And_IdToUid(id);

                            //SHARED PREFERENCES
                            SharedPreferences shpf = getSharedPreferences(MAIN_KEY,MODE_PRIVATE);
                            SharedPreferences.Editor editor = shpf.edit();
                            editor.putBoolean("loggeddk",true);
                            editor.apply();

                            Intent intent = new Intent(SignupAct.this,MainActivity.class);
                            finish();
                            startActivity(intent);
//                            updateUI(us);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupAct.this, "The Email already exists || Invalid Email",Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
    public void addin_AvailableId_And_IdToUid(String uid)
    {
            mDbRef.child("LastId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String str = snapshot.getValue(String.class);

                    mDbRef.child("AvailableId").child(str).setValue(uid);
                    mDbRef.child("IdToUid").child(uid).setValue(str);
                    int a = Integer.parseInt(str);a++;
                    str2 = String.valueOf(a);

                    mDbRef.child("LastId").setValue(str2);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    public void addinDatabase(String name, String email,String uid)
    {
        AppUser apu = new AppUser(name,email,uid);
        mDbRef = FirebaseDatabase.getInstance().getReference();
        mDbRef.child("User").child(uid).setValue(apu);
    }
}