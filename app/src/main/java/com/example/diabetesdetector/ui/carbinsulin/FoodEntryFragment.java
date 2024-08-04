//package com.example.diabetesdetector.ui.carbinsulin;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.ProgressBar;
//import android.widget.TimePicker;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.diabetesdetector.R;
//import com.example.diabetesdetector.adapters.FoodNamesAdapter;
//import com.example.diabetesdetector.apiService.ApiCaller;
//import com.example.diabetesdetector.models.CarbsInsulin;
//import com.example.diabetesdetector.models.FoodList;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FoodEntryFragment extends Fragment implements ApiCaller.ApiCallerCallback {
//
//    private DatePicker datePicker;
//    private TimePicker timePicker;
//    private TextInputEditText edtFoodName;
//    private TextInputEditText edtInsulin;
//    private TextInputEditText edtSugar;
//    private Button btnAddFood;
//    private Button btnCalculate;
//    private RecyclerView recyclerFoodNames;
//    private ProgressBar progressBar;
//    private List<FoodList> foodNames;
//
//    private FoodNamesAdapter foodNamesAdapter;
//
//    private DatabaseReference mDatabase;
//    private FirebaseUser currentUser;
//
//    public FoodEntryFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_carb_insulin, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        datePicker = view.findViewById(R.id.date_picker);
//        timePicker = view.findViewById(R.id.time_picker);
//        edtFoodName = view.findViewById(R.id.edt_food_name);
//        edtSugar = view.findViewById(R.id.edt_sugar);
//        edtInsulin = view.findViewById(R.id.edt_insulin);
//        btnAddFood = view.findViewById(R.id.btn_add_food);
//        btnCalculate = view.findViewById(R.id.btn_calculate);
//        recyclerFoodNames = view.findViewById(R.id.recycler_food_names);
//        progressBar = view.findViewById(R.id.progress_bar);
//
//        // Initialize Firebase Database reference
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        // Initialize food names list and adapter
//        foodNames = new ArrayList<>();
//        foodNamesAdapter = new FoodNamesAdapter(foodNames);
//
//        // Set layout manager and adapter for RecyclerView
//        recyclerFoodNames.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerFoodNames.setAdapter(foodNamesAdapter);
//
//        // Button click listener for adding food names
//        btnAddFood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String foodName = edtFoodName.getText().toString().trim();
//                if (!foodName.isEmpty()) {
//                    FoodList food = new FoodList(foodName);
//                    foodNames.add(food);
//                    foodNamesAdapter.notifyDataSetChanged();
//                    edtFoodName.setText(""); // Clear EditText after adding food name
//                } else {
//                    Toast.makeText(getContext(), "Please enter a food name", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        // Button click listener for calculating carbs and insulin
//        btnCalculate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (foodNamesAdapter.getItemCount() == 0) {
//                    Toast.makeText(getContext(), "Please add some food items first", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String sugarValue = edtInsulin.getText().toString().trim();
//                if (sugarValue.isEmpty()) {
//                    Toast.makeText(getContext(), "Please enter sugar level value", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                String insulinValue = edtInsulin.getText().toString().trim();
//                if (insulinValue.isEmpty()) {
//                    Toast.makeText(getContext(), "Please enter insulin value", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                progressBar.setVisibility(View.VISIBLE);
//                String foodItems = "";
//                for (FoodList item : foodNamesAdapter.getFoodList()) {
//                    foodItems += item.getFoodName() + " ";
//                }
//                String query = foodItems;
//                String apiUrl = "https://api.calorieninjas.com/v1/nutrition?query=" + query;
//                ApiCaller apiCaller = new ApiCaller();
//                apiCaller.setApiCallerCallback(FoodEntryFragment.this);
//                apiCaller.execute(apiUrl, "+Kymj4OzlDLXexZ3ZcOOVg==sDaXa4L4oS0Xg4m8");
//            }
//        });
//    }
//
//    private void calculateCarbsAndInsulin(double totalCals) {
//        // Simulate calculation for demonstration purposes
//        double carbs = totalCals / 4; // Assuming 4 calories per gram of carb
//        double sugar = Double.parseDouble(edtSugar.getText().toString().trim());
//        double insulin = Double.parseDouble(edtInsulin.getText().toString().trim());
//        // Delay for 2 seconds to simulate calculation
//        new Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // Hide loading progress bar
//                        progressBar.setVisibility(View.GONE);
//                        // Send result to Firebase with calculated calories
//                        sendResultToFirebase(carbs, sugar, insulin, totalCals);
//                        // Show result or navigate to another view to display result
//                        Toast.makeText(getContext(), "Carbs and insulin calculated", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                2000);
//    }
//
//
//    private void sendResultToFirebase(double carbs, double sugar, double insulin, double calories) {
//        Toast.makeText(getContext(), "save method called", Toast.LENGTH_SHORT).show();
//
//        // Get date
//        int year = datePicker.getYear();
//        int month = datePicker.getMonth();
//        int dayOfMonth = datePicker.getDayOfMonth();
//
//        // Get time
//        int hour = timePicker.getHour();
//        int minute = timePicker.getMinute();
//
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            String key = mDatabase.child("users").child(userId).child("carbinsulin").push().getKey();
//            if (key != null) {
//                CarbsInsulin carbsInsulin = new CarbsInsulin(carbs, sugar, insulin, calories, year, month + 1, dayOfMonth, hour, minute);
//                mDatabase.child("users").child(userId).child("carbinsulin").child(key).setValue(carbsInsulin)
//                        .addOnSuccessListener(aVoid -> {
//                            // Data successfully written to Firebase
//                            Toast.makeText(getContext(), "Result saved to Firebase", Toast.LENGTH_SHORT).show();
//                            Log.d("FoodEntryFragment", "Data saved to Firebase");
//
//                        })
//                        .addOnFailureListener(e -> {
//                            // Failed to write data to Firebase
//                            Toast.makeText(getContext(), "Failed to save result to Firebase", Toast.LENGTH_SHORT).show();
//                            Log.e("FoodEntryFragment", "Failed to save data to Firebase", e);
//                        });
//            }
//        }
//    }
//
//
//    @Override
//    public void onApiCallCompleted(String result) {
//        try {
//            JSONObject jsonObject = new JSONObject(result);
//            JSONArray itemsArray = jsonObject.getJSONArray("items");
//            double totalCals = 0;
//
//            // Iterate through the items array
//            for (int i = 0; i < itemsArray.length(); i++) {
//                JSONObject item = itemsArray.getJSONObject(i);
//                double calories = item.getDouble("calories");
//                totalCals += calories;
//            }
//
//            // Calculate carbs and insulin
//            calculateCarbsAndInsulin(totalCals);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}


package com.example.diabetesdetector.ui.carbinsulin;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.adapters.FoodNamesAdapter;
import com.example.diabetesdetector.apiService.ApiCaller;
import com.example.diabetesdetector.models.CarbsInsulin;
import com.example.diabetesdetector.models.FoodList;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodEntryFragment extends Fragment implements ApiCaller.ApiCallerCallback {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextInputEditText edtFoodName;
    private TextInputEditText edtInsulin;
    private TextInputEditText edtSugar;
    private Button btnAddFood;
    private Button btnCalculate;
    private RecyclerView recyclerFoodNames;
    private ProgressBar progressBar;
    private List<FoodList> foodNames;

    private FoodNamesAdapter foodNamesAdapter;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    public FoodEntryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_carb_insulin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        datePicker = view.findViewById(R.id.date_picker);
        timePicker = view.findViewById(R.id.time_picker);
        edtFoodName = view.findViewById(R.id.edt_food_name);
        edtSugar = view.findViewById(R.id.edt_sugar);
        edtInsulin = view.findViewById(R.id.edt_insulin);
        btnAddFood = view.findViewById(R.id.btn_add_food);
        btnCalculate = view.findViewById(R.id.btn_calculate);
        recyclerFoodNames = view.findViewById(R.id.recycler_food_names);
        progressBar = view.findViewById(R.id.progress_bar);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize food names list and adapter
        foodNames = new ArrayList<>();
        foodNamesAdapter = new FoodNamesAdapter(foodNames);

        // Set layout manager and adapter for RecyclerView
        recyclerFoodNames.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFoodNames.setAdapter(foodNamesAdapter);

        // Button click listener for adding food names
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = edtFoodName.getText().toString().trim();
                if (!foodName.isEmpty()) {
                    FoodList food = new FoodList(foodName);
                    foodNames.add(food);
                    foodNamesAdapter.notifyDataSetChanged();
                    edtFoodName.setText(""); // Clear EditText after adding food name
                } else {
                    Toast.makeText(getContext(), "Please enter a food name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Button click listener for calculating carbs and insulin
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodNamesAdapter.getItemCount() == 0) {
                    Toast.makeText(getContext(), "Please add some food items first", Toast.LENGTH_SHORT).show();
                    return;
                }

                String sugarValue = edtSugar.getText().toString().trim();
                if (sugarValue.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter sugar level value", Toast.LENGTH_SHORT).show();
                    return;
                }
                String insulinValue = edtInsulin.getText().toString().trim();
                if (insulinValue.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter insulin value", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                String foodItems = "";
                for (FoodList item : foodNamesAdapter.getFoodList()) {
                    foodItems += item.getFoodName() + " ";
                }
                String query = foodItems;
                String apiUrl = "https://api.calorieninjas.com/v1/nutrition?query=" + query;
                ApiCaller apiCaller = new ApiCaller();
                apiCaller.setApiCallerCallback(FoodEntryFragment.this);
                apiCaller.execute(apiUrl, "+Kymj4OzlDLXexZ3ZcOOVg==sDaXa4L4oS0Xg4m8");
            }
        });
    }

    private void calculateCarbsAndInsulin(double totalCals) {
        // Simulate calculation for demonstration purposes
        double carbs = totalCals / 4; // Assuming 4 calories per gram of carb
        double sugar = Double.parseDouble(edtSugar.getText().toString().trim());
        double insulin = Double.parseDouble(edtInsulin.getText().toString().trim());
        // Delay for 2 seconds to simulate calculation
        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // Hide loading progress bar
                        progressBar.setVisibility(View.GONE);
                        // Send result to Firebase with calculated calories
                        sendResultToFirebase(carbs, sugar, insulin, totalCals);
                        // Show result or navigate to another view to display result
                        Toast.makeText(getContext(), "Carbs and insulin calculated", Toast.LENGTH_SHORT).show();
                    }
                },
                2000);
    }

    private void sendResultToFirebase(double carbs, double sugar, double insulin, double calories) {
        Toast.makeText(getContext(), "save method called", Toast.LENGTH_SHORT).show();

        // Get date
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();

        // Get time
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String key = mDatabase.child("users").child(userId).child("carbinsulin").push().getKey();
            if (key != null) {
                CarbsInsulin carbsInsulin = new CarbsInsulin(carbs, sugar, insulin, calories, year, month + 1, dayOfMonth, hour, minute);
                mDatabase.child("users").child(userId).child("carbinsulin").child(key).setValue(carbsInsulin)
                        .addOnSuccessListener(aVoid -> {
                            // Data successfully written to Firebase
                            Toast.makeText(getContext(), "Result saved to Firebase", Toast.LENGTH_SHORT).show();
                            Log.d("FoodEntryFragment", "Data saved to Firebase");
                        })
                        .addOnFailureListener(e -> {
                            // Failed to write data to Firebase
                            Toast.makeText(getContext(), "Failed to save result to Firebase", Toast.LENGTH_SHORT).show();
                            Log.e("FoodEntryFragment", "Failed to save data to Firebase", e);
                        });
            }
        }
    }

    @Override
    public void onApiCallCompleted(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            double totalCals = 0;

            // Iterate through the items array
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                double calories = item.getDouble("calories");
                totalCals += calories;
            }

            // Calculate carbs and insulin
            calculateCarbsAndInsulin(totalCals);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
