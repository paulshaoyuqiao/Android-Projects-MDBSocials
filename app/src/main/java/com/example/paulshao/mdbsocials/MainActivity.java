package com.example.paulshao.mdbsocials;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button signUpButton;
    Button LoginButton;
    public static String email;

    private static FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        signUpButton = (Button)findViewById(R.id.newAccountButton);
        LoginButton = (Button)findViewById(R.id.loginButton);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Logged in", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Logged out", "onAuthStateChanged:signed_out");
                }
                // ...
            }

        };

        signUpButton.setOnClickListener(this);
        LoginButton.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        if (view == signUpButton)
        {
            signUpMethod();
        }
        else if (view == LoginButton){
            signInMethod();
        }
    }



    //as the user signs in
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    //as the user signs out or no user exists
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //sign in method
    public void signInMethod(){
        email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
        Utils.utilsLogIn(email,password,mAuth,MainActivity.this);
    }

    //sign up method
    public void signUpMethod()
    {
        Intent intent = new Intent (getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
    }


}
