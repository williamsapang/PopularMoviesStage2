package com.example.samsophias.popularmoviesstage2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    private Context context;
    private List<String> listReview;

    public ReviewAdapter(List<String> listReview) {
        this.listReview = listReview;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.review_list, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txtReview.setText(listReview.get(i));
    }

    @Override
    public int getItemCount() {
        return listReview.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtReview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReview = itemView.findViewById(R.id.reviewText);
        }
    }

}
