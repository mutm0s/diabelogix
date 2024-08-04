//package com.example.diabetesdetector;
//
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
//import com.example.diabetesdetector.models.Glucose;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class AddGlucoseActivity extends AppCompatActivity {
//
//    private DatabaseReference mDatabase;
//    private EditText mGlucoseValue;
//    private EditText mGlucoseDate;
//    private EditText mGlucoseTime;
//    private Button mSaveButton;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_glucose);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mGlucoseValue = findViewById(R.id.glucose_value);
//        mGlucoseDate = findViewById(R.id.glucose_date);
//        mGlucoseTime = findViewById(R.id.glucose_time);
//        mSaveButton = findViewById(R.id.save_glucose);
//
//        mSaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveGlucose();
//            }
//        });
//    }
//
//    private void saveGlucose() {
//        String value = mGlucoseValue.getText().toString().trim();
//        String date = mGlucoseDate.getText().toString().trim();
//        String time = mGlucoseTime.getText().toString().trim();
//
//        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
//            Toast.makeText(AddGlucoseActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        double glucoseValue;
//        try {
//            glucoseValue = Double.parseDouble(value);
//        } catch (NumberFormatException e) {
//            Toast.makeText(AddGlucoseActivity.this, "Invalid glucose value", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = getUid();
//        String key = mDatabase.child("users").child(userId).child("glucose").push().getKey();
//        if (key != null) {
//            Glucose glucose = new Glucose(glucoseValue, date, time);
//            mDatabase.child("users").child(userId).child("glucose").child(key).setValue(glucose)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(AddGlucoseActivity.this, "Glucose saved", Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            Toast.makeText(AddGlucoseActivity.this, "Error saving glucose", Toast.LENGTH_SHORT).show();
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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diabetesdetector.models.Glucose;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddGlucoseActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private EditText mGlucoseValue;
    private EditText mGlucoseDate;
    private EditText mGlucoseTime;
    private Button mSaveButton;
    private ImageView mBackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_glucose);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mGlucoseValue = findViewById(R.id.glucose_value);
        mGlucoseDate = findViewById(R.id.glucose_date);
        mGlucoseTime = findViewById(R.id.glucose_time);
        mSaveButton = findViewById(R.id.save_glucose);
        mBackButton = findViewById(R.id.btn_back);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGlucose();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to DailyDashboardFragment
                onBackPressed();
            }
        });
    }

    private void saveGlucose() {
        String value = mGlucoseValue.getText().toString().trim();
        String date = mGlucoseDate.getText().toString().trim();
        String time = mGlucoseTime.getText().toString().trim();

        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            Toast.makeText(AddGlucoseActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double glucoseValue;
        try {
            glucoseValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Toast.makeText(AddGlucoseActivity.this, "Invalid glucose value", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String key = mDatabase.child("users").child(userId).child("glucose").push().getKey();
            if (key != null) {
                Glucose glucose = new Glucose(glucoseValue, date, time);
                mDatabase.child("users").child(userId).child("glucose").child(key).setValue(glucose)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddGlucoseActivity.this, "Glucose saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddGlucoseActivity.this, "Error saving glucose", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(AddGlucoseActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
