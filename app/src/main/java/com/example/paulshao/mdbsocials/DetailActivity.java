package com.example.paulshao.mdbsocials;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    ImageView eventPic;
    TextView eventName;
    TextView date;
    TextView description;
    TextView RSVP;
    CheckBox Interested;

    String eventNameString;
    int eventRSVP;
    String eventShortDescription;
    String emailString;
    String eventPicString;
    String eventKey;
    String dateString;
    ImageView nameIcon;
    ImageView desIcon;

    Post data;


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

        Intent intent = getIntent();
        eventNameString = intent.getStringExtra("EventName");
        eventRSVP = intent.getIntExtra("EventRSVP",0);
        eventShortDescription = intent.getStringExtra("EventShortDescription");
        emailString = intent.getStringExtra("Email");
        eventPicString = intent.getStringExtra("EventPicUrl");
        eventKey = intent.getStringExtra("EventKey");
        dateString = intent.getStringExtra("Date");


        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(eventPicString);
        Glide.with(getApplicationContext()).using(new FirebaseImageLoader()).load(storageReference).into(this.eventPic);
        if (eventNameString.length()>6) {
            String truncatedName = eventNameString.substring(0, 6) + "..";
            eventName.setText(truncatedName);
            nameIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Full Event Title: "+eventNameString,Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            eventName.setText(eventNameString);
        }

        RSVP.setText(""+eventRSVP);
        description.setText(eventShortDescription);
        date.setText(dateString);
        RSVP.setText(""+eventRSVP);
        if (eventShortDescription.length()>150) {
            String truncatedName = eventShortDescription.substring(0, 150) + "..";
            description.setText(truncatedName);
            desIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"Full Description: "+eventShortDescription,Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            eventName.setText(eventNameString);
        }

        onCheckboxClicked(Interested);


        }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        data = new Post(emailString,eventNameString,eventPicString,eventShortDescription,eventRSVP,eventKey,dateString);

        // Check which checkbox was clicked
        switch(view.getId()) {

            case R.id.ABC:
                if (checked)
                {

                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/SocialsApp");
                    //ref.child(data.key).child("pplRSVPed").setValue(data.pplRSVPed+1);
                    data.setPplRSVPed(data.pplRSVPed+1);
                    RSVP.setText(""+(data.pplRSVPed)+"ppl");
                    Toast.makeText(getApplicationContext(),"You are now interested in this event!",Toast.LENGTH_LONG).show();
                }
            else
                {
                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/SocialsApp");
                    //ref.child(data.key).child("pplRSVPed").setValue(data.pplRSVPed-1);
                    data.setPplRSVPed(data.pplRSVPed-1);
                    RSVP.setText(""+data.pplRSVPed);
                }
                break;

        }




    }


}
