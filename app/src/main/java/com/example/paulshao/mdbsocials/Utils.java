package com.example.paulshao.mdbsocials;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by paulshao on 10/4/17.
 */

public class Utils {

    //This general method helps trim the content of a post to ensure that it fits the overall layout
    public static void trimContent(String string, TextView textView, int limit) {
        if (string.length() > limit) {
            String truncatedName = string.substring(0, limit) + "..";
            textView.setText(truncatedName);
        } else {
            textView.setText(string);
        }
    }

    //This general method helps with Firebase authentication, specifically on logging in signed-up users
    public static void utilsLogIn(String email, String password, FirebaseAuth mAuth, final Activity activity) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            {
            Toast.makeText(activity, "Email or Password Missing", Toast.LENGTH_LONG).show();
            }
        else
            {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("ye", "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful())
                            {
                                Log.w("ye", "signInWithEmail:failed", task.getException());
                                Toast.makeText(activity, "Sign in failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Intent intent = new Intent(activity.getApplicationContext(), ListActivity.class);
                                activity.startActivity(intent);
                            }
                        }
                    });
            }

    }

    //This general method helps with Firebase authentication, specifically on creating and signing up new users
    public static void UtilsattemptCreate(String email, String password, FirebaseAuth mAuth,final Activity activity){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("ye", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(activity, "Sign up failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(activity.getApplicationContext(), ListActivity.class);
                            activity.startActivity(intent);
                        }

                        // ...
                    }
                });
        }

    //This general method intends on invoking a picture chooser that the user can use to upload and display their local images
    public static void UtilshowPicChooser(Activity activity, int pickImageREQUEST) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageREQUEST);
    }

    public static Uri bitmapToUriConverter(Bitmap mBitmap, Activity activity) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = 1;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(activity.getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = activity.openFileOutput(file.getName(),
                    Context.MODE_WORLD_READABLE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    public static Uri alternativeUriConverter(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //This general method intends on transmitting all the data in a new post to the firebase, with a generated key
    //as the identifier of this specific post for future retrieval and usage.
    public static void transmit(final ProgressBar loadingProgress, ImageView eventPic,
                                final DatabaseReference databaseReference, TextView eventName,
                                TextView shortDescription, TextView date, FirebaseAuth mAuth,
                                int pplRSVPed, final Uri uri, final Activity activity,
                                final StorageReference storageReference, ArrayList<String> arrayList){
        loadingProgress.setVisibility(View.VISIBLE);
        eventPic.setDrawingCacheEnabled(true);
        eventPic.buildDrawingCache();
        Bitmap bitmap = eventPic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //byte[] data = baos.toByteArray();


        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-c8ba2.appspot.com");

        final ArrayList<String> attendance =  new ArrayList<String>();
        final String id = databaseReference.child("SocialsApp").push().getKey();
        StorageReference storageReference1 = storageRef.child(id+".png");
        final String eventNameTitle = eventName.getText().toString().trim();
        final String shortDescriptionTitle = shortDescription.getText().toString().trim();
        final String dateTitle = date.getText().toString();
        final String emailTitle = mAuth.getCurrentUser().getEmail();
        final int numRSVPTitle = pplRSVPed;


        storageReference1.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "darn", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (!TextUtils.isEmpty(eventNameTitle)&&!TextUtils.isEmpty(shortDescriptionTitle)
                        &&!TextUtils.isEmpty(dateTitle)&&!TextUtils.isEmpty(emailTitle)&&!(numRSVPTitle>10000)){
                    StorageReference filepath = storageReference.child("PostImage").child(uri.getLastPathSegment());
                    ArrayList<String> Attendance = new ArrayList<String>();
                    Attendance.add("1");
                    final ArrayList<String> attendance = Attendance;
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Post post = new Post (emailTitle,eventNameTitle,id,shortDescriptionTitle,numRSVPTitle,id,dateTitle,attendance);
                            databaseReference.child(id).setValue(post);
                            Toast.makeText(activity,"Upload Complete",Toast.LENGTH_LONG).show();
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    });

                }
                else
                {
                    Toast.makeText(activity,"Make sure you enter the name, description, date, and upload the picture"
                    ,Toast.LENGTH_LONG).show();
                }
            }
        });




    }



}
