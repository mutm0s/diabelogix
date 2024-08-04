package com.example.diabetesdetector.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.Doctor;
import com.example.diabetesdetector.ui.appointment.DoctorsListFragment;

import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder> {

    private List<Doctor> doctorList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Doctor doctor);
    }

    public DoctorsAdapter(List<Doctor> doctorList, OnItemClickListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.bind(doctor, listener);
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {

        // Declare your views here
        TextView doctorName, doctorSpecialty, doctorRatingValue, doctorDistance;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorSpecialty = itemView.findViewById(R.id.doctor_specialty);
            doctorRatingValue = itemView.findViewById(R.id.doctor_rating_value);
            doctorDistance = itemView.findViewById(R.id.doctor_distance);
        }

        public void bind(final Doctor doctor, final OnItemClickListener listener) {
            // Bind data to your views
            doctorName.setText(doctor.getName());
            doctorSpecialty.setText(doctor.getSpecialty());
            doctorRatingValue.setText(String.valueOf(doctor.getRating()));
            doctorDistance.setText(doctor.getDistance());

            // Set click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(doctor);
                }
            });
        }
    }
}
