package com.example.paulshao.mdbsocials;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

//import static com.example.paulshao.mdbsocials.R.id.imageView;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int pickImageREQUEST = 111;
    private static FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Uri uri = null;

    DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    ImageView eventPic;

    //Button uploadPic;
    //Button choosePic;
    //Button saveData;
    ImageButton choosePic;
    ImageButton uploadPic;
    ImageButton saveData;

    EditText eventName;
    EditText shortDescription;
    TextView date;
    String email;
    TextView RSVPed;

    int pplRSVPed;
    int postNumber;

    private Uri filePath;
    private File fileDestination;
    String downloadUrl;

    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;



    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mAuth = FirebaseAuth.getInstance();

        uploadPic = (ImageButton) findViewById(R.id.uploadPic);
        choosePic = (ImageButton)findViewById(R.id.choosePic);
        saveData = (ImageButton) findViewById(R.id.saveButton);

        eventName = (EditText)findViewById(R.id.eventNameDetail);
        shortDescription = (EditText) findViewById(R.id.shortDescription);
        date = (TextView)findViewById(R.id.dateDetail);
        RSVPed = (TextView)findViewById(R.id.RSVPed);
        //eventPic = (ImageView)findViewById(R.id.eventPic);


        dateView = (TextView) findViewById(R.id.dateDetail);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SocialsApp");

        uploadPic.setOnClickListener(this);
        choosePic.setOnClickListener(this);
        saveData.setOnClickListener(this);


    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageREQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==pickImageREQUEST && resultCode==RESULT_OK){
            uri = data.getData();
            eventPic = (ImageView)findViewById(R.id.eventPic);
            eventPic.setImageURI(uri);


        }
    }

    @Override
    public void onClick(View view) {
        //if the clicked button is choose
        if (view == choosePic) {
            showFileChooser();
        }
        //if the clicked button is upload
        else if (view == uploadPic) {
            transmit();
        }

        else if (view.getId() == R.id.saveButton)
        {
           Intent intent = new Intent(getApplicationContext(), ListActivity.class);
           startActivity(intent);


        }
    }

    private void writeNewPost(String email, String eventName, String PictureURL, String shortDescription, int pplRSVPed, String key,String date) {
        Post newPost = new Post(email, eventName, PictureURL, shortDescription, pplRSVPed, key, date);

        mDataRef.child("users").setValue(newPost);
    }


    //this method will save the data and transmit it onto the listActivity class
    private void transmit(){


        eventPic.setDrawingCacheEnabled(true);
        eventPic.buildDrawingCache();
        Bitmap bitmap = eventPic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-c8ba2.appspot.com");


        final String id = databaseReference.child("SocialsApp").push().getKey();
        StorageReference storageReference1 = storageRef.child(id+".png");
        final String eventNameTitle = eventName.getText().toString().trim();
        final String shortDescriptionTitle = shortDescription.getText().toString().trim();
        final String dateTitle = date.getText().toString();
        final String emailTitle = mAuth.getCurrentUser().getEmail().toString();
        final int numRSVPTitle = pplRSVPed;

        storageReference1.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewPostActivity.this, "darn", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (!TextUtils.isEmpty(eventNameTitle)&&!TextUtils.isEmpty(shortDescriptionTitle)
                        &&!TextUtils.isEmpty(dateTitle)&&!TextUtils.isEmpty(emailTitle)&&!(numRSVPTitle>10000)){
                    StorageReference filepath = storageReference.child("PostImage").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            Post post = new Post (emailTitle,eventNameTitle,id,shortDescriptionTitle,numRSVPTitle,id,dateTitle);
                            databaseReference.child(id).setValue(post);
                            Toast.makeText(NewPostActivity.this,"Upload Complete",Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });



    }

    //this method will download the file
    private void getFile (File fileDestination) throws IOException {
        fileDestination = File.createTempFile("images", "jpg");
        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
        riversRef.getFile(fileDestination)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),
                                "Download Success!",Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(),
                        "Download Failed...",Toast.LENGTH_SHORT);
            }
        });
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };


    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }





        }


