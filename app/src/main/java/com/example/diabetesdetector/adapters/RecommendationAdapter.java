package com.example.diabetesdetector.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.Recommendation;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.Recommendation;

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private List<Recommendation> recommendations;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Recommendation recommendation);
    }

    public RecommendationAdapter(List<Recommendation> recommendations, OnItemClickListener listener) {
        this.recommendations = recommendations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recommendation recommendation = recommendations.get(position);
        holder.bind(recommendation, listener);
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodNameTextView;
        ImageView foodImageView;

        ViewHolder(View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.textView_food_name);
            foodImageView = itemView.findViewById(R.id.imageView_food);
        }

        void bind(final Recommendation recommendation, final OnItemClickListener listener) {
            foodNameTextView.setText(recommendation.getFoodName());
            Glide.with(context).load(recommendation.getImageUrl()).into(foodImageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(recommendation);
                }
            });
        }
    }
}


