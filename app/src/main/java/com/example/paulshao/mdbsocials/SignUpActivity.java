package com.example.paulshao.mdbsocials;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    //initiating all the necessary variables
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;

    EditText emailInput;
    EditText passwordInput;

    Button createAccount;
    Button backToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //locating all the elements and matching them with their
        // assigned references
        emailInput = (EditText)findViewById(R.id.newEmail);
        passwordInput = (EditText)findViewById(R.id.newPassword);

        backToLogin = (Button)findViewById(R.id.backButton);
        createAccount = (Button)findViewById(R.id.createButton);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("ye", "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d("ye", "onAuthStateChanged:signed_out");
                }
            }
        };

        createAccount.setOnClickListener(this);
        backToLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view == createAccount){
            attemptCreate();
        }
        else if (view == backToLogin){
            setBackToLogin();
        }
    }

    private void attemptCreate(){
        String email = ((EditText) findViewById(R.id.newEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.newPassword)).getText().toString();
        //Compared with the last-week version, this one uses the Utils class to generalize
        //the attemptCreate (the one that signs new users up) method (because originally
        // it takes a huge space (many lines) in the SignUpActivity class.
        Utils.UtilsattemptCreate(email,password,mAuth,SignUpActivity.this);
    }

    private void setBackToLogin(){
        Intent intent = new Intent (getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }



}
