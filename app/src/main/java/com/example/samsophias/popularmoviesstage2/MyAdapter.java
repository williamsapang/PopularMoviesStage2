package com.example.samsophias.popularmoviesstage2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<String> listImg;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public MyAdapter(List<String> imgs, OnItemClickListener onItemClickListener) {
        this.listImg = imgs;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_list, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        String img = listImg.get(i);
        Picasso.with(context).load("https://image.tmdb.org/t/p/w185" + img).into(myViewHolder.imageView);
        myViewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listImg.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        public ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            parent = itemView;
        }
    }

}
