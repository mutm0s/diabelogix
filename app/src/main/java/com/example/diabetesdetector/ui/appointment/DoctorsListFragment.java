package com.example.diabetesdetector.ui.appointment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorsListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DoctorsAdapter adapter;
    private List<Doctor> doctorList;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private ImageView mBackButton;

    private TextView toolbar_title;

    public DoctorsListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doctorList = new ArrayList<>();
        adapter = new DoctorsAdapter(doctorList, new DoctorsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Doctor doctor) {
                openAppointmentFragment(doctor);
            }
        });

        // Initialize Firebase components
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        fetchDoctors();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doctors_list, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        mBackButton = root.findViewById(R.id.btn_back);
        toolbar_title = root.findViewById(R.id.toolbar_title);

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return root;
    }

    private void fetchDoctors() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("users").child(userId).child("doctors")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            doctorList.clear(); // Clear existing data before adding new data

                            // Check if there are any doctors in the database
                            if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                int count = 0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    if (count < 2) {
                                        String name = dataSnapshot.child("name").getValue(String.class);
                                        String specialty = dataSnapshot.child("specialty").getValue(String.class);
                                        double rating = dataSnapshot.child("rating").getValue(Double.class);
                                        String distance = dataSnapshot.child("distance").getValue(String.class);
                                        String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                                        Doctor doctor = new Doctor(name, specialty, rating, distance, imageUrl);
                                        doctorList.add(doctor);
                                    }
                                    count++;
                                }
                            } else {
                                // If no doctors found in database, add dummy data
                                doctorList.add(new Doctor("Dr. Joe Mathews", "Cardiologist", 4.5, "1.2 km", "https://www.pexels.com/photo/mosaic-alien-on-wall-1670977/"));
                                doctorList.add(new Doctor("Dr. Jane Smith", "Pediatrician", 4.8, "0.8 km", "https://www.pexels.com/photo/mosaic-alien-on-wall-1670977/"));
                            }

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                            // For example, log error message
                            Log.e("DoctorsListFragment", "Failed to read value.", error.toException());
                        }
                    });
        }
    }

    private void openAppointmentFragment(Doctor doctor) {
        AppointmentsFragment appointmentFragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putParcelable("doctor", doctor);
        appointmentFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, appointmentFragment)
                .addToBackStack(null)
                .commit();
    }

    // ViewHolder for RecyclerView Adapter
    private static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName, doctorSpecialty, doctorRatingValue, doctorDistance;

        DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
            doctorRatingValue = itemView.findViewById(R.id.doctor_rating_value);
            doctorDistance = itemView.findViewById(R.id.doctor_distance);
        }
    }

    // Adapter for RecyclerView
    private static class DoctorsAdapter extends RecyclerView.Adapter<DoctorViewHolder> {

        private List<Doctor> doctorList;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(Doctor doctor);
        }

        DoctorsAdapter(List<Doctor> doctorList, OnItemClickListener listener) {
            this.doctorList = doctorList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_doctor, parent, false);
            return new DoctorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            Doctor doctor = doctorList.get(position);
            holder.doctorName.setText(doctor.getName());
            holder.doctorSpecialty.setText(doctor.getSpecialty());
            holder.doctorRatingValue.setText(String.valueOf(doctor.getRating()));
            holder.doctorDistance.setText(doctor.getDistance());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(doctor);
                }
            });
        }

        @Override
        public int getItemCount() {
            return doctorList.size();
        }
    }
}
