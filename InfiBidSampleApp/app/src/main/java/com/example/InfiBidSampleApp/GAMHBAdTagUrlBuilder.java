package com.example.InfiBidSampleApp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class is responsible for building and managing Google Ad Manager (GAM) ad tag URLs
 * for header bidding and making network requests to obtain targeting information.
 */
public class GAMHBAdTagUrlBuilder {

    // Base URL for Google Ad Manager ad requests.
    private static final String BASE_GAM_URL = "https://pubads.g.doubleclick.net/gampad/ads";
    private static final String TAG = "GAMHBAdTagUrlBuilder";

    private final String pubId;
    private final String adUnitId;
    private final String gamAdUnitId;
    private final Context context;

    // OkHttpClient instance for making network requests.
    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Constructs a new instance of {@code GAMHBAdTagUrlBuilder}.
     *
     * @param context    The application context.
     * @param pubId      The publisher ID for GAM.
     * @param adUnitId   The ad unit ID for the specific ad request.
     * @param gamAdUnitId The GAM ad unit ID.
     */
    public GAMHBAdTagUrlBuilder(Context context, String pubId, String adUnitId, String gamAdUnitId) {
        // Store the application context to avoid memory leaks.
        this.context = context.getApplicationContext();
        this.pubId = pubId;
        this.adUnitId = adUnitId;
        this.gamAdUnitId = gamAdUnitId;
    }

    /**
     * Generates the full URL for the GAM ad request with the specified custom parameters.
     *
     * @param custParam Custom parameters to be appended to the URL.
     * @return The full GAM ad tag URL.
     */
    private String generateGAMUrl(String custParam) {
        // Create a DeviceInfo object to retrieve device screen dimensions.
        DeviceInfo deviceInfo = new DeviceInfo(context);
        int screenWidth = deviceInfo.getScreenWidth();
        int screenHeight = deviceInfo.getScreenHeight();

        // Build the full URL by appending query parameters.
        Uri.Builder builder = Uri.parse(BASE_GAM_URL).buildUpon()
                .appendQueryParameter("sz", screenHeight + "x" + screenWidth)  // Set screen size
                .appendQueryParameter("iu", gamAdUnitId)  // Set GAM ad unit ID
                .appendQueryParameter("env", "vp")  // Specify the environment (video player)
                .appendQueryParameter("gdfp_req", "1")  // Specify Google DoubleClick for Publishers request
                .appendQueryParameter("output", "vast")  // Specify VAST output format
                .appendQueryParameter("correlator", String.valueOf(System.currentTimeMillis())); // Use current time as correlator
                if (custParam != null) {
                    builder.appendQueryParameter("cust_params", custParam);  // Append custom parameters
                }
        return builder.build().toString();
    }

    /**
     * Initiates an asynchronous request to fetch header bidding ad targeting data and invokes the provided callback
     * with the resulting custom parameters.
     *
     * @param callback A callback to handle the success or failure of the targeting request.
     */
    public void build(BuildListener callback) {
        // Hardcoded VAST URL for sandbox testing purposes.
        String vastUrl = "https://lemmadigital.com/infibid/v1/video/targeting";
        // Use a LemmaVastAdTagUrlBuilder to construct the targeting URL.
        LemmaVastAdTagUrlBuilder urlBuilder = new LemmaVastAdTagUrlBuilder(this.context, vastUrl, pubId, adUnitId);
        String targetingUrl = urlBuilder.build();

        // Create an HTTP request using the targeting URL.
        Request request = new Request.Builder()
                .url(targetingUrl)
                .build();

        // Perform the network request asynchronously using OkHttp's enqueue method.
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Log the error and invoke the failure callback if the request fails.
                Log.e(TAG, "Error fetching targeting data", e);
                onSuccessResponse(callback, null);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful()) {
                        // If the response is successful, parse the response body.
                        String responseData = response.body().string();
                        Log.d(TAG, "Targeting Response: " + responseData);

                        // Use a parser to extract and generate custom parameters from the response.
                        TargetingResponseParser parser = new TargetingResponseParser();
                        String queryParams;


                            // Parse the response and generate query parameters.
                            queryParams = parser.parseAndGenerateQueryParams(responseData);
                        onSuccessResponse(callback, queryParams); // Invoke success callback with the parameters

                    } else {
                        // Log an error if the request failed with a non-successful status code.
                        Log.e(TAG, "Request failed with code: " + response.code() + " and message: " + response.message());
                        onSuccessResponse(callback, null);
                    }
                } catch (Exception e) {
                    // Handle any errors that occur during parsing.
                    Log.e(TAG, "Error parsing targeting response", e);
                    onSuccessResponse(callback, null);
                }
            }
        });
    }

    private void onSuccessResponse(BuildListener callback, String customParams) {
        if (callback != null) {
            callback.onSuccess(generateGAMUrl(customParams));
        }
    }

    /**
     * Interface definition for a callback to handle the results of GAM vast ad url.
     */
    public interface BuildListener {
        /**
         * Called when the targeting request is successful.
         *
         * @param url The GAM vast ad url
         */
        void onSuccess(String url);
    }
}
