package com.example.InfiBidSampleApp;

import android.content.Intent;
import android.net.Uri;
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

    private static final String BASE_VAST_URL =
            "http://sandbox.lemmatechnologies.com/infibid/v1/video/gam";

    private static final String BASE_URL_AID_PID =
            "http://sandbox.lemmatechnologies.com/infibid/v1/video/vast";

    private static final String DEFAULT_VAST_TAG_PARAMS =
            "?test=1&tmax=3000&vw=1920&vh=1080&apdom=%22test.prebid.com%22&apbndl=%22com.prebid.test%22&ip=%22192.168.1.2%22&vmimes=[%22video%2Fmp4%22%2C%20%22video%2Fog3%22]&wpid=178&waid=lemma-hb-adunit&gam_au=";

    private static final String DEFAULT_URL_AID_PID_PARAMS =
            "?test=1&tmax=3000&vw=1080&vh=1920&apid=abcd&apdom=%22test.prebid.com%22&apbndl=%22com.prebid.test%22&ip=%22192.168.1.2%22&vmimes=[%22video%2Fmp4%22%2C%20%22video%2Fog3%22]&wpid=178&waid=GAM_HB_demo";

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
            // Get the value from TextInputEditText every time the button is clicked
            EditText gamAdUnitEditText = findViewById(R.id.gam_adUnit_val_id);
            EditText pubIdEditText = findViewById(R.id.pub_val_id);
            EditText adUnitIdEditText = findViewById(R.id.au_val_id);

            String gamAdUnitId = gamAdUnitEditText.getText().toString().trim();
            String pubId = pubIdEditText.getText().toString().trim();
            String adUnitId = adUnitIdEditText.getText().toString().trim();

            // Construct the URL based on the selected ad server
            String url = "";

            if ("No Ad Server".equals(selectedServer)) {
                Uri.Builder builder = Uri.parse(BASE_URL_AID_PID).buildUpon()
                        .appendQueryParameter("test", "1")
                        .appendQueryParameter("tmax", "3000")
                        .appendQueryParameter("vw", "1080")
                        .appendQueryParameter("vh", "1920")
                        .appendQueryParameter("apid", "abcd")
                        .appendQueryParameter("apdom", "test.prebid.com")
                        .appendQueryParameter("apbndl", "com.prebid.test")
                        .appendQueryParameter("ip", "192.168.1.2")
                        .appendQueryParameter("vmimes", "[\"video/mp4\",\"video/og3\"]")
                        .appendQueryParameter("wpid", pubId)
                        .appendQueryParameter("waid", adUnitId);

                url = builder.build().toString();
                Log.d("Ad with No Ad Server", url);

            } else if ("GAM Ad Server".equals(selectedServer)) {
                Uri.Builder builder = Uri.parse(BASE_VAST_URL).buildUpon()
                        .appendQueryParameter("test", "1")
                        .appendQueryParameter("tmax", "3000")
                        .appendQueryParameter("vw", "1920")
                        .appendQueryParameter("vh", "1080")
                        .appendQueryParameter("apdom", "test.prebid.com")
                        .appendQueryParameter("apbndl", "com.prebid.test")
                        .appendQueryParameter("ip", "192.168.1.2")
                        .appendQueryParameter("vmimes", "[\"video/mp4\",\"video/og3\"]")
                        .appendQueryParameter("wpid", "178")
                        .appendQueryParameter("waid", "lemma-hb-adunit")
                        .appendQueryParameter("gam_au", gamAdUnitId);

                url = builder.build().toString();
                Log.d("GAM Ad with gam id", url);

            }

            // Start the IMAPlayer activity with the constructed URL
            Intent intent = new Intent(MainActivity.this, IMAPlayer.class);
            intent.putExtra(IMAPlayer.EXTRA_VAST_TAG_URL, url);
            startActivity(intent);
        });
    }
}
