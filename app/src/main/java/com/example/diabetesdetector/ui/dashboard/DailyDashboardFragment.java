package com.example.diabetesdetector.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.diabetesdetector.AddBmiActivity;
import com.example.diabetesdetector.AddGlucoseActivity;
import com.example.diabetesdetector.AddWeightActivity;
import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.Bmi;
import com.example.diabetesdetector.models.Glucose;
import com.example.diabetesdetector.models.Weight;
import com.example.diabetesdetector.models.CarbsInsulin;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyDashboardFragment extends Fragment {

    private static final String TAG = "DailyDashboardFragment";

    private TextView mTextDateTime;
    private TextView mTextGlucoseNumber;
    private TextView mTextBmiNumber;
    private TextView mTextWeightNumber;
    private LineChart mGlucoseTrendChart;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_dashboard, container, false);

        mTextDateTime = view.findViewById(R.id.text_date_time);
        mTextGlucoseNumber = view.findViewById(R.id.text_glucose_number);
        mTextBmiNumber = view.findViewById(R.id.text_bmi_number);
        mTextWeightNumber = view.findViewById(R.id.text_weight_number);
        mGlucoseTrendChart = view.findViewById(R.id.glucose_trend_chart);
        Button bmiButton = view.findViewById(R.id.bmi);
        Button weightButton = view.findViewById(R.id.weight);
        Button glucoseButton = view.findViewById(R.id.glucose);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        bmiButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddBmiActivity.class)));
        weightButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddWeightActivity.class)));
        glucoseButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddGlucoseActivity.class)));

        // Set current date
        setCurrentDate();

        fetchData();

        return view;
    }

    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        mTextDateTime.setText(currentDate);
    }

    private void fetchData() {
        if (currentUser == null) {
            Log.w(TAG, "User is not logged in.");
            return;
        }

        String userId = currentUser.getUid();

        fetchGlucoseData(userId);
        fetchBmiData(userId);
        fetchWeightData(userId);
        fetchCarbsInsulinData(userId); // Fetch carbs, insulin, sugar, calories
    }

    private void fetchGlucoseData(String userId) {
        mDatabase.child("users").child(userId).child("glucose").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Glucose glucose = snapshot.getValue(Glucose.class);
                    if (glucose != null) {
                        Log.d(TAG, "Glucose data: " + glucose.getValue());
                        mTextGlucoseNumber.setText(String.valueOf(glucose.getValue()));
                    }
                }

                // After fetching glucose data, fetch other data and setup chart
                fetchBmiData(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read glucose data", databaseError.toException());
            }
        });
    }

    private void fetchBmiData(String userId) {
        mDatabase.child("users").child(userId).child("bmi").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bmi bmi = snapshot.getValue(Bmi.class);
                    if (bmi != null) {
                        Log.d(TAG, "BMI data: " + bmi.getValue());
                        mTextBmiNumber.setText(String.valueOf(bmi.getValue()));
                    }
                }

                // After fetching BMI data, fetch weight data
                fetchWeightData(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read BMI data", databaseError.toException());
            }
        });
    }

    private void fetchWeightData(String userId) {
        mDatabase.child("users").child(userId).child("weight").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Weight weight = snapshot.getValue(Weight.class);
                    if (weight != null) {
                        Log.d(TAG, "Weight data: " + weight.getValue());
                        mTextWeightNumber.setText(String.valueOf(weight.getValue()));
                    }
                }

                // After fetching weight data, fetch carbs and insulin data
                fetchCarbsInsulinData(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read weight data", databaseError.toException());
            }
        });
    }

    private void fetchCarbsInsulinData(String userId) {
        mDatabase.child("users").child(userId).child("carbinsulin").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CarbsInsulin carbsInsulin = snapshot.getValue(CarbsInsulin.class);
                    if (carbsInsulin != null) {
                        Log.d(TAG, "CarbsInsulin data: Carbs=" + carbsInsulin.getCarbs() + ", Insulin=" + carbsInsulin.getInsulin() +
                                ", Sugar=" + carbsInsulin.getSugar() + ", Calories=" + carbsInsulin.getCalories());

                        // Log the values that will populate the chart
                        double carbs = carbsInsulin.getCarbs();
                        double insulin = carbsInsulin.getInsulin();
                        double sugar = carbsInsulin.getSugar();
                        double calories = carbsInsulin.getCalories();
                        Log.d(TAG, "Chart data: Carbs=" + carbs + ", Insulin=" + insulin +
                                ", Sugar=" + sugar + ", Calories=" + calories);

                        // Setup the chart with fetched data
                        setupChart(carbs, insulin, sugar, calories);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read CarbsInsulin data", databaseError.toException());
            }
        });
    }

    private void setupChart(double carbs, double insulin, double sugar, double calories) {
        mGlucoseTrendChart.setDrawGridBackground(false);
        mGlucoseTrendChart.getDescription().setEnabled(false);
        mGlucoseTrendChart.setTouchEnabled(true);
        mGlucoseTrendChart.setDragEnabled(true);
        mGlucoseTrendChart.setScaleEnabled(true);
        mGlucoseTrendChart.setPinchZoom(true);

        Legend legend = mGlucoseTrendChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);

        // Define labels for the legend
        LegendEntry[] legendEntries = new LegendEntry[4];
        legendEntries[0] = new LegendEntry("Carbs", Legend.LegendForm.LINE, 10f, 2f, null, Color.RED);
        legendEntries[1] = new LegendEntry("Insulin", Legend.LegendForm.LINE, 10f, 2f, null, Color.GREEN);
        legendEntries[2] = new LegendEntry("Sugar", Legend.LegendForm.LINE, 10f, 2f, null, Color.BLUE);
        legendEntries[3] = new LegendEntry("Calories", Legend.LegendForm.LINE, 10f, 2f, null, Color.YELLOW);
        legend.setCustom(legendEntries);

        // Create a list of Entry objects for each dataset
        List<Entry> carbEntries = new ArrayList<>();
        List<Entry> insulinEntries = new ArrayList<>();
        List<Entry> sugarEntries = new ArrayList<>();
        List<Entry> calorieEntries = new ArrayList<>();

        // Populate the lists with sample data points
        carbEntries.add(new Entry(1, (float) carbs));
        insulinEntries.add(new Entry(1, (float) insulin));
        sugarEntries.add(new Entry(1, (float) sugar));
        calorieEntries.add(new Entry(1, (float) calories));

        // Create LineDataSets for each list of entries
        LineDataSet carbDataSet = new LineDataSet(carbEntries, "Carbs");
        carbDataSet.setColor(Color.RED);
        carbDataSet.setLineWidth(2f);
        carbDataSet.setCircleColor(Color.RED);

        LineDataSet insulinDataSet = new LineDataSet(insulinEntries, "Insulin");
        insulinDataSet.setColor(Color.GREEN);
        insulinDataSet.setLineWidth(2f);
        insulinDataSet.setCircleColor(Color.GREEN);

        LineDataSet sugarDataSet = new LineDataSet(sugarEntries, "Sugar");
        sugarDataSet.setColor(Color.BLUE);
        sugarDataSet.setLineWidth(2f);
        sugarDataSet.setCircleColor(Color.BLUE);

        LineDataSet calorieDataSet = new LineDataSet(calorieEntries, "Calories");
        calorieDataSet.setColor(Color.YELLOW);
        calorieDataSet.setLineWidth(2f);
        calorieDataSet.setCircleColor(Color.YELLOW);

        // Add the datasets to the chart
        LineData lineData = new LineData(carbDataSet, insulinDataSet, sugarDataSet, calorieDataSet);
        mGlucoseTrendChart.setData(lineData);
        mGlucoseTrendChart.invalidate(); // Refresh the chart
    }
}





//package com.example.diabetesdetector.ui.dashboard;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.diabetesdetector.AddBmiActivity;
//import com.example.diabetesdetector.AddGlucoseActivity;
//import com.example.diabetesdetector.AddWeightActivity;
//import com.example.diabetesdetector.R;
//import com.example.diabetesdetector.models.Bmi;
//import com.example.diabetesdetector.models.Glucose;
//import com.example.diabetesdetector.models.Weight;
//import com.example.diabetesdetector.models.CarbsInsulin;
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.Legend;
//import com.github.mikephil.charting.components.LegendEntry;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class DailyDashboardFragment extends Fragment {
//
//    private static final String TAG = "DailyDashboardFragment";
//
//    private TextView mTextDateTime;
//    private TextView mTextGlucoseNumber;
//    private TextView mTextBmiNumber;
//    private TextView mTextWeightNumber;
//    private LineChart mGlucoseTrendChart;
//    private DatabaseReference mDatabase;
//    private FirebaseUser currentUser;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_daily_dashboard, container, false);
//
//        mTextDateTime = view.findViewById(R.id.text_date_time);
//        mTextGlucoseNumber = view.findViewById(R.id.text_glucose_number);
//        mTextBmiNumber = view.findViewById(R.id.text_bmi_number);
//        mTextWeightNumber = view.findViewById(R.id.text_weight_number);
//        mGlucoseTrendChart = view.findViewById(R.id.glucose_trend_chart);
//        Button bmiButton = view.findViewById(R.id.bmi);
//        Button weightButton = view.findViewById(R.id.weight);
//        Button glucoseButton = view.findViewById(R.id.glucose);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        bmiButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddBmiActivity.class)));
//        weightButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddWeightActivity.class)));
//        glucoseButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddGlucoseActivity.class)));
//
//        fetchData();
//
//        return view;
//    }
//
//    private void fetchData() {
//        if (currentUser == null) {
//            Log.w(TAG, "User is not logged in.");
//            return;
//        }
//
//        String userId = currentUser.getUid();
//
//        fetchGlucoseData(userId);
//        fetchBmiData(userId);
//        fetchWeightData(userId);
//        fetchCarbsInsulinData(userId); // Fetch carbs, insulin, sugar, calories
//    }
//
//    private void fetchGlucoseData(String userId) {
//        mDatabase.child("users").child(userId).child("glucose").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Glucose glucose = snapshot.getValue(Glucose.class);
//                    if (glucose != null) {
//                        Log.d(TAG, "Glucose data: " + glucose.getValue());
//                        mTextGlucoseNumber.setText(String.valueOf(glucose.getValue()));
//                    }
//                }
//
//                // After fetching glucose data, fetch other data and setup chart
//                fetchBmiData(userId);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to read glucose data", databaseError.toException());
//            }
//        });
//    }
//
//    private void fetchBmiData(String userId) {
//        mDatabase.child("users").child(userId).child("bmi").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Bmi bmi = snapshot.getValue(Bmi.class);
//                    if (bmi != null) {
//                        Log.d(TAG, "BMI data: " + bmi.getValue());
//                        mTextBmiNumber.setText(String.valueOf(bmi.getValue()));
//                    }
//                }
//
//                // After fetching BMI data, fetch weight data
//                fetchWeightData(userId);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to read BMI data", databaseError.toException());
//            }
//        });
//    }
//
//    private void fetchWeightData(String userId) {
//        mDatabase.child("users").child(userId).child("weight").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Weight weight = snapshot.getValue(Weight.class);
//                    if (weight != null) {
//                        Log.d(TAG, "Weight data: " + weight.getValue());
//                        mTextWeightNumber.setText(String.valueOf(weight.getValue()));
//                    }
//                }
//
//                // After fetching weight data, fetch carbs and insulin data
//                fetchCarbsInsulinData(userId);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to read weight data", databaseError.toException());
//            }
//        });
//    }
//
//    private void fetchCarbsInsulinData(String userId) {
//        mDatabase.child("users").child(userId).child("carbinsulin").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    CarbsInsulin carbsInsulin = snapshot.getValue(CarbsInsulin.class);
//                    if (carbsInsulin != null) {
//                        Log.d(TAG, "CarbsInsulin data: Carbs=" + carbsInsulin.getCarbs() + ", Insulin=" + carbsInsulin.getInsulin() +
//                                ", Sugar=" + carbsInsulin.getSugar() + ", Calories=" + carbsInsulin.getCalories());
//
//                        // Log the values that will populate the chart
//                        double carbs = carbsInsulin.getCarbs();
//                        double insulin = carbsInsulin.getInsulin();
//                        double sugar = carbsInsulin.getSugar();
//                        double calories = carbsInsulin.getCalories();
//                        Log.d(TAG, "Chart data: Carbs=" + carbs + ", Insulin=" + insulin +
//                                ", Sugar=" + sugar + ", Calories=" + calories);
//
//                        // Setup the chart with fetched data
//                        setupChart(carbs, insulin, sugar, calories);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "Failed to read CarbsInsulin data", databaseError.toException());
//            }
//        });
//    }
//
//    private void setupChart(double carbs, double insulin, double sugar, double calories) {
//        mGlucoseTrendChart.setDrawGridBackground(false);
//        mGlucoseTrendChart.getDescription().setEnabled(false);
//        mGlucoseTrendChart.setTouchEnabled(true);
//        mGlucoseTrendChart.setDragEnabled(true);
//        mGlucoseTrendChart.setScaleEnabled(true);
//        mGlucoseTrendChart.setPinchZoom(true);
//
//        Legend legend = mGlucoseTrendChart.getLegend();
//        legend.setForm(Legend.LegendForm.LINE);
//        legend.setTextSize(12f);
//        legend.setTextColor(Color.BLACK);
//
//        // Define labels for the legend
//        LegendEntry[] legendEntries = new LegendEntry[4]; // Adjust the number based on your datasets
//        legendEntries[0] = new LegendEntry("Carbs", Legend.LegendForm.LINE, 8f, 2f, null, ContextCompat.getColor(requireContext(), R.color.colorPrimary));
//        legendEntries[1] = new LegendEntry("Insulin", Legend.LegendForm.LINE, 8f, 2f, null, ContextCompat.getColor(requireContext(), R.color.colorAccent));
//        legendEntries[2] = new LegendEntry("Sugar", Legend.LegendForm.LINE, 8f, 2f, null, ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
//        legendEntries[3] = new LegendEntry("Calories", Legend.LegendForm.LINE, 8f, 2f, null, ContextCompat.getColor(requireContext(), R.color.colorPrimaryLight));
//
//        legend.setCustom(legendEntries);
//
//        List<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(0, (float) carbs, "Carbs"));
//        entries.add(new Entry(1, (float) insulin, "Insulin"));
//        entries.add(new Entry(2, (float) sugar, "Sugar"));
//        entries.add(new Entry(3, (float) calories, "Calories"));
//
//        LineDataSet dataSet = new LineDataSet(entries, "Data");
//        dataSet.setColors(new int[] {
//                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
//                ContextCompat.getColor(requireContext(), R.color.colorAccent),
//                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark),
//                ContextCompat.getColor(requireContext(), R.color.colorPrimaryLight)
//        });
//
//        // Setting value text colors as a List of Integer values
//        dataSet.setValueTextColors(Arrays.asList(
//                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
//                ContextCompat.getColor(requireContext(), R.color.colorAccent),
//                ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark),
//                ContextCompat.getColor(requireContext(), R.color.colorPrimaryLight)
//        ));
//
//        dataSet.setLineWidth(2f);
//        dataSet.setDrawCircles(true);
//        dataSet.setDrawValues(true);
//
//        LineData lineData = new LineData(dataSet);
//        mGlucoseTrendChart.setData(lineData);
//        mGlucoseTrendChart.invalidate(); // refresh the chart
//    }
//
//
//
//}
//
