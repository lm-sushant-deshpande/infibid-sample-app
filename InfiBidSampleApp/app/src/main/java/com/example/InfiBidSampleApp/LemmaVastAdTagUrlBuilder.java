package com.example.InfiBidSampleApp;

import android.content.Context;
import android.net.Uri;

import java.util.List;

/**
 * This class is responsible for building a VAST ad tag URL using Lemma's specific parameters.
 * It gathers device information such as screen size, IP address, and supported MIME types to
 * construct the URL.
 */
public class LemmaVastAdTagUrlBuilder {

    private final String pubId;
    private final String adUnitId;
    private final Context context;
    private final String url;

    /**
     * Constructs a new instance of {@code LemmaVastAdTagUrlBuilder}.
     *
     * @param context   The application context.
     * @param url       The base URL to which parameters will be appended.
     * @param pubId     The publisher ID for Lemma.
     * @param adUnitId  The ad unit ID for the specific ad request.
     */
    public LemmaVastAdTagUrlBuilder(Context context, String url, String pubId, String adUnitId) {
        this.context = context.getApplicationContext(); // Use application context to avoid memory leaks.
        this.pubId = pubId;
        this.adUnitId = adUnitId;
        this.url = url;
    }

    /**
     * Generates the full URL with the necessary parameters for a VAST ad request.
     * The URL includes parameters like screen dimensions, IP address, supported MIME types, etc.
     *
     * @return The complete VAST ad tag URL.
     */
    public String build() {
        // Create a DeviceInfo object to retrieve device-specific information.
        DeviceInfo deviceInfo = new DeviceInfo(context);
        // Retrieve the device's IP address.
        String ipAddress = DeviceInfo.getIPAddress(true);
        // Get the list of supported MIME types on the device.
        List<String> mimesType = DeviceInfo.getSupportedMimeTypes();
        // Retrieve the screen width and height.
        int screenWidth = deviceInfo.getScreenWidth();
        int screenHeight = deviceInfo.getScreenHeight();
        // Get the app's bundle identifier.
        String appBundle = deviceInfo.getAppBundle();

        // Construct the URL by appending necessary query parameters.
        Uri.Builder builder = Uri.parse(this.url).buildUpon()
                .appendQueryParameter("test", "1")  // Test parameter for debugging or sandbox environment.
                .appendQueryParameter("tmax", "3000")  // Maximum timeout for the request in milliseconds.
                .appendQueryParameter("vw", String.valueOf(screenWidth))  // Screen width in pixels.
                .appendQueryParameter("vh", String.valueOf(screenHeight))  // Screen height in pixels.
                .appendQueryParameter("apid", "abcd")  // Application ID (placeholder value).
                .appendQueryParameter("apdom", appBundle)
                .appendQueryParameter("apbndl", appBundle)  // Another parameter for the app bundle identifier.
                .appendQueryParameter("ip", ipAddress)  // Device IP address.
                .appendQueryParameter("vmimes", mimesType.toString())  // Supported MIME types as a comma-separated string.
                .appendQueryParameter("wpid", pubId)  // Publisher ID for Lemma.
                .appendQueryParameter("waid", adUnitId);  // Ad unit ID for the specific ad request.

        return builder.build().toString();  // Return the constructed URL as a string.
    }
}
