package com.example.diabetesdetector.apiService;

import com.example.diabetesdetector.models.Recommendation;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface KetoDietService {
    @Headers({
            "x-rapidapi-key: 16b3cbd142mshcef2588c242144ap12ae9djsn7b874e613fd1",
            "x-rapidapi-host: keto-diet.p.rapidapi.com"
    })
    @GET("/?protein_in_grams__lt=15&protein_in_grams__gt=5")
    Call<List<Recommendation>> getRecommendations();
}
