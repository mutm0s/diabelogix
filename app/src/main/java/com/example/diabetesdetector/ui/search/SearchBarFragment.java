package com.example.diabetesdetector.ui.search;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.diabetesdetector.R;

public class SearchBarFragment extends Fragment {

    private EditText searchBarEditText;
    private Button searchButton;

    public SearchBarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_bar, container, false);

        // Initialize UI components
        searchBarEditText = view.findViewById(R.id.search_bar_edit_text);
        searchButton = view.findViewById(R.id.search_button);

        // Set click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBarEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    // Perform search operation or invoke ML model
                    // For demonstration purpose, displaying a toast with the search query
                    Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please enter a search query", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}

