package com.example.paulshao.mdbsocials;

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


public class loginActivity extends AppCompatActivity {

    Button signUpButton;

    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initiating the sign up button
        signUpButton = (Button)findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });

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

        ((Button) findViewById(R.id.loginButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


    }

    private void attemptLogin() {
        String email = ((EditText) findViewById(R.id.emailView)).getText().toString();
        String password = ((EditText) findViewById(R.id.emailView)).getText().toString();
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) {
            Toast.makeText(loginActivity.this,"Email or Password Missing",Toast.LENGTH_LONG).show();
        }
        else{
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("ye", "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("ye", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(loginActivity.this, "Sign in failed",
                                            Toast.LENGTH_SHORT).show();
                                }

                                else {
                                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        }


    private void attemptSignup() {
        String email = ((EditText) findViewById(R.id.emailView)).getText().toString();
        String password = ((EditText) findViewById(R.id.emailView)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("ye", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("ye", "signInWithEmail:failed", task.getException());
                            Toast.makeText(loginActivity.this, "sign up failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
}

