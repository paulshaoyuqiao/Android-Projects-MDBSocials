package com.example.paulshao.mdbsocials;

/**
 * Created by paulshao on 9/27/17.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


    //inflate cardview and initiate the display
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }

    //
    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Post m = data.get(position);
        holder.listEmailAddress.setText(m.email);
        Log.d("m",m.email);
        Utils.trimContent(m.eventName,holder.listEventName,10);
        holder.listRSVPed.setText(""+m.pplRSVPed);
        Utils.trimContent(m.shortDescription,holder.listShortDescription,15);



        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(m.eventPictureURL+".png");
        //String image_url = storageReference.getDownloadUrl().toString();

        //DownloadFilesTask downloadFilesTask = new DownloadFilesTask();
        //downloadFilesTask.execute(image_url);
        //downloadFilesTask downloadFilesTask = new downloadFilesTask(holder,data);
        //downloadFilesTask.execute();
        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).into(holder.listEventPic);

        holder.listParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("key",m.key);
                //Utils.transmitExtra(intent,m.pplRSVPed,m.eventName,
                  //      m.shortDescription,m.email,m.date,m.eventPictureURL,m.key);
                context.startActivity(intent);
            }
        });

        FirebaseStorage.getInstance().getReference().child(m.eventPictureURL+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Log.d("Loading Success!", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading Failed...", exception.toString());
            }
        });


    }

    public class downloadFilesTask extends AsyncTask<Void, Void, Bitmap> {
        CustomViewHolder holder;
        ArrayList<Post> data;
        public downloadFilesTask (CustomViewHolder holder, ArrayList<Post> data){
            this.holder = holder;
            this.data = data;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference mStorageReference = storageReference.child(data.get(0).key+".png");
        final Bitmap[] bitmap = new Bitmap[1];

        @Override
        protected Bitmap doInBackground(Void... voids) {


            final long ONE_MEGABYTE = 1024 * 1024;
            mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Log.d("string", String.valueOf(bitmap[0]));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
            return bitmap[0];
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            holder.listEventPic.setImageBitmap(bitmap);
            System.out.println(holder.listEventPic.toString());
        }
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


    }
}


