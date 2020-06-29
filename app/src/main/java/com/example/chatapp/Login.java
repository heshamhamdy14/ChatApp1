package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText email , password;
    Button login;
    FirebaseAuth auth;
    TextView forget_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        email =findViewById(R.id.login_email);
        password =findViewById(R.id.login_password);
        login=findViewById(R.id.login);
        auth=FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_user();
            }
        });

        forget_password=findViewById(R.id.forget_password);
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this , ResetPassword.class);
                startActivity(intent);
            }
        });
    }
    public void sign_user(){
        String Email =email.getText().toString();
        String Pass =password.getText().toString();
        if (Email.equals("") || Pass.equals("")){
            Toast.makeText(this, "Invalid Email Or Password!!", Toast.LENGTH_SHORT).show();
        }else if (Pass.length() <= 6){
            Toast.makeText(this, "Password Must Be Mora Than 6 Litters", Toast.LENGTH_SHORT).show();
        }else
        auth.signInWithEmailAndPassword(Email , Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
           if(task.isSuccessful()){
               Toast.makeText(Login.this, "Login Successful ._.", Toast.LENGTH_SHORT).show();
               Intent intent=new Intent(Login.this , Home.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
               finish();
           }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
