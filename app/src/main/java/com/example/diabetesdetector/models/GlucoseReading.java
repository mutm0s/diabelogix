package com.example.diabetesdetector.models;

public class GlucoseReading {
    private int glucoseReading;
    private String timestamp;

    public GlucoseReading() { }

    public GlucoseReading(int glucoseReading, int year, int month, int day, int hour, int minute) {
        this.glucoseReading = glucoseReading;
        this.timestamp = String.format("%04d-%02d-%02dT%02d:%02d:00Z", year, month, day, hour, minute);
    }

    public int getGlucoseReading() {
        return glucoseReading;
    }

    public void setGlucoseReading(int glucoseReading) {
        this.glucoseReading = glucoseReading;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

