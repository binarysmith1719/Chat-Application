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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;

public class loginAct extends AppCompatActivity {
private Button signupbutton;
private Button loginbutton;
private EditText editTextEmail;
private EditText editTextPass;
private FirebaseAuth mAuth;
private String MAIN_KEY="main key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        SharedPreferences getshpf= getSharedPreferences(MAIN_KEY,MODE_PRIVATE);
        Boolean value = getshpf.getBoolean("loggeddk",false);
        if(value)
        {
            Intent intent = new Intent(loginAct.this,MainActivity.class);
            finish();
            startActivity(intent);
        }

        getSupportActionBar().hide();

        signupbutton= findViewById(R.id.btn_signup);
        loginbutton= findViewById(R.id.btn_login);
        editTextEmail=findViewById(R.id.edt_eml);
        editTextPass=findViewById(R.id.edt_pass);

        mAuth = FirebaseAuth.getInstance();

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginAct.this,SignupAct.class);
                startActivity(intent);
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= editTextEmail.getText().toString();
                String password=editTextPass.getText().toString();
                loginfunc(email,password);
            }
        });
    }

    public void loginfunc(String email,String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            SharedPreferences shpf = getSharedPreferences(MAIN_KEY,MODE_PRIVATE);
                            SharedPreferences.Editor editor = shpf.edit();
                            editor.putBoolean("loggeddk",true);
                            editor.apply();

                            Intent intent = new Intent(loginAct.this,MainActivity.class);
                            finish();
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(loginAct.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}