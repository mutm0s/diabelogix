package com.example.diabetesdetector.models;

public class Weight {
    private double value;
    private String date;
    private String time;

    // Default constructor required for calls to DataSnapshot.getValue(Weight.class)
    public Weight() {
    }

    public Weight(double value, String date, String time) {
        this.value = value;
        this.date = date;
        this.time = time;
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

