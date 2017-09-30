package com.example.paulshao.mdbsocials;

/**
 * Created by paulshao on 9/27/17.
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.paulshao.mdbsocials.R.id.emailAddress;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<Post> data;
    private ArrayList<Post> trueData;

    public ListAdapter(Context context, ArrayList<Post> data) {
        this.context = context;
        this.data = data;
        Log.d("m",""+ data.size());
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Post m = data.get(position);
        holder.listEmailAddress.setText(m.email);
        Log.d("m",m.email);
        if (m.eventName.length()>10) {
            String truncatedName = m.eventName.substring(0, 10) + "..";
            holder.listEventName.setText(truncatedName);
        }
        else
        {
            holder.listEventName.setText(m.eventName);
        }

        holder.listRSVPed.setText(""+m.pplRSVPed);
        if (m.shortDescription.length()>20) {
            String truncatedDescription = m.shortDescription.substring(0, 20) + "..";
            holder.listShortDescription.setText(truncatedDescription);
        }
        else
        {
            holder.listShortDescription.setText(m.shortDescription);
        }
        //String truncatedDescription = m.shortDescription.substring(0,20)+"...";
        //holder.listShortDescription.setText(truncatedDescription);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(m.eventPictureURL+".png");
        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).into(holder.listEventPic);
        holder.listParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("EventName", m.eventName);
                intent.putExtra("EventRSVP",m.pplRSVPed);
                intent.putExtra("EventShortDescription",m.shortDescription);
                intent.putExtra("Email",m.email);
                intent.putExtra("Date",m.date);
                intent.putExtra("EventPicUrl",m.eventPictureURL+".png");
                intent.putExtra("EventKey",m.key);

                context.startActivity(intent);
            }
        });

        FirebaseStorage.getInstance().getReference().child(m.eventPictureURL+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("Loading Success!", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading Failed...", exception.toString());
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<Post> newData) {
        trueData = newData;
        notifyDataSetChanged();

    }

    /**
     * A card displayed in the RecyclerView
     */
    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView listEventName;
        TextView listEmailAddress;
        TextView listRSVPed;
        TextView listShortDescription;
        ImageView listEventPic;
        View listParentView;

        public CustomViewHolder (View itemView) {
            super(itemView);
            this.listEmailAddress = itemView.findViewById(emailAddress);
            this.listEventName = itemView.findViewById(R.id.EventName);
            this.listRSVPed = itemView.findViewById(R.id.RSVPed);
            this.listShortDescription = itemView.findViewById(R.id.shortDescription);
            this.listEventPic = itemView.findViewById(R.id.eventPic);
            this.listParentView = itemView.findViewById(R.id.listParentView);
        }

        public void setListEventName(String eventName)
        {
            TextView event_name = (TextView)itemView.findViewById(R.id.eventNameDetail);
            event_name.setText(eventName);
        }

        public void setListEmailAddress(String emailAddress)
        {
            TextView email_address = (TextView)itemView.findViewById(R.id.emailAddress);
            email_address.setText(emailAddress);
        }

        public void setListShortDescription(String shortDescription)
        {
            TextView short_description = (TextView)itemView.findViewById(R.id.shortDescription);
            short_description.setText(shortDescription);
        }

        public void setListEventPic(Uri url)
        {
            ImageView event_pic = (ImageView) itemView.findViewById(R.id.eventPic);
            event_pic.setImageURI(url);
        }



    }
}


