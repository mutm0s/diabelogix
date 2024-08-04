package com.example.diabetesdetector.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetesdetector.R;
import com.example.diabetesdetector.models.CarbsInsulin;
import com.example.diabetesdetector.models.GlucoseReading;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CARBS_INSULIN = 0;
    private static final int TYPE_GLUCOSE_READING = 1;
    private List<Object> dashboardData;

    public DashboardAdapter(List<Object> dashboardData) {
        this.dashboardData = dashboardData;
    }

    @Override
    public int getItemViewType(int position) {
        if (dashboardData.get(position) instanceof CarbsInsulin) {
            return TYPE_CARBS_INSULIN;
        } else {
            return TYPE_GLUCOSE_READING;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CARBS_INSULIN) {
            View view = inflater.inflate(R.layout.item_carbs_insulin, parent, false);
            return new CarbsInsulinViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_glucose_reading, parent, false);
            return new GlucoseReadingViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_CARBS_INSULIN) {
            CarbsInsulinViewHolder viewHolder = (CarbsInsulinViewHolder) holder;
            CarbsInsulin carbsInsulin = (CarbsInsulin) dashboardData.get(position);
            viewHolder.bind(carbsInsulin);
        } else {
            GlucoseReadingViewHolder viewHolder = (GlucoseReadingViewHolder) holder;
            GlucoseReading glucoseReading = (GlucoseReading) dashboardData.get(position);
            viewHolder.bind(glucoseReading);
        }
    }

    @Override
    public int getItemCount() {
        return dashboardData.size();
    }

    static class CarbsInsulinViewHolder extends RecyclerView.ViewHolder {
        TextView tvCarbs, tvInsulin, tvTimestamp;

        CarbsInsulinViewHolder(View itemView) {
            super(itemView);
            tvCarbs = itemView.findViewById(R.id.tv_carbs);
            tvInsulin = itemView.findViewById(R.id.tv_insulin);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        void bind(CarbsInsulin carbsInsulin) {
            tvCarbs.setText(String.format("Carbs: %.2f", carbsInsulin.getCarbs()));
            tvInsulin.setText(String.format("Insulin: %.2f", carbsInsulin.getInsulin()));
            //tvTimestamp.setText(carbsInsulin.getTimestamp());
        }
    }

    static class GlucoseReadingViewHolder extends RecyclerView.ViewHolder {
        TextView tvGlucoseReading, tvTimestamp;

        GlucoseReadingViewHolder(View itemView) {
            super(itemView);
            tvGlucoseReading = itemView.findViewById(R.id.tv_glucose_reading);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }

        void bind(GlucoseReading glucoseReading) {
            tvGlucoseReading.setText(String.format("Glucose: %d", glucoseReading.getGlucoseReading()));
            tvTimestamp.setText(glucoseReading.getTimestamp());
        }
    }
}

