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

public class SignUpActivity extends AppCompatActivity {

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

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreate();
            }
        });


    }

    private void attemptCreate(){
        String email = ((EditText) findViewById(R.id.newEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.newPassword)).getText().toString();
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("ye", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Sign up failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                            startActivity(intent);
                        }

                        // ...
                    }
                });
    }




}
