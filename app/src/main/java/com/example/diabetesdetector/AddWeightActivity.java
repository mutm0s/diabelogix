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
//import com.example.diabetesdetector.models.Weight;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class AddWeightActivity extends AppCompatActivity {
//
//    private DatabaseReference mDatabase;
//    private EditText mWeightValue;
//    private EditText mWeightDate;
//    private EditText mWeightTime;
//    private Button mSaveButton;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_weight);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mWeightValue = findViewById(R.id.weight_value);
//        mWeightDate = findViewById(R.id.weight_date);
//        mWeightTime = findViewById(R.id.weight_time);
//        mSaveButton = findViewById(R.id.save_weight);
//
//        mSaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveWeight();
//            }
//        });
//    }
//
//    private void saveWeight() {
//        String value = mWeightValue.getText().toString().trim();
//        String date = mWeightDate.getText().toString().trim();
//        String time = mWeightTime.getText().toString().trim();
//
//        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
//            Toast.makeText(AddWeightActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        double weightValue;
//        try {
//            weightValue = Double.parseDouble(value);
//        } catch (NumberFormatException e) {
//            Toast.makeText(AddWeightActivity.this, "Invalid weight value", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String userId = getUid();
//        String key = mDatabase.child("users").child(userId).child("weight").push().getKey();
//        if (key != null) {
//            Weight weight = new Weight(weightValue, date, time);
//            mDatabase.child("users").child(userId).child("weight").child(key).setValue(weight)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(AddWeightActivity.this, "Weight saved", Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            Toast.makeText(AddWeightActivity.this, "Error saving weight", Toast.LENGTH_SHORT).show();
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

import com.example.diabetesdetector.models.Weight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWeightActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private EditText mWeightValue;
    private EditText mWeightDate;
    private EditText mWeightTime;
    private Button mSaveButton;

    private ImageView mBackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mWeightValue = findViewById(R.id.weight_value);
        mWeightDate = findViewById(R.id.weight_date);
        mWeightTime = findViewById(R.id.weight_time);
        mSaveButton = findViewById(R.id.save_weight);
        mBackButton = findViewById(R.id.btn_back);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWeight();
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

    private void saveWeight() {
        String value = mWeightValue.getText().toString().trim();
        String date = mWeightDate.getText().toString().trim();
        String time = mWeightTime.getText().toString().trim();

        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            Toast.makeText(AddWeightActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double weightValue;
        try {
            weightValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Toast.makeText(AddWeightActivity.this, "Invalid weight value", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String key = mDatabase.child("users").child(userId).child("weight").push().getKey();
            if (key != null) {
                Weight weight = new Weight(weightValue, date, time);
                mDatabase.child("users").child(userId).child("weight").child(key).setValue(weight)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddWeightActivity.this, "Weight saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddWeightActivity.this, "Error saving weight", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(AddWeightActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
