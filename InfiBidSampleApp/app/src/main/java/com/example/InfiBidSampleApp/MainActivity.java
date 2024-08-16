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

/**
 * The {@code MainActivity} class is the main entry point of the app, allowing users to select an ad server,
 * input required ad unit details, and play a video ad using the selected server's URL.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Tag for logging purposes.

    private String selectedServer = "No Ad Server"; // Default value for the selected ad server.

    /**
     * Called when the activity is first created.
     * Initializes the UI components and sets up listeners for the ad server selection spinner and the play video button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in {@link #onSaveInstanceState(Bundle)}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find and initialize the spinner for selecting ad servers.
        Spinner adServerSpinner = findViewById(R.id.server_spinner);
        // Find the layout that will be shown/hidden based on the selected ad server.
        LinearLayout gamAdUnitLayout = findViewById(R.id.gam_ad_unit_layout);

        // Set up the spinner with ad server options from a string array resource.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ad_server_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adServerSpinner.setAdapter(adapter);

        // Listen for user selection on the ad server spinner.
        adServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update the selected server based on user choice.
                selectedServer = (String) parent.getItemAtPosition(position);
                // Show or hide the GAM ad unit layout based on the selected server.
                if ("GAM Ad Server".equals(selectedServer)) {
                    gamAdUnitLayout.setVisibility(View.VISIBLE);
                } else {
                    gamAdUnitLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action required when nothing is selected.
            }
        });

        // Find and initialize the play video button.
        Button playVideoButton = findViewById(R.id.Ad_play_button);
        // Set up an onClick listener for the button to handle ad playback.
        playVideoButton.setOnClickListener(v -> {
            // Retrieve input values from EditText fields.
            EditText gamAdUnitEditText = findViewById(R.id.gam_adUnit_val_id);
            EditText pubIdEditText = findViewById(R.id.pub_val_id);
            EditText adUnitIdEditText = findViewById(R.id.au_val_id);

            String gamAdUnitId = gamAdUnitEditText.getText().toString().trim();
            String pubId = pubIdEditText.getText().toString().trim();
            String adUnitId = adUnitIdEditText.getText().toString().trim();

            // Handle ad request based on the selected ad server.
            if ("No Ad Server".equals(selectedServer)) {
                // Build the VAST ad tag URL using LemmaVastAdTagUrlBuilder.
                String vastUrl = "http://sandbox.lemmatechnologies.com/infibid/v1/video/vast";
                LemmaVastAdTagUrlBuilder urlBuilder = new LemmaVastAdTagUrlBuilder(this, vastUrl, pubId, adUnitId);
                String url = urlBuilder.getUrl();
                Log.d(TAG, "Ad with No Ad Server URL: " + url);

                // Start the IMAPlayer activity to play the video ad.
                startIMAPlayerActivity(url);

            } else if ("GAM Ad Server".equals(selectedServer)) {
                // Build the GAM ad tag URL using GAMHBAdTagUrlBuilder and request targeting data.
                GAMHBAdTagUrlBuilder urlBuilder = new GAMHBAdTagUrlBuilder(this, pubId, adUnitId, gamAdUnitId);

                urlBuilder.requestHBAdTargeting(new GAMHBAdTagUrlBuilder.TargetingCallback() {
                    @Override
                    public void onSuccess(String custParam) {
                        // Build the URL with custom parameters and start the IMAPlayer activity.
                        String url = urlBuilder.getUrl(custParam);
                        Log.d(TAG, "Ad with GAM Ad Server URL: " + url);
                        startIMAPlayerActivity(url);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Log the error and handle it, e.g., show a message to the user.
                        Log.e(TAG, "Failed to fetch targeting data", e);
                    }
                });

            } else {
                // Handle case where no ad server is selected or an unknown server is chosen.
                String url = "";
                startIMAPlayerActivity(url);
            }
        });
    }

    /**
     * Starts the {@link IMAPlayer} activity to play the video ad using the provided VAST tag URL.
     *
     * @param url The VAST tag URL to be used by the IMAPlayer.
     */
    private void startIMAPlayerActivity(String url) {
        // Create an intent to start the IMAPlayer activity and pass the VAST tag URL.
        Intent intent = new Intent(MainActivity.this, IMAPlayer.class);
        intent.putExtra(IMAPlayer.EXTRA_VAST_TAG_URL, url);
        startActivity(intent); // Start the IMAPlayer activity.
    }
}
