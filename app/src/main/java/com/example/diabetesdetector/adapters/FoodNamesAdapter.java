package com.example.diabetesdetector.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetesdetector.R;

import java.util.List;


import com.example.diabetesdetector.models.FoodList;

import java.util.ArrayList;


public class FoodNamesAdapter extends RecyclerView.Adapter<FoodNamesAdapter.FoodNameViewHolder> {

    private List<FoodList> foodNames;

    public FoodNamesAdapter() {
        this.foodNames = new ArrayList<>();
    }

    public FoodNamesAdapter(List<FoodList> foodNames) {
        this.foodNames = foodNames;
    }

    @NonNull
    @Override
    public FoodNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_name, parent, false);
        return new FoodNameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodNameViewHolder holder, int position) {
        FoodList foodItem = foodNames.get(position);
        holder.bind(foodItem);
    }

    public List<FoodList> getFoodList() {
        return this.foodNames;
    }

    @Override
    public int getItemCount() {
        return foodNames.size();
    }

    public class FoodNameViewHolder extends RecyclerView.ViewHolder {

        private TextView txtFoodName;

        public FoodNameViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodName = itemView.findViewById(R.id.txt_food_name);
        }

        public void bind(FoodList foodItem) {
            txtFoodName.setText(foodItem.getFoodName());
        }
    }
}

//
//public class FoodNamesAdapter extends RecyclerView.Adapter<FoodNamesAdapter.FoodNameViewHolder> {
//
//    private List<String> foodNames;
//
//    public FoodNamesAdapter(List<String> foodNames) {
//        this.foodNames = foodNames;
//    }
//
//    @NonNull
//    @Override
//    public FoodNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_name, parent, false);
//        return new FoodNameViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull FoodNameViewHolder holder, int position) {
//        String foodName = foodNames.get(position);
//        holder.bind(foodName);
//    }
//
//    @Override
//    public int getItemCount() {
//        return foodNames.size();
//    }
//
//    public class FoodNameViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView txtFoodName;
//
//        public FoodNameViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtFoodName = itemView.findViewById(R.id.txt_food_name);
//        }
//
//        public void bind(String foodName) {
//            txtFoodName.setText(foodName);
//        }
//    }
//}
//
