package com.example.paulshao.mdbsocials;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{
    //initialize all the layout elements
    ImageView eventPic;
    TextView eventName;
    TextView date;
    TextView description;
    TextView RSVP;
    CheckBox Interested;

    //initialize all the corresponding values for the elements
    String eventNameString;
    int eventRSVP;
    String eventShortDescription;
    String emailString;
    String eventPicString;
    String eventKey;
    String dateString;
    ImageView nameIcon;
    ImageView desIcon;
    ArrayList<String> eventLike;


    Post data;
    boolean checked;
    boolean unchecked;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        eventPic = (ImageView)findViewById(R.id.eventPicture);
        eventName = (TextView)findViewById(R.id.eventNameDetail);
        date = (TextView)findViewById(R.id.dateDetail);
        description = (TextView)findViewById(R.id.descriptionDetail);
        RSVP = (TextView)findViewById(R.id.RSVPDetail);
        Interested = (CheckBox)findViewById(R.id.ABC);
        nameIcon = (ImageView) findViewById(R.id.nameIcon);
        desIcon = (ImageView) findViewById(R.id.desIcon);

        //receive the key from the listAdapter Activity
        Intent intent = getIntent();
        eventKey = intent.getStringExtra("key");

        checked = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("checkBox1", true);
        unchecked = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("checkBox1", false);

        //p.attendance.add("1");





        //generate the reference to the data based on the key and output them to the layout
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/SocialsApp").child(eventKey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(Post.class);
                eventNameString = data.eventName;
                eventName.setText(eventNameString);
                Utils.trimContent(eventNameString,eventName,6);
                RSVP.setText(String.valueOf(data.pplRSVPed));
                date.setText(data.date);
                description.setText(data.shortDescription);
                eventPicString = data.eventPictureURL;
                eventLike = data.attendance;
                emailString = data.email;
                //eventLike.add("default");






                //Use Asynctask For this Instead of Glide for loading pictures
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(eventPicString + ".png");
                Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(storageReference).into(eventPic);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //track if the user clicks the checkbox
        if (eventLike == null || !(eventLike.contains(emailString))){
            Toast.makeText(DetailActivity.this,"eventLike is null", Toast.LENGTH_LONG).show();
            Interested.setChecked(false);
        }
        else if(eventLike.contains(emailString)){
            Interested.setChecked(true);
        }

        Interested.setOnClickListener(this);




    }



    private void onBoxChecked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override

            public Transaction.Result doTransaction(MutableData mutableData) {
                final Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (Interested.isChecked()) {
                    // Unstar the post and remove self from stars
                    p.pplRSVPed = p.pplRSVPed + 1;
                    RSVP.setText(""+ data.pplRSVPed);
                    PreferenceManager.getDefaultSharedPreferences(DetailActivity.this).edit()
                            .putBoolean("checkBox1", checked).apply();
                    if (!(eventLike.contains(emailString))){
                    eventLike.add(emailString);}
                    p.setAttendance(eventLike);
                    boolean checked = Interested.isChecked();

                }
                else if (!(Interested.isChecked())){
                    // Star the post and add self to stars
                    p.pplRSVPed = p.pplRSVPed - 1;
                    RSVP.setText(""+data.pplRSVPed);
                    boolean checked = Interested.isChecked();
                    if (eventLike.contains(emailString)){
                      eventLike.remove(MainActivity.email);
                    }
                    p.setAttendance(eventLike);

                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("Errors", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void onClick(View view) {
               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/SocialsApp").child(eventKey);
               onBoxChecked(databaseReference);

        }

}



