package com.example.diabetesdetector.ui.glucose;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.GlucoseReading;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GlucoseMonitoringFragment extends Fragment {


    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText editTextGlucose;
    private Button buttonSubmit;

    private DatabaseReference databaseReference;

    public GlucoseMonitoringFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glucose_monitoring, container, false);

        // Initialize views
        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);
        editTextGlucose = view.findViewById(R.id.editTextGlucose);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);

        // Set current date and time as default
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        timePicker.setIs24HourView(true);
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("glucoseReadings");

        // Set onClickListener for submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitGlucoseReading();
            }
        });

        return view;
    }

    private void submitGlucoseReading() {
        // Get date
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();

        // Get time
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Get glucose reading
        String glucoseReading = editTextGlucose.getText().toString().trim();
        if (glucoseReading.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a glucose reading", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique key for each reading
        String key = databaseReference.push().getKey();

        // Prepare data to be saved
        Map<String, Object> readingData = new HashMap<>();
        readingData.put("date", year + "-" + (month + 1) + "-" + dayOfMonth);
        readingData.put("time", String.format("%02d:%02d", hour, minute));
        readingData.put("glucoseReading", glucoseReading);

        // Save data to Firebase
        if (key != null) {
            databaseReference.child(key).setValue(readingData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Glucose reading saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save glucose reading", Toast.LENGTH_SHORT).show());
        }
    }
}
