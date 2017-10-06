package com.example.paulshao.mdbsocials;

import android.content.Intent;
import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class ListActivity extends AppCompatActivity {

    //initiating the database reference and posts
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SocialsApp");
    final ArrayList<Post> posts = new ArrayList<>();
    ListAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //create recyclerview
        RecyclerView recyclerAdapter = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerAdapter.setHasFixedSize(true);
        recyclerAdapter.setLayoutManager(new LinearLayoutManager(this));

        //set the new adapter with the posts
        adapter = new ListAdapter(getApplicationContext(),posts);
        recyclerAdapter.setAdapter(adapter);

        //button that leads to a new post activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewPostActivity.class);

                startActivity(intent);
            }
        });

        //to detect and retrieve once new values/events are added
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/SocialsApp");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                //load the new post into the temporary arraylist
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {

                    Post post = dataSnapshot1.getValue(Post.class);
                    posts.add(post);
                }
                Collections.reverse(posts);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }





}


