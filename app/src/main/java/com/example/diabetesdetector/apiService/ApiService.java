package com.example.diabetesdetector.apiService;

import com.example.diabetesdetector.models.PredictionRequest;
import com.example.diabetesdetector.models.PredictionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/predict")
    Call<PredictionResponse> predict(@Body PredictionRequest request);
}

