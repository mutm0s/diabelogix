//
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
//import com.example.diabetesdetector.models.Bmi;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class AddBmiActivity extends AppCompatActivity {
//
//    private DatabaseReference mDatabase;
//    private FirebaseUser currentUser;
//
//    private EditText mBmiValue;
//    private EditText mBmiDate;
//    private EditText mBmiTime;
//    private Button mSaveButton;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_bmi);
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        mBmiValue = findViewById(R.id.bmi_value);
//        mBmiDate = findViewById(R.id.bmi_date);
//        mBmiTime = findViewById(R.id.bmi_time);
//        mSaveButton = findViewById(R.id.save_bmi);
//
//        mSaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveBmi();
//            }
//        });
//    }
//
//    private void saveBmi() {
//        String value = mBmiValue.getText().toString().trim();
//        String date = mBmiDate.getText().toString().trim();
//        String time = mBmiTime.getText().toString().trim();
//
//        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
//            Toast.makeText(AddBmiActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        double bmiValue;
//        try {
//            bmiValue = Double.parseDouble(value);
//        } catch (NumberFormatException e) {
//            Toast.makeText(AddBmiActivity.this, "Invalid BMI value", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (currentUser != null) {
//            String userId = currentUser.getUid();
//            String key = mDatabase.child("users").child(userId).child("bmi").push().getKey();
//            if (key != null) {
//                Bmi bmi = new Bmi(bmiValue, date, time);
//                mDatabase.child("users").child(userId).child("bmi").child(key).setValue(bmi)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(AddBmiActivity.this, "BMI saved", Toast.LENGTH_SHORT).show();
//                                finish();
//                            } else {
//                                Toast.makeText(AddBmiActivity.this, "Error saving BMI", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        } else {
//            Toast.makeText(AddBmiActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
//        }
//    }
//}


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
import androidx.fragment.app.FragmentManager;

import com.example.diabetesdetector.models.Bmi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBmiActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    private EditText mBmiValue;
    private EditText mBmiDate;
    private EditText mBmiTime;
    private Button mSaveButton;
    private ImageView mBackButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bmi);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mBmiValue = findViewById(R.id.bmi_value);
        mBmiDate = findViewById(R.id.bmi_date);
        mBmiTime = findViewById(R.id.bmi_time);
        mSaveButton = findViewById(R.id.save_bmi);
        mBackButton = findViewById(R.id.btn_back);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBmi();
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

    private void saveBmi() {
        String value = mBmiValue.getText().toString().trim();
        String date = mBmiDate.getText().toString().trim();
        String time = mBmiTime.getText().toString().trim();

        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            Toast.makeText(AddBmiActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double bmiValue;
        try {
            bmiValue = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Toast.makeText(AddBmiActivity.this, "Invalid BMI value", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            String key = mDatabase.child("users").child(userId).child("bmi").push().getKey();
            if (key != null) {
                Bmi bmi = new Bmi(bmiValue, date, time);
                mDatabase.child("users").child(userId).child("bmi").child(key).setValue(bmi)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddBmiActivity.this, "BMI saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddBmiActivity.this, "Error saving BMI", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(AddBmiActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
