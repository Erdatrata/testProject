package com.example.tazpitapp;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{

    @NonNull
    @NotNull
    String [] title;
    String [] data;
    //handle later
    //Image image;
    String [] imageUrl;
    String [] type;
    String [] date;
    String [] writer;
    Context context;
    public MyAdapter(Context ct,String [] title2, String [] data2, String [] date2, String [] image2, String [] type2, String [] writer2){
        context=ct;
        title=title2;
        data=data2;
        date=date2;
        imageUrl=image2;
        type=type2;
        writer=writer2;


    }
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
       View view= inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
    holder.titleItem.setText(title[position]);
        holder.dataItem.setText(data[position]);
        holder.timeItem.setText(date[position]);
        holder.writerItem.setText(writer[position]);
       // holder.myImageItem.setImageResource(imageUrl[position]);
    }

    @Override
    public int getItemCount() {
        return title.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleItem,dataItem,timeItem,writerItem;
        ImageView myImageItem;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            titleItem=itemView.findViewById(R.id.titleArticle);
            dataItem=itemView.findViewById(R.id.dataArticle);
            timeItem=itemView.findViewById(R.id.timeArticle);
            writerItem=itemView.findViewById(R.id.writerArticle);
            myImageItem=itemView.findViewById(R.id.imageArticale);
        }
    }
}
