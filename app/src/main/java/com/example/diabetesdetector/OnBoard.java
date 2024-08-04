package com.example.diabetesdetector;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class OnBoard extends AppCompatActivity {

    private LinearLayout nextSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard);

        nextSection = findViewById(R.id.next_section);

        nextSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent optionsIntent = new Intent(OnBoard.this, OptionsBoard.class);
                startActivity(optionsIntent);
            }
        });
    }
}

