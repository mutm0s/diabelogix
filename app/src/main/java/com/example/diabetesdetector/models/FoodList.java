package com.example.diabetesdetector.models;


public class FoodList {
    private String foodName;
    private double quantity;
    private String unit;

    public FoodList(String foodName, double quantity, String unit) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public FoodList(String foodName, double quantity) {
        this.foodName = foodName;
        this.quantity = quantity;
    }

    public FoodList(String foodName) {
        this.foodName = foodName;
    }


    public String getFoodName() {
        return foodName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}


