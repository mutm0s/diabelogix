package com.example.diabetesdetector.models;

import com.google.gson.annotations.SerializedName;

public class Recommendation {
    @SerializedName("recipe")
    private String foodName;

    @SerializedName("image")
    private String imageUrl;

    // Constructor
    public Recommendation(String foodName, String imageUrl) {
        this.foodName = foodName;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
