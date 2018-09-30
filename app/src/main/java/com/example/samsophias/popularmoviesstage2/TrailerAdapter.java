package com.example.samsophias.popularmoviesstage2;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    private Context context;
    private String[] trailers;
    public TrailerAdapter(String[] trailers) {
        this.trailers = trailers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.trailer_list, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final String trailer = trailers[i];
        myViewHolder.btnTrailer.setText("Watch Trailer " + (i+1));

        myViewHolder.btnTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer));
                    String title = "Watch video via";
                    Intent chooser = Intent.createChooser(intent, title);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(chooser);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return trailers.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        Button btnTrailer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btnTrailer = itemView.findViewById(R.id.btnTrailer);
        }
    }
}
