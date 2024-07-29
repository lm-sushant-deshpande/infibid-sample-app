package com.example.InfiBidSampleApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);

        // Initialize the Spinner after setting the content view
        Spinner adServerSpinner = findViewById(R.id.server_spinner);

        // Initialize the LinearLayout for GAM Ad Unit ID fields
        LinearLayout gamAdUnitLayout = findViewById(R.id.gam_ad_unit_layout);

        // Set up the ArrayAdapter for the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ad_server_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adServerSpinner.setAdapter(adapter);

        // Set default selection (No Ad Server)
        adServerSpinner.setSelection(0);

        // Set an OnItemSelectedListener for the Spinner
        adServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedServer = (String) parent.getItemAtPosition(position);

                if ("GAM Ad Server".equals(selectedServer)) {
                    gamAdUnitLayout.setVisibility(View.VISIBLE);
                } else {
                    gamAdUnitLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }

        });

        // Initialize the Play Video Button and set its click listener
        Button playVideoButton = findViewById(R.id.Ad_play_button);
        playVideoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, IMAPlayer.class);
            startActivity(intent);
        });
    }
}
