package com.example.tazpitapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{

    @NonNull
    @NotNull //the arrrays that we put the different data anout each article
    String [] title;
    String [] data;
    Bitmap [] imageUrl;
    String [] type;
    String [] date;
    String [] writer;
    Context context;
    String []urlToWeb;

    //the constructor, which builded in MainActivity
    public MyAdapter(Context ct, @NotNull String [] title2, String [] data2, String [] date2, Bitmap [] image2, String [] type2, String [] writer2, String[] urlToWeb){
        context=ct;
        title=title2;
        data=data2;
        date=date2;
        imageUrl=image2;
        type=type2;
        writer=writer2;
        this.urlToWeb=urlToWeb;


    }
//creates the holder
    @NotNull
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
       View view= inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override //builds each article to holder
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
    holder.titleItem.setText(title[position]);
        holder.dataItem.setText(data[position]);
        holder.timeItem.setText(date[position]);
        holder.writerItem.setText(writer[position]);
        holder.myImageItem.setImageBitmap(imageUrl[position]);
        holder.linkItem.setOnClickListener(v -> {
            Uri uri = Uri.parse(urlToWeb[position]); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        });
       // holder.myImageItem.setImageResource(imageUrl[position]);



    }

    @Override //returns the num of articles we have to put
    public int getItemCount() {
        return title.length;
    }
//creating the elemnts that connected with my_row.xml that contains the design that each article will have
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleItem,dataItem,timeItem,writerItem;
        ImageView myImageItem;
        ConstraintLayout linkItem;
//conecting between the elemnts and the xml
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
