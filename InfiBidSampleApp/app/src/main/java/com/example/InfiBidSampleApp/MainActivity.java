package com.example.InfiBidSampleApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String selectedServer = "No Ad Server"; // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner adServerSpinner = findViewById(R.id.server_spinner);
        LinearLayout gamAdUnitLayout = findViewById(R.id.gam_ad_unit_layout);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ad_server_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adServerSpinner.setAdapter(adapter);

        adServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedServer = (String) parent.getItemAtPosition(position);
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

        Button playVideoButton = findViewById(R.id.Ad_play_button);
        playVideoButton.setOnClickListener(v -> {

            EditText gamAdUnitEditText = findViewById(R.id.gam_adUnit_val_id);
            EditText pubIdEditText = findViewById(R.id.pub_val_id);
            EditText adUnitIdEditText = findViewById(R.id.au_val_id);

            String gamAdUnitId = gamAdUnitEditText.getText().toString().trim();
            String pubId = pubIdEditText.getText().toString().trim();
            String adUnitId = adUnitIdEditText.getText().toString().trim();

            if ("No Ad Server".equals(selectedServer)) {
                String vastUrl = "http://sandbox.lemmatechnologies.com/infibid/v1/video/vast";
                LemmaVastAdTagUrlBuilder urlBuilder = new LemmaVastAdTagUrlBuilder(this, vastUrl, pubId, adUnitId);
                String url = urlBuilder.getUrl();
                Log.d(TAG, "Ad with No Ad Server URL: " + url);

                startIMAPlayerActivity(url);

            } else if ("GAM Ad Server".equals(selectedServer)) {
                GAMHBAdTagUrlBuilder urlBuilder = new GAMHBAdTagUrlBuilder(this, pubId, adUnitId, gamAdUnitId);

                urlBuilder.requestHBAdTargeting(new GAMHBAdTagUrlBuilder.TargetingCallback() {
                    @Override
                    public void onSuccess(String custParam) {
                        String url = urlBuilder.getUrl(custParam);
                        Log.d(TAG, "Ad with GAM Ad Server URL: " + url);
                        startIMAPlayerActivity(url);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to fetch targeting data", e);
                        // Handle error, e.g., show a message to the user
                    }
                });

            } else {
                String url = "";
                startIMAPlayerActivity(url);
            }
        });
    }

    private void startIMAPlayerActivity(String url) {
        Intent intent = new Intent(MainActivity.this, IMAPlayer.class);
        intent.putExtra(IMAPlayer.EXTRA_VAST_TAG_URL, url);
        startActivity(intent);
    }
}
