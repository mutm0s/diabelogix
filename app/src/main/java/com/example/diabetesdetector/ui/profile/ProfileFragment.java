package com.example.diabetesdetector.ui.profile;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.diabetesdetector.LoginActivity;
import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.CarbsInsulin;
import com.example.diabetesdetector.models.Weight;
import com.example.diabetesdetector.ui.appointment.AppointmentsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class ProfileFragment extends Fragment {

    private ImageView profilePicture;
    private TextView username;
    private TextView heartRate;
    private TextView calories;
    private TextView weight;
    private float weightValue;
    private LinearLayout mySaved;
    private LinearLayout appointments;
    private LinearLayout faqs;
    private LinearLayout logout;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePicture = root.findViewById(R.id.profile_picture);
        username = root.findViewById(R.id.username);
        heartRate = root.findViewById(R.id.heart_rate);
        calories = root.findViewById(R.id.calories);
        weight = root.findViewById(R.id.weight);
        mySaved = root.findViewById(R.id.my_saved);
        appointments = root.findViewById(R.id.appointments);
        faqs = root.findViewById(R.id.faqs);
        logout = root.findViewById(R.id.logout);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            loadUserProfile(currentUser.getUid());
        }

        mySaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle My Saved click
                // Example: startActivity(new Intent(getContext(), MySavedActivity.class));
            }
        });

        appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new AppointmentsFragment());
            }
        });

        faqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle FAQs click
                // Example: startActivity(new Intent(getContext(), FaqsActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Logout click
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        return root;
    }

    private void loadUserProfile(String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve name from Firebase and set it
                    String name = snapshot.child("email").getValue(String.class);
                    username.setText(name != null ? name : "Name not found");

                    // Fetch additional data and update UI
                    fetchWeightData(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("ProfileFragment", "Error loading user profile: " + error.getMessage());
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
                double insulin = 0;
                double sugar = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CarbsInsulin carbsInsulin = snapshot.getValue(CarbsInsulin.class);
                    if (carbsInsulin != null) {
                        Log.d(TAG, "CarbsInsulin data: Carbs=" + carbsInsulin.getCarbs() + ", Insulin=" + carbsInsulin.getInsulin() +
                                ", Sugar=" + carbsInsulin.getSugar() + ", Calories=" + carbsInsulin.getCalories());

                        // Assign fetched values
                        insulin = carbsInsulin.getInsulin();
                        sugar = carbsInsulin.getSugar();

                       updateUI(weightValue,insulin, sugar);
                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read CarbsInsulin data", databaseError.toException());
            }
        });
    }

    private void updateUI(float weightValue,  double insulin, double sugar) {
        // Update UI elements with fetched data
        heartRate.setText("Insulin\n" + insulin);
        calories.setText("Calories\n" + sugar);
        weight.setText("Weight\n" + weightValue);
    }

    private void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
