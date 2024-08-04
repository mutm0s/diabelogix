//package com.example.diabetesdetector;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.diabetesdetector.models.Doctor;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class AddDoctorActivity extends AppCompatActivity {
//
//    private DatabaseReference mDatabase;
//
//    private EditText mDoctorName;
//    private EditText mDoctorSpecialty;
//    private EditText mDoctorRating;
//    private EditText mDoctorDistance;
//    private EditText mDoctorImageUrl;
//    private Button mSaveButton;
//
//    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_doctor);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        mDoctorName = findViewById(R.id.doctor_name);
//        mDoctorSpecialty = findViewById(R.id.doctor_specialty);
//        mDoctorRating = findViewById(R.id.doctor_rating);
//        mDoctorDistance = findViewById(R.id.doctor_distance);
//        mDoctorImageUrl = findViewById(R.id.doctor_image);
//        mSaveButton = findViewById(R.id.save_btn);
//
//        mSaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveDoctor();
//            }
//        });
//    }
//
//    private void saveDoctor() {
//        String name = mDoctorName.getText().toString().trim();
//        String specialty = mDoctorSpecialty.getText().toString().trim();
//        String ratingStr = mDoctorRating.getText().toString().trim();
//        String distance = mDoctorDistance.getText().toString().trim();
//        String imageUrl = mDoctorImageUrl.getText().toString().trim();
//
//        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(specialty) || TextUtils.isEmpty(ratingStr) ||
//                TextUtils.isEmpty(distance) || TextUtils.isEmpty(imageUrl)) {
//            Toast.makeText(AddDoctorActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        double rating;
//        try {
//            rating = Double.parseDouble(ratingStr);
//        } catch (NumberFormatException e) {
//            Toast.makeText(AddDoctorActivity.this, "Invalid rating", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Doctor doctor = new Doctor(name, specialty, rating, distance, imageUrl);
//
//        String userId = getUid();
//        String key = mDatabase.child("users").child(userId).child("doctors").push().getKey();
//        if (key != null) {
//            mDatabase.child("users").child(userId).child("doctors").child(key).setValue(doctor)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(AddDoctorActivity.this, "Doctor saved", Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            Toast.makeText(AddDoctorActivity.this, "Error saving doctor", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
//
//    private String getUid() {
//        // Replace this with your logic to get the current user's UID
//        return "sampleUid";
//    }
//}
//
package com.example.diabetesdetector;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diabetesdetector.models.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDoctorActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private EditText mDoctorName;
    private EditText mDoctorSpecialty;
    private EditText mDoctorRating;
    private EditText mDoctorDistance;
    private EditText mDoctorImageUrl;
    private Button mSaveButton;
    private ImageView mBackButton;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDoctorName = findViewById(R.id.doctor_name);
        mDoctorSpecialty = findViewById(R.id.doctor_specialty);
        mDoctorRating = findViewById(R.id.doctor_rating);
        mDoctorDistance = findViewById(R.id.doctor_distance);
        mDoctorImageUrl = findViewById(R.id.doctor_image);
        mSaveButton = findViewById(R.id.save_btn);
        mBackButton = findViewById(R.id.btn_back);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDoctor();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void saveDoctor() {
        String name = mDoctorName.getText().toString().trim();
        String specialty = mDoctorSpecialty.getText().toString().trim();
        String ratingStr = mDoctorRating.getText().toString().trim();
        String distance = mDoctorDistance.getText().toString().trim();
        String imageUrl = mDoctorImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(specialty) || TextUtils.isEmpty(ratingStr) ||
                TextUtils.isEmpty(distance) || TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(AddDoctorActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingStr);
        } catch (NumberFormatException e) {
            Toast.makeText(AddDoctorActivity.this, "Invalid rating", Toast.LENGTH_SHORT).show();
            return;
        }

        Doctor doctor = new Doctor(name, specialty, rating, distance, imageUrl);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String key = mDatabase.child("users").child(userId).child("doctors").push().getKey();
            if (key != null) {
                mDatabase.child("users").child(userId).child("doctors").child(key).setValue(doctor)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddDoctorActivity.this, "Doctor saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddDoctorActivity.this, "Error saving doctor", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(AddDoctorActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
