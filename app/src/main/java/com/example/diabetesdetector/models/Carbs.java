package com.example.diabetesdetector.models;

public class Carbs {
    private double value;
    private String date;
    private String time;

    public Carbs() {
    }

    public Carbs(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
