package com.example.diabetesdetector.ui.dashboard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.Bmi;
import com.example.diabetesdetector.models.CarbsInsulin;
import com.example.diabetesdetector.models.Glucose;
import com.example.diabetesdetector.models.Weight;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";

    private PieChart pieChartNutrient;
    private PieChart pieChartGlucose;
    private LineChart lineChart;
    private BarChart barChart;

    private float glucoseValue;
    private float bmiValue;
    private float weightValue;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        // Initialize charts
        pieChartNutrient = root.findViewById(R.id.nutrientPieChart);
        pieChartGlucose = root.findViewById(R.id.carbsInsulinPieChart);
        lineChart = root.findViewById(R.id.lineChart);
        barChart = root.findViewById(R.id.barChart);

        // Fetch and display data
        fetchData();

        return root;
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
        fetchCarbsInsulinData(userId);

    }

    private void fetchGlucoseData(String userId) {
        mDatabase.child("users").child(userId).child("glucose").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Glucose glucose = snapshot.getValue(Glucose.class);
                    if (glucose != null) {
                        glucoseValue = (float) glucose.getValue(); // Store glucose value
                        Log.d(TAG, "Glucose data: " + glucoseValue);
                        fetchBmiData(userId);
                    }
                }
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
                        bmiValue = (float) bmi.getValue(); // Store BMI value
                        Log.d(TAG, "BMI data: " + bmiValue);
                        fetchWeightData(userId);
                    }
                }
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
                        weightValue = (float) weight.getValue(); // Store weight value
                        Log.d(TAG, "Weight data: " + weightValue);
                        fetchCarbsInsulinData(userId);
                    }
                }
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
                double carbs = 0;
                double insulin = 0;
                double sugar = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CarbsInsulin carbsInsulin = snapshot.getValue(CarbsInsulin.class);
                    if (carbsInsulin != null) {
                        Log.d(TAG, "CarbsInsulin data: Carbs=" + carbsInsulin.getCarbs() + ", Insulin=" + carbsInsulin.getInsulin() +
                                ", Sugar=" + carbsInsulin.getSugar() + ", Calories=" + carbsInsulin.getCalories());

                        // Assign fetched values
                        carbs = carbsInsulin.getCarbs();
                        insulin = carbsInsulin.getInsulin();
                        sugar = carbsInsulin.getSugar();
                    }
                }

                setupNutrientPieChart((float) carbs, (float) insulin, (float) sugar);
                // Call setupGlucosePieChart with fetched glucose, bmi, and weight values
                setupGlucosePieChart(glucoseValue, bmiValue, weightValue);

                setupLineChart((float) carbs, (float) insulin, (float) sugar);
                setupBarChart((float) carbs, (float) insulin, (float) sugar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read CarbsInsulin data", databaseError.toException());
            }
        });
    }

    private void setupGlucosePieChart(float glucoseValue, float bmiValue, float weightValue) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(glucoseValue, "Glucose"));
        entries.add(new PieEntry(bmiValue, "BMI"));
        entries.add(new PieEntry(weightValue, "Weight"));

        PieDataSet dataSet = new PieDataSet(entries, "Glucose vs BMI & Weight");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        pieChartGlucose.setData(data);

        // Legend setup
        Legend legend = pieChartGlucose.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);

        // Refresh chart
        pieChartGlucose.invalidate();
    }


    private void setupNutrientPieChart(double carbs, double insulin, double sugar) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) carbs, "Carbs"));
        entries.add(new PieEntry((float) insulin, "Insulin"));
        entries.add(new PieEntry((float) sugar, "Sugar"));
        entries.add(new PieEntry(100 - (float) (carbs + insulin + sugar), "Calories"));

        PieDataSet dataSet = new PieDataSet(entries, "Nutrient Breakdown");
        dataSet.setValueTextSize(12f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        pieChartNutrient.setData(data);

        // Legend setup for pieChartNutrient
        Legend legendNutrient = pieChartNutrient.getLegend();
        legendNutrient.setForm(Legend.LegendForm.CIRCLE);
        legendNutrient.setTextSize(12f);
        legendNutrient.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legendNutrient.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legendNutrient.setOrientation(Legend.LegendOrientation.VERTICAL);
        legendNutrient.setDrawInside(false);

        // Refresh chart
        pieChartNutrient.invalidate();
    }

    private void setupLineChart(double carbs, double insulin, double sugar) {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, (float) carbs));
        entries.add(new Entry(1, (float) insulin));
        entries.add(new Entry(2, (float) sugar));

        LineDataSet dataSet = new LineDataSet(entries, "Nutrient Levels");

        // Line customization
        dataSet.setColor(getResources().getColor(R.color.colorPrimary, requireContext().getTheme()));
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark, requireContext().getTheme()));
        dataSet.setLineWidth(2f);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);

        // X-axis customization
        String[] nutrients = {"Carbs", "Insulin", "Sugar"};
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(nutrients));
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextSize(12f);

        // Y-axis customization
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setTextSize(12f);

        // Chart description
        lineChart.getDescription().setEnabled(false);

        // Legend customization
        Legend legendLine = lineChart.getLegend();
        legendLine.setTextSize(12f);
        legendLine.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legendLine.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legendLine.setOrientation(Legend.LegendOrientation.VERTICAL);
        legendLine.setDrawInside(false);

        // Refresh chart
        lineChart.invalidate();
    }

    private void setupBarChart(double carbs, double insulin, double sugar) {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) carbs));
        entries.add(new BarEntry(1, (float) insulin));
        entries.add(new BarEntry(2, (float) sugar));

        BarDataSet dataSet = new BarDataSet(entries, "Nutrient Levels");

        // Bar customization
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark, requireContext().getTheme()));
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // X-axis customization
        String[] nutrients = {"Carbs", "Insulin", "Sugar"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(nutrients));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(12f);

        // Y-axis customization
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setTextSize(12f);

        // Chart description
        barChart.getDescription().setEnabled(false);

        // Legend customization
        Legend legendBar = barChart.getLegend();
        legendBar.setTextSize(12f);
        legendBar.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legendBar.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legendBar.setOrientation(Legend.LegendOrientation.VERTICAL);
        legendBar.setDrawInside(false);

        // Refresh chart
        barChart.invalidate();
    }
}