package com.example.diabetesdetector.models;
//
//public class CarbsInsulin {
//    private double carbs;
//
//    private double sugar;
//    private double insulin;
//    private String timestamp;
//
//
//    public CarbsInsulin() { }
//
//    public CarbsInsulin(double carbs,double sugar, double insulin, int year, int month, int day, int hour, int minute) {
//        this.carbs = carbs;
//        this.sugar = sugar;
//        this.insulin = insulin;
//        this.timestamp = String.format("%04d-%02d-%02dT%02d:%02d:00Z", year, month, day, hour, minute);
//    }
//
//    public double getCarbs() {
//        return carbs;
//    }
//
//    public void setCarbs(double carbs) {
//        this.carbs = carbs;
//    }
//
//    public double getSugar() {
//        return sugar;
//    }
//
//    public void setSugar(double sugar) {
//        this.sugar = sugar;
//    }
//
//    public double getInsulin() {
//        return insulin;
//    }
//
//    public void setInsulin(double insulin) {
//        this.insulin = insulin;
//    }
//
//    public String getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(String timestamp) {
//        this.timestamp = timestamp;
//    }
//}
//


public class CarbsInsulin {
    private double carbs;
    private double sugar;
    private double insulin;
    private double calories;
    private int year;
    private int month;
    private int dayOfMonth;
    private int hour;
    private int minute;

    public CarbsInsulin() {
        // Default constructor required for calls to DataSnapshot.getValue(CarbsInsulin.class)
    }

    public CarbsInsulin(double carbs, double sugar, double insulin, double calories, int year, int month, int dayOfMonth, int hour, int minute) {
        this.carbs = carbs;
        this.sugar = sugar;
        this.insulin = insulin;
        this.calories = calories;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getInsulin() {
        return insulin;
    }

    public void setInsulin(double insulin) {
        this.insulin = insulin;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
