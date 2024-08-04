package com.example.diabetesdetector.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Doctor implements Parcelable {
    private String name;
    private String specialty;
    private double rating;
    private String distance;
    private String imageUrl;

    // Default constructor required for calls to DataSnapshot.getValue(Doctor.class)
    public Doctor() {
    }

    public Doctor(String name, String specialty, double rating, String distance, String imageUrl) {
        this.name = name;
        this.specialty = specialty;
        this.rating = rating;
        this.distance = distance;
        this.imageUrl = imageUrl;
    }

    // Parcelable constructor
    protected Doctor(Parcel in) {
        name = in.readString();
        specialty = in.readString();
        rating = in.readDouble();
        distance = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Doctor> CREATOR = new Creator<Doctor>() {
        @Override
        public Doctor createFromParcel(Parcel in) {
            return new Doctor(in);
        }

        @Override
        public Doctor[] newArray(int size) {
            return new Doctor[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Implement Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(specialty);
        dest.writeDouble(rating);
        dest.writeString(distance);
        dest.writeString(imageUrl);
    }
}
