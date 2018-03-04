package com.example.paulshao.mdbsocials;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;


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
import java.util.ArrayList;
import java.util.Calendar;


public class NewPostActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int pickImageREQUEST = 111;
    private static FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Uri uri = null;
    private TextView dateView;
    private static final int CAM_REQUEST=1313;

    ImageView eventPic;
    ImageButton picDate;
    ProgressBar loadingProgress;

    ImageButton choosePic;
    ImageButton uploadPic;
    ImageButton saveData;

    EditText eventName;
    EditText shortDescription;
    TextView date;
    TextView RSVPed;

    int pplRSVPed;


    private StorageReference mStorageRef;
    private DatabaseReference mDataRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mAuth = FirebaseAuth.getInstance();

        //Locate and match all the widgets/progressbar with their IDs
        // and assigned references in this class
        uploadPic = (ImageButton) findViewById(R.id.uploadPic);
        choosePic = (ImageButton)findViewById(R.id.choosePic);
        saveData = (ImageButton) findViewById(R.id.saveButton);
        picDate = (ImageButton)findViewById(R.id.picDate);
        loadingProgress = (ProgressBar)findViewById(R.id.progressBar);
        eventName = (EditText)findViewById(R.id.eventNameDetail);
        shortDescription = (EditText) findViewById(R.id.shortDescription);
        date = (TextView)findViewById(R.id.dateDetail);
        RSVPed = (TextView)findViewById(R.id.RSVPed);
        eventPic = (ImageView)findViewById(R.id.eventPic);
        dateView = (TextView) findViewById(R.id.dateDetail);

        //Instantiating the Firebase storage reference and data reference
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDataRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SocialsApp");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



        //set the buttons all on OnClickListener
        uploadPic.setOnClickListener(this);
        choosePic.setOnClickListener(this);
        saveData.setOnClickListener(this);


    }

    @Override
    public void onClick(final View view) {
        //if the clicked button is to choose a picture
        if (view == choosePic) {
            CharSequence colors[] = new CharSequence[] {"Select from Gallery", "Take a picture(doesn't work ;_;)"};

            AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
            builder.setTitle("Select an Import Option");
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //final int request = 111;
                            switch (which) {
                                case 0:
                                    Utils.UtilshowPicChooser(NewPostActivity.this, pickImageREQUEST);
                                    break;
                                case 1:
                                    btnTakePhotoClicker btnTakePhotoClicker = new btnTakePhotoClicker();
                                    btnTakePhotoClicker.onClick(view);

                            }
                        }
                    });
            builder.show();
        }
        //if the clicked button is to upload all the information
        else if (view == uploadPic) {
            //Compared with the last-week version, this one uses the Utils class to generalize
            //the transmit method (because originally it takes a huge space (many lines) in the
            //newPostActivity class.
            Toast.makeText(getApplicationContext(),"Make sure you enter the name, description, date, and upload the picture"
                    ,Toast.LENGTH_LONG).show();
            if (uri == null){
                Toast.makeText(getApplicationContext(),"Make sure you actually upload a picture",Toast.LENGTH_LONG).show();
            }
            else{
                ArrayList<String> attendance = new ArrayList<>();
                attendance.add("default");
            Utils.transmit(loadingProgress,eventPic,databaseReference,eventName,
                    shortDescription,date,mAuth,pplRSVPed,uri,NewPostActivity.this,storageReference,attendance);}
        }

        //if the clicked button is to save the data and go to the mainfeed activity
        else if (view == saveData)
        {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);


        }
    }



    //The class and the method below are used to construct a datepickerfragment
    //that calls the date-picking object for users to select a specific date for their post/event
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

        //Once the data is set, they're transmitted and displayed in the Textview for date
        public void onDateSet(DatePicker view, int year, int month, int day) {
            TextView setDate = getActivity().findViewById(R.id.dateDetail);
            setDate.setText(view.getYear()+"/"+(view.getMonth()+1)+"/"+view.getDayOfMonth());

        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
         if (requestCode == CAM_REQUEST && resultCode == RESULT_OK) {
                if (data!=null){
                //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //eventPic.setImageBitmap(bitmap);
                Bundle bundle = data.getExtras();
                uri = uriSavedImage;
                    eventPic = (ImageView)findViewById(R.id.eventPic);
                    eventPic.setImageURI(uri);}
                //uri = Utils.bitmapToUriConverter(bitmap,NewPostActivity.this);
            }

    }
    Uri uriSavedImage;
    class btnTakePhotoClicker implements Button.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            uriSavedImage = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/photo1.jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
            //startActivityForResult(camera, 1);
            startActivityForResult(intent,CAM_REQUEST);

        }
    }



}


