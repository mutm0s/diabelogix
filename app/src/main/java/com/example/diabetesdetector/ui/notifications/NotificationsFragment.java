package com.example.diabetesdetector.ui.notifications;


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
import com.example.diabetesdetector.adapters.NotificationAdapter;
import com.example.diabetesdetector.models.Bmi;
import com.example.diabetesdetector.models.CarbsInsulin;
import com.example.diabetesdetector.models.Glucose;
import com.example.diabetesdetector.models.Weight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private ListView notificationsListView;
    private NotificationAdapter adapter;
    private List<String> notifications;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private static final String TAG = "NotificationsFragment";

    private float glucoseValue = 0f;
    private float bmiValue = 0f;
    private float weightValue = 0f;
    private double carbs = 0;
    private double insulin = 0;
    private double sugar = 0;

    // Define standard limits
    private static final float WEIGHT_LIMIT = 100.0f;
    private static final float BMI_LIMIT = 25.0f;
    private static final double SUGAR_LIMIT = 140.0;
    private static final double GLUCOSE_LIMIT = 140.0;
    private static final double INSULIN_LIMIT = 25.0;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsListView = root.findViewById(R.id.notifications_list_view);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notifications);
        notificationsListView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            fetchGlucoseData(currentUser.getUid());
        }

        return root;
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

                displayWarnings();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read CarbsInsulin data", databaseError.toException());
            }
        });
    }

    private void displayWarnings() {
        if (weightValue > WEIGHT_LIMIT) {
            notifications.add("Weight exceeds the limit!");
        }

        if (bmiValue > BMI_LIMIT) {
            notifications.add("BMI exceeds the limit!");
        }

        if (sugar > SUGAR_LIMIT) {
            notifications.add("Sugar level exceeds the limit!");
        }

        if (glucoseValue > GLUCOSE_LIMIT) {
            notifications.add("Glucose level exceeds the limit!");
        }

        if (insulin > INSULIN_LIMIT) {
            notifications.add("Insulin level exceeds the limit!");
        }

        if (notifications.isEmpty()) {
            notifications.add("All vitals are within the normal range.");
        }

        adapter.notifyDataSetChanged();
    }
}
