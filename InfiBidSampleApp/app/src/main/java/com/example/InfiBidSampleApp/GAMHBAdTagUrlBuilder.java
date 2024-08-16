package com.example.InfiBidSampleApp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GAMHBAdTagUrlBuilder {

    private static final String BASE_GAM_URL =
            "https://pubads.g.doubleclick.net/gampad/ads";

    private static final String TAG = "GAMHBAdTagUrlBuilder";

    private final String pubId;
    private final String adUnitId;
    private final String gamAdUnitId;
    private final Context context;

    private final OkHttpClient httpClient = new OkHttpClient();

    public GAMHBAdTagUrlBuilder(Context context, String pubId, String adUnitId, String gamAdUnitId) {
        this.context = context.getApplicationContext();
        this.pubId = pubId;
        this.adUnitId = adUnitId;
        this.gamAdUnitId = gamAdUnitId;
    }

    public String getUrl(String custParam) {
        DeviceInfo deviceInfo = new DeviceInfo(context);
        int screenWidth = deviceInfo.getScreenWidth();
        int screenHeight = deviceInfo.getScreenHeight();

        Uri.Builder builder = Uri.parse(BASE_GAM_URL).buildUpon()
                .appendQueryParameter("sz", screenHeight + "x" + screenWidth)
                .appendQueryParameter("iu", gamAdUnitId)
                .appendQueryParameter("env", "vp")
                .appendQueryParameter("gdfp_req", "1")
                .appendQueryParameter("output", "vast")
                .appendQueryParameter("correlator", String.valueOf(System.currentTimeMillis())) // Current time for correlator
                .appendQueryParameter("cust_params", custParam);

        return builder.build().toString();
    }

    public void requestHBAdTargeting(TargetingCallback callback) {
        String vastUrl = "http://sandbox.lemmatechnologies.com/infibid/v1/video/targeting";
        LemmaVastAdTagUrlBuilder urlBuilder = new LemmaVastAdTagUrlBuilder(this.context, vastUrl, pubId, adUnitId);
        String targetingUrl = urlBuilder.getUrl();

        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(targetingUrl)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.d(TAG, "Targeting Response: " + responseData);

                        TargetingResponseParser parser = new TargetingResponseParser();
                        String queryParams;

                        try {
                            queryParams = parser.parseAndGenerateQueryParams(responseData);

                            // Call the callback with the generated query parameters
                            callback.onSuccess(queryParams);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing targeting response", e);
                            callback.onFailure(e);
                        }
                    } else {
                        Log.e(TAG, "Request failed with code: " + response.code() + " and message: " + response.message());
                        callback.onFailure(new IOException("Request failed"));
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching targeting data", e);
                callback.onFailure(e);
            }
        }).start();
    }

    public interface TargetingCallback {
        void onSuccess(String custParam);
        void onFailure(Exception e);
    }
}
