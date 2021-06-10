package com.example.tazpitapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{

    @NonNull
    @NotNull
    String [] title;
    String [] data;
    //handle later
    //Image image;
    Bitmap [] imageUrl;
    String [] type;
    String [] date;
    String [] writer;
    Context context;
    String []urlToWeb;

    String[]link;
    public MyAdapter(Context ct,String [] title2, String [] data2, String [] date2, Bitmap [] image2, String [] type2, String [] writer2,String[] urlToWeb){
        context=ct;
        title=title2;
        data=data2;
        date=date2;
        imageUrl=image2;
        type=type2;
        writer=writer2;
        this.urlToWeb=urlToWeb;


    }

    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
       View view= inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
    holder.titleItem.setText(title[title.length-position-1]);
        holder.dataItem.setText(data[title.length-position-1]);
        holder.timeItem.setText(date[title.length-position-1]);
        holder.writerItem.setText(writer[title.length-position-1]);
        holder.myImageItem.setImageBitmap(imageUrl[title.length-position-1]);
        holder.linkItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(urlToWeb[title.length-position-1]); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
       // holder.myImageItem.setImageResource(imageUrl[position]);



    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleItem,dataItem,timeItem,writerItem;
        ImageView myImageItem;
        ConstraintLayout linkItem;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleItem=itemView.findViewById(R.id.titleArticle);
            dataItem=itemView.findViewById(R.id.dataArticle);
            timeItem=itemView.findViewById(R.id.timeArticle);
            writerItem=itemView.findViewById(R.id.writerArticle);
            myImageItem=itemView.findViewById(R.id.imageArticale);
            linkItem=itemView.findViewById(R.id.blockOfNews);

        }
    }


}
