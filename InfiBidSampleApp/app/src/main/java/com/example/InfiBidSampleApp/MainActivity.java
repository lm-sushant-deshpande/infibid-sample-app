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

    private static final String DEFAULT_VAST_TAG_URL =
            "https://pubads.g.doubleclick.net/gampad/ads?iu=/22192417927/HB-Video-Test&gdfp_req=1&unviewed_position_start=1&output=vast&env=vp&impl=s&sz=1920x1080%7C1080x1920%7C1080x1920%7C1920x1080&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]&cust_params=pb_sz%3D1920x1080";

    private static final String DEFAULT_URL_Aid_Pid =
            "http://localhost:8000/openrtb2/video?test=1&tmax=3000&vw=1080&vh=1920&apid=abcd&apdom=%22test.prebid.com%22&apbndl=%22com.prebid.test%22&ip=%22192.168.1.2%22&vmimes=[%22video%2Fmp4%22%2C%20%22video%2Fog3%22]&wpid=178&waid=lemma-hb-adunit";

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
                // Use the default URL_Aid_Pid if "No Ad Server" is selected
                url = DEFAULT_URL_Aid_Pid.replace("178",pubId).replace("lemma-hb-adunit",adUnitId);
                Log.d("Ad with No Ad Server", url);
            } else if("GAM Ad Server".equals(selectedServer)) {
                // Construct VAST URL based on the presence of GAM Ad Unit ID
                    // Replace placeholder with actual GAM Ad Unit ID
                    url = DEFAULT_VAST_TAG_URL.replace("/22192417927", "/" + gamAdUnitId);
                    Log.d("GAM Ad with gam id",url);
            }

            // Start the IMAPlayer activity with the constructed URL
            Intent intent = new Intent(MainActivity.this, IMAPlayer.class);
            intent.putExtra(IMAPlayer.EXTRA_VAST_TAG_URL, url);
            startActivity(intent);
        });
    }
}
