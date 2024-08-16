package com.example.InfiBidSampleApp;


import android.content.Context;
import android.net.Uri;

import java.util.List;

public class LemmaVastAdTagUrlBuilder {

    private final String pubId;
    private final String adUnitId;
    private final Context context;
    private final String url;

    public LemmaVastAdTagUrlBuilder(Context context, String url, String pubId, String adUnitId) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks
        this.pubId = pubId;
        this.adUnitId = adUnitId;
        this.url = url;
    }

    public String getUrl() {
        DeviceInfo deviceInfo = new DeviceInfo(context);
        String ipAddress = DeviceInfo.getIPAddress(true);
        List<String> mimesType = DeviceInfo.getSupportedMimeTypes();
        int screenWidth = deviceInfo.getScreenWidth();
        int screenHeight = deviceInfo.getScreenHeight();

        // Construct the URL based on your requirements
        Uri.Builder builder = Uri.parse(this.url).buildUpon()
                .appendQueryParameter("test", "1")
                .appendQueryParameter("tmax", "3000")
                .appendQueryParameter("vw", String.valueOf(screenWidth))
                .appendQueryParameter("vh", String.valueOf(screenHeight))
                .appendQueryParameter("apid", "abcd")
                .appendQueryParameter("apdom", "test.prebid.com")
                .appendQueryParameter("apbndl", "com.prebid.test")
                .appendQueryParameter("ip", ipAddress)
                .appendQueryParameter("vmimes", mimesType.toString())
                .appendQueryParameter("wpid", pubId)
                .appendQueryParameter("waid", adUnitId);

            return builder.build().toString();
    }

}
