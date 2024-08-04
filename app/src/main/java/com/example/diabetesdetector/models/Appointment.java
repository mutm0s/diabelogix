package com.example.diabetesdetector.models;

public class Appointment {
    private String doctorName;

    private String email;
    private String description;
    private String date;
    private String time;

    public Appointment(String doctorName, String description, String date, String time) {
        this.doctorName = doctorName;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public Appointment(String doctorName, String email, String description, String date, String time) {
        this.doctorName = doctorName;
        this.email = email;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public Appointment(String doctorName, String description, String time) {
        this.doctorName = doctorName;
        this.description = description;
        this.time = time;
    }

    public Appointment(){}

    public String getDoctorName() {
        return doctorName;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}


