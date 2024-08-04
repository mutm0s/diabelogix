package com.example.diabetesdetector.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.apiService.ApiService;
import com.example.diabetesdetector.models.PredictionRequest;
import com.example.diabetesdetector.models.PredictionResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {


    EditText pregnanciesEditText, glucoseEditText, bloodPressureEditText,
            skinThicknessEditText, insulinEditText, bmiEditText,
            dpfEditText, ageEditText;


    TextView predictionTextView, probabilityTextView;

    ApiService apiService;

    private ImageView mBackButton;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.predictive_items, container, false);

        // Initialize Retrofit with logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Find EditText fields and TextViews
        pregnanciesEditText = view.findViewById(R.id.pregnanciesEditText);
        glucoseEditText = view.findViewById(R.id.glucoseEditText);
        bloodPressureEditText = view.findViewById(R.id.bloodPressureEditText);
        skinThicknessEditText = view.findViewById(R.id.skinThicknessEditText);
        insulinEditText = view.findViewById(R.id.insulinEditText);
        bmiEditText = view.findViewById(R.id.bmiEditText);
        dpfEditText = view.findViewById(R.id.dpfEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        predictionTextView = view.findViewById(R.id.predictionTextView);
        probabilityTextView = view.findViewById(R.id.probabilityTextView);
        mBackButton = view.findViewById(R.id.btn_back);

        // Find predictButton and set OnClickListener
        Button predictButton = view.findViewById(R.id.predictButton);
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePrediction();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }

    private void makePrediction() {
        // Collect user inputs
        String pregnanciesText = pregnanciesEditText.getText().toString().trim();
        String glucoseText = glucoseEditText.getText().toString().trim();
        String bloodPressureText = bloodPressureEditText.getText().toString().trim();
        String skinThicknessText = skinThicknessEditText.getText().toString().trim();
        String insulinText = insulinEditText.getText().toString().trim();
        String bmiText = bmiEditText.getText().toString().trim();
        String dpfText = dpfEditText.getText().toString().trim();
        String ageText = ageEditText.getText().toString().trim();

        if (pregnanciesText.isEmpty() || glucoseText.isEmpty() || bloodPressureText.isEmpty() ||
                skinThicknessText.isEmpty() || insulinText.isEmpty() || bmiText.isEmpty() ||
                dpfText.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        PredictionRequest request = new PredictionRequest();
        request.Pregnancies = Integer.parseInt(pregnanciesText);
        request.Glucose = Integer.parseInt(glucoseText);
        request.BloodPressure = Integer.parseInt(bloodPressureText);
        request.SkinThickness = Integer.parseInt(skinThicknessText);
        request.Insulin = Integer.parseInt(insulinText);
        request.BMI = Float.parseFloat(bmiText);
        request.DiabetesPedigreeFunction = Float.parseFloat(dpfText);
        request.Age = Integer.parseInt(ageText);

        Call<PredictionResponse> call = apiService.predict(request);
        call.enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful()) {
                    PredictionResponse prediction = response.body();
                    predictionTextView.setText("Prediction: " + (prediction.prediction == 1 ? "Diabetic" : "Not Diabetic"));
                    // Format probability as percentage
                    float probabilityPercentage = prediction.probability * 100;
                    probabilityTextView.setText("Probability: " + String.format("%.2f", probabilityPercentage) + "%");
                } else {
                    Toast.makeText(requireContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to make prediction", Toast.LENGTH_SHORT).show();
            }
        });
    }
}