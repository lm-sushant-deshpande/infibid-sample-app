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
        String ipAddress = DeviceInfo.getIPAddress(true);
        List<String> mimesType = DeviceInfo.getSupportedMimeTypes();
        int screenWidth = deviceInfo.getScreenWidth();
        int screenHeight = deviceInfo.getScreenHeight();

        Uri.Builder builder = Uri.parse(BASE_GAM_URL).buildUpon()
//                .appendQueryParameter("test", "1")
//                .appendQueryParameter("tmax", "3000")
//                .appendQueryParameter("vw", String.valueOf(screenHeight))
//                .appendQueryParameter("vh", String.valueOf(screenWidth))
//                .appendQueryParameter("apdom", "test.prebid.com")
//                .appendQueryParameter("apbndl", "com.prebid.test")
//                .appendQueryParameter("ip", ipAddress)
//                .appendQueryParameter("vmimes", mimesType.toString())
//                .appendQueryParameter("wpid", pubId)
//                .appendQueryParameter("waid", adUnitId)
//                .appendQueryParameter("gam_au", gamAdUnitId)
                .appendQueryParameter("sz", screenHeight + "x" + screenWidth)
                .appendQueryParameter("iu", gamAdUnitId)
                .appendQueryParameter("env", "vp")
                .appendQueryParameter("gdfp_req", "1")
                .appendQueryParameter("output", "vast")
                .appendQueryParameter("correlator", String.valueOf(System.currentTimeMillis())) // Current time for correlator
                .appendQueryParameter("cust_params", custParam);

        return builder.build().toString();
    }

    private Map<String, String> convertLmTargetingToMap(TargetingResponseModel.LMTargeting targeting) {
        Map<String, String> map = new HashMap<>();
        // Manually add properties to the map
        if (targeting.getLmHbBidder() != null) map.put("lm_hb_bidder", targeting.getLmHbBidder());
        if (targeting.getLmHbBidderPubmatic() != null) map.put("lm_hb_bidder_pubmatic", targeting.getLmHbBidderPubmatic());
        if (targeting.getLmHbCacheHost() != null) map.put("lm_hb_cache_host", targeting.getLmHbCacheHost());
        if (targeting.getLmHbCacheHostPubmat() != null) map.put("lm_hb_cache_host_pubmat", targeting.getLmHbCacheHostPubmat());
        if (targeting.getLmHbCacheId() != null) map.put("lm_hb_cache_id", targeting.getLmHbCacheId());
        if (targeting.getLmHbCachePath() != null) map.put("lm_hb_cache_path", targeting.getLmHbCachePath());
        if (targeting.getLmHbCachePathPubmat() != null) map.put("lm_hb_cache_path_pubmat", targeting.getLmHbCachePathPubmat());
        if (targeting.getLmHbEnv() != null) map.put("lm_hb_env", targeting.getLmHbEnv());
        if (targeting.getLmHbEnvPubmatic() != null) map.put("lm_hb_env_pubmatic", targeting.getLmHbEnvPubmatic());
        if (targeting.getLmHbFormat() != null) map.put("lm_hb_format", targeting.getLmHbFormat());
        if (targeting.getLmHbFormatPubmatic() != null) map.put("lm_hb_format_pubmatic", targeting.getLmHbFormatPubmatic());
        if (targeting.getLmHbPb() != null) map.put("lm_hb_pb", targeting.getLmHbPb());
        if (targeting.getLmHbPbPubmatic() != null) map.put("lm_hb_pb_pubmatic", targeting.getLmHbPbPubmatic());
        if (targeting.getLmHbSize() != null) map.put("lm_hb_size", targeting.getLmHbSize());
        if (targeting.getLmHbSizePubmatic() != null) map.put("lm_hb_size_pubmatic", targeting.getLmHbSizePubmatic());

        return map;
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
                            Log.d(TAG, "Generated Query Params: " + queryParams);

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
