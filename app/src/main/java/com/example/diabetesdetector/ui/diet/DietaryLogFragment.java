package com.example.diabetesdetector.ui.diet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.adapters.RecommendationAdapter;
import com.example.diabetesdetector.apiService.KetoDietService;
import com.example.diabetesdetector.models.Recommendation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DietaryLogFragment extends Fragment implements RecommendationAdapter.OnItemClickListener {

    private EditText foodNameEditText;
    private EditText quantityEditText;
    private Button addFoodButton;
    private Button saveAddFoodButton;
    private ListView pastLogsListView;
    private ListView currentLogsListView;
    private TextView recommendationTextView;
    private RecyclerView recommendationRecyclerView;
    private RecommendationAdapter recommendationAdapter;

    private ArrayList<String> pastLogs;
    private ArrayList<String> currentLogs; // New list for current logs
    private ArrayAdapter<String> pastLogsAdapter;
    private ArrayAdapter<String> currentLogsAdapter; // Adapter for the current logs ListView

    private DatabaseReference databaseReference;
    private String userId;

    public DietaryLogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_log, container, false);

        // Initialize views
        foodNameEditText = view.findViewById(R.id.editText_food_name);
        quantityEditText = view.findViewById(R.id.editText_quantity);
        addFoodButton = view.findViewById(R.id.button_add_food);
        saveAddFoodButton = view.findViewById(R.id.save_add_food);
        pastLogsListView = view.findViewById(R.id.listView_past_logs);
        currentLogsListView = view.findViewById(R.id.listView_current_logs);
        recommendationTextView = view.findViewById(R.id.textView_recommendation_title);
        recommendationRecyclerView = view.findViewById(R.id.recyclerView_recommendations);

        // Initialize past logs list
        pastLogs = new ArrayList<>();
        currentLogs = new ArrayList<>(); // Initialize current logs list
        pastLogsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, pastLogs);
        currentLogsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, currentLogs); // Initialize adapter for current logs
        pastLogsListView.setAdapter(pastLogsAdapter);
        currentLogsListView.setAdapter(currentLogsAdapter); // Set adapter for current logs

        // Initialize recommendation list and adapter
        List<Recommendation> recommendations = new ArrayList<>();
        recommendationAdapter = new RecommendationAdapter(recommendations, this);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationRecyclerView.setAdapter(recommendationAdapter);

        // Initialize Firebase Database reference and user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();
        } else {
            // Handle user not logged in
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Load past logs from Firebase
        loadPastLogsFromFirebase();

        // Add button click listener
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToCurrentLog(); // Change to add to current log
            }
        });

        // Save button click listener
        saveAddFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFoodLogToFirebase();
            }
        });

        // Fetch recommendations from the API
        fetchRecommendationsFromApi();

        return view;
    }

    private void fetchRecommendationsFromApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://keto-diet.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KetoDietService service = retrofit.create(KetoDietService.class);
        Call<List<Recommendation>> call = service.getRecommendations();
        call.enqueue(new Callback<List<Recommendation>>() {
            @Override
            public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recommendation> recommendations = response.body();
                    recommendationAdapter.setRecommendations(recommendations);
                    recommendationAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch recommendations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
                Toast.makeText(requireContext(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to add food to the current log
    private void addFoodToCurrentLog() {
        String foodName = foodNameEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();

        if (foodName.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String log = foodName + ": " + quantity;
        currentLogs.add(log); // Add to current logs list
        currentLogsAdapter.notifyDataSetChanged(); // Notify adapter
        foodNameEditText.getText().clear();
        quantityEditText.getText().clear();
    }

    private void saveFoodLogToFirebase() {
        ArrayList<String> foodLogs = new ArrayList<>(currentLogs); // Save current logs to Firebase

        if (foodLogs.isEmpty()) {
            Toast.makeText(requireContext(), "No food logs to save", Toast.LENGTH_SHORT).show();
            return;
        }

        saveAddFoodButton.setEnabled(false);

        for (String log : foodLogs) {
            String key = databaseReference.child("users").child(userId).child("dietary_logs").push().getKey();
            if (key != null) {
                databaseReference.child("users").child(userId).child("dietary_logs").child(key).setValue(log).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Log saved to Firebase", Toast.LENGTH_SHORT).show();
                        if (foodLogs.indexOf(log) == foodLogs.size() - 1) {
                            loadPastLogsFromFirebase();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to save log to Firebase", Toast.LENGTH_SHORT).show();
                    }
                    saveAddFoodButton.setEnabled(true);
                });
            }
        }

        currentLogs.clear(); // Clear current logs
        currentLogsAdapter.notifyDataSetChanged(); // Notify adapter
    }

    private void loadPastLogsFromFirebase() {
        databaseReference.child("users").child(userId).child("dietary_logs").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pastLogs.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String log = dataSnapshot.getValue(String.class);
                    pastLogs.add(log);
                }
                pastLogsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load logs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Recommendation recommendation) {
        Toast.makeText(requireContext(), "Clicked recommendation: " + recommendation.getFoodName(), Toast.LENGTH_SHORT).show();
        String quantity = "100g";
        String log = recommendation.getFoodName() + ": " + quantity;
        currentLogs.add(log); // Add to current logs
        currentLogsAdapter.notifyDataSetChanged(); // Notify adapter
        foodNameEditText.getText().clear();
        quantityEditText.getText().clear();
    }
}



//package com.example.diabetesdetector.ui.diet;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.diabetesdetector.R;
//import com.example.diabetesdetector.adapters.RecommendationAdapter;
//import com.example.diabetesdetector.apiService.KetoDietService;
//import com.example.diabetesdetector.models.Recommendation;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class DietaryLogFragment extends Fragment implements RecommendationAdapter.OnItemClickListener {
//
//    private EditText foodNameEditText;
//    private EditText quantityEditText;
//    private Button addFoodButton;
//    private Button saveAddFoodButton;
//    private ListView pastLogsListView;
//    private ListView currentLogsListView;
//    private TextView recommendationTextView;
//    private RecyclerView recommendationRecyclerView;
//    private RecommendationAdapter recommendationAdapter;
//
//    private ArrayList<String> pastLogs;
//    private ArrayList<String> currentLogs; // New list for current logs
//    private ArrayAdapter<String> pastLogsAdapter;
//    private ArrayAdapter<String> currentLogsAdapter; // Adapter for the current logs ListView
//
//    private DatabaseReference databaseReference;
//
//    public DietaryLogFragment() {
//        // Required empty public constructor
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_diet_log, container, false);
//
//        // Initialize views
//        foodNameEditText = view.findViewById(R.id.editText_food_name);
//        quantityEditText = view.findViewById(R.id.editText_quantity);
//        addFoodButton = view.findViewById(R.id.button_add_food);
//        saveAddFoodButton = view.findViewById(R.id.save_add_food);
//        pastLogsListView = view.findViewById(R.id.listView_past_logs);
//        currentLogsListView = view.findViewById(R.id.listView_current_logs);
//        recommendationTextView = view.findViewById(R.id.textView_recommendation_title);
//        recommendationRecyclerView = view.findViewById(R.id.recyclerView_recommendations);
//
//        // Initialize past logs list
//        pastLogs = new ArrayList<>();
//        currentLogs = new ArrayList<>(); // Initialize current logs list
//        pastLogsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, pastLogs);
//        currentLogsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, currentLogs); // Initialize adapter for current logs
//        pastLogsListView.setAdapter(pastLogsAdapter);
//        currentLogsListView.setAdapter(currentLogsAdapter); // Set adapter for current logs
//
//        // Initialize recommendation list and adapter
//        List<Recommendation> recommendations = new ArrayList<>();
//        recommendationAdapter = new RecommendationAdapter(recommendations, this);
//        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//        recommendationRecyclerView.setAdapter(recommendationAdapter);
//
//        // Initialize Firebase Database reference
//        databaseReference = FirebaseDatabase.getInstance().getReference("dietary_logs");
//
//        // Load past logs from Firebase
//        loadPastLogsFromFirebase();
//
//        // Add button click listener
//        addFoodButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addFoodToCurrentLog(); // Change to add to current log
//            }
//        });
//
//        // Save button click listener
//        saveAddFoodButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveFoodLogToFirebase();
//            }
//        });
//
//        // Fetch recommendations from the API
//        fetchRecommendationsFromApi();
//
//        return view;
//    }
//
//    private void fetchRecommendationsFromApi() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://keto-diet.p.rapidapi.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        KetoDietService service = retrofit.create(KetoDietService.class);
//        Call<List<Recommendation>> call = service.getRecommendations();
//        call.enqueue(new Callback<List<Recommendation>>() {
//            @Override
//            public void onResponse(Call<List<Recommendation>> call, Response<List<Recommendation>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Recommendation> recommendations = response.body();
//                    recommendationAdapter.setRecommendations(recommendations);
//                    recommendationAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(requireContext(), "Failed to fetch recommendations", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Recommendation>> call, Throwable t) {
//                Toast.makeText(requireContext(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    // Method to add food to the current log
//    private void addFoodToCurrentLog() {
//        String foodName = foodNameEditText.getText().toString().trim();
//        String quantity = quantityEditText.getText().toString().trim();
//
//        if (foodName.isEmpty() || quantity.isEmpty()) {
//            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String log = foodName + ": " + quantity;
//        currentLogs.add(log); // Add to current logs list
//        currentLogsAdapter.notifyDataSetChanged(); // Notify adapter
//        foodNameEditText.getText().clear();
//        quantityEditText.getText().clear();
//    }
//
//    private void saveFoodLogToFirebase() {
//        ArrayList<String> foodLogs = new ArrayList<>(currentLogs); // Save current logs to Firebase
//
//        if (foodLogs.isEmpty()) {
//            Toast.makeText(requireContext(), "No food logs to save", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        saveAddFoodButton.setEnabled(false);
//
//        for (String log : foodLogs) {
//            databaseReference.push().setValue(log).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(requireContext(), "Log saved to Firebase", Toast.LENGTH_SHORT).show();
//                    if (foodLogs.indexOf(log) == foodLogs.size() - 1) {
//                        loadPastLogsFromFirebase();
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "Failed to save log to Firebase", Toast.LENGTH_SHORT).show();
//                }
//                saveAddFoodButton.setEnabled(true);
//            });
//        }
//
//        currentLogs.clear(); // Clear current logs
//        currentLogsAdapter.notifyDataSetChanged(); // Notify adapter
//    }
//
//    private void loadPastLogsFromFirebase() {
//        databaseReference.limitToLast(5).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                pastLogs.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    String log = dataSnapshot.getValue(String.class);
//                    pastLogs.add(log);
//                }
//                pastLogsAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(requireContext(), "Failed to load logs", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onItemClick(Recommendation recommendation) {
//        Toast.makeText(requireContext(), "Clicked recommendation: " + recommendation.getFoodName(), Toast.LENGTH_SHORT).show();
//        String quantity = "100g";
//        String log = recommendation.getFoodName() + ": " + quantity;
//        currentLogs.add(log); // Add to current logs
//        currentLogsAdapter.notifyDataSetChanged(); // Notify adapter
//        foodNameEditText.getText().clear();
//        quantityEditText.getText().clear();
//    }
//}
