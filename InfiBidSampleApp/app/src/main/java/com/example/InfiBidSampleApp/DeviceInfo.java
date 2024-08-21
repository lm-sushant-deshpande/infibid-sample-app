package com.example.InfiBidSampleApp;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code DeviceInfo} class provides various utility methods to retrieve device-specific
 * information such as screen dimensions, IP address, app bundle name, and supported video MIME types.
 */
public class DeviceInfo {

    private static final String TAG = "DeviceInfo";

    // Context object to access device resources and services
    private Context context;

    /**
     * Constructs a new {@code DeviceInfo} object using the provided context.
     *
     * @param context The context to access device resources and services.
     */
    public DeviceInfo(Context context) {
        this.context = context;
    }

    /**
     * Returns the height of the device screen, including system decorations (status bar, navigation bar).
     *
     * @return The height of the screen in pixels.
     */
    public int getScreenHeight() {
        // Retrieves the screen dimensions and returns the height
        int height = getScreenSizeIncludingTopBottomBar()[1];
        return height;
    }

    /**
     * Returns the width of the device screen, including system decorations (status bar, navigation bar).
     *
     * @return The width of the screen in pixels.
     */
    public int getScreenWidth() {
        // Retrieves the screen dimensions and returns the width
        int width = getScreenSizeIncludingTopBottomBar()[0];
        return width;
    }

    /**
     * Returns the screen dimensions of the device, including system decorations (status bar, navigation bar).
     *
     * @return An array where the first element is the width and the second element is the height of the screen.
     */
    private int[] getScreenSizeIncludingTopBottomBar() {
        int[] screenDimensions = new int[2]; // width[0], height[1]
        int x, y;

        // Retrieves the current orientation of the device (portrait or landscape)
        int orientation = context.getResources().getConfiguration().orientation;

        // Gets the WindowManager system service to interact with the display
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        // Check if the device SDK version supports advanced methods for retrieving screen size
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point screenSize = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // Retrieves the real size of the display, including system decorations
                display.getRealSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            } else {
                // Retrieves the size of the display excluding system decorations
                display.getSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            }
        } else {
            // For older devices, retrieve the display width and height directly
            x = display.getWidth();
            y = display.getHeight();
        }

        // Adjust dimensions based on orientation (width and height swap in landscape mode)
        screenDimensions[0] = orientation == Configuration.ORIENTATION_PORTRAIT ? x : y; // width
        screenDimensions[1] = orientation == Configuration.ORIENTATION_PORTRAIT ? y : x; // height

        return screenDimensions;
    }

    /**
     * Returns the IP address of the device.
     *
     * @param useIPv4 {@code true} to return an IPv4 address, {@code false} to return an IPv6 address.
     * @return The IP address as a string. If an error occurs, an empty string is returned.
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            // Retrieves all network interfaces on the device
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            // Iterates through each network interface
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());

                // Iterates through each IP address associated with the current network interface
                for (InetAddress address : addresses) {
                    if (!address.isLoopbackAddress()) { // Skip loopback addresses
                        String ipAddress = address.getHostAddress();
                        boolean isIPv4 = ipAddress.indexOf(':') < 0; // Check if it's an IPv4 address

                        if (useIPv4 && isIPv4) {
                            return ipAddress; // Return IPv4 address
                        } else if (!useIPv4 && !isIPv4) {
                            // For IPv6, remove any zone suffix (e.g., "%eth0")
                            int delimiterIndex = ipAddress.indexOf('%');
                            return delimiterIndex < 0 ? ipAddress.toUpperCase() : ipAddress.substring(0, delimiterIndex).toUpperCase();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Handle exception if necessary
        }
        return ""; // Return an empty string if no IP address was found
    }

    /**
     * Returns the bundle name (package name) of the application.
     *
     * @return The app's bundle name. If an error occurs, "unknown" is returned.
     */
    String getAppBundle() {
        try {
            // Get the PackageManager to retrieve package information
            PackageManager packageManager = context.getPackageManager();
            // Get the PackageInfo which contains the package name (bundle name)
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName; // This is the app's bundle name
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "unknown"; // Return "unknown" if an error occurs
        }
    }

    /**
     * Returns a list of supported video MIME types on the device.
     *
     * @return A list of supported video MIME types.
     */
    public static List<String> getSupportedMimeTypes() {
        List<String> videoMimeTypes = new ArrayList<>();

        // Get the list of all available codecs on the device
        MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        MediaCodecInfo[] codecInfos = codecList.getCodecInfos();

        // Iterate through each codec
        for (MediaCodecInfo codecInfo : codecInfos) {
            if (!codecInfo.isEncoder()) { // We are only interested in decoders (not encoders)
                String[] supportedTypes = codecInfo.getSupportedTypes();

                // Iterate through each supported MIME type
                for (String type : supportedTypes) {
                    if (type.startsWith("video/")) { // Check if the MIME type is a video type
                        if (!videoMimeTypes.contains(type)) { // Avoid duplicates
                            videoMimeTypes.add(type); // Add the video MIME type to the list
                            Log.d("SupportedVideoMimeTypes", "Codec: " + codecInfo.getName() + ", MIME type: " + type);
                        }
                    }
                }
            }
        }
        return videoMimeTypes;
    }
}
