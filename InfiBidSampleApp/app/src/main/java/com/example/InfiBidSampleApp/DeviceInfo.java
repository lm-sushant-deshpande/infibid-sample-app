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

public class DeviceInfo {

    private static final String TAG = "DeviceInfo";

    private Context context;

    public DeviceInfo(Context context) {
        this.context = context;
    }

    public int getScreenHeight() {
        int height = getScreenSizeInlcudingTopBottomBar()[1];
        return height;
    }

    public int getScreenWidth() {
        int width = getScreenSizeInlcudingTopBottomBar()[0];
        return width;
    }

    // Method to get screen dimensions including system decorations
    private int[] getScreenSizeInlcudingTopBottomBar() {
        int[] screenDimensions = new int[2]; // width[0], height[1]
        int x, y, orientation = context.getResources().getConfiguration().orientation;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point screenSize = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            } else {
                display.getSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            }
        } else {
            x = display.getWidth();
            y = display.getHeight();
        }

        screenDimensions[0] = orientation == Configuration.ORIENTATION_PORTRAIT ? x : y; // width
        screenDimensions[1] = orientation == Configuration.ORIENTATION_PORTRAIT ? y : x; // height

        return screenDimensions;
    }

    // Method to get the IP address of the device
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> addresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : addresses) {
                    if (!address.isLoopbackAddress()) {
                        String ipAddress = address.getHostAddress();
                        boolean isIPv4 = ipAddress.indexOf(':') < 0;

                        if (useIPv4 && isIPv4) {
                            return ipAddress;
                        } else if (!useIPv4 && !isIPv4) {
                            int delimiterIndex = ipAddress.indexOf('%'); // drop IPv6 zone suffix
                            return delimiterIndex < 0 ? ipAddress.toUpperCase() : ipAddress.substring(0, delimiterIndex).toUpperCase();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Handle exception if necessary
        }
        return "";
    }

    String getAppBundle() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName; // This is the app's bundle name
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    public static List<String> getSupportedMimeTypes() {
        List<String> videoMimeTypes = new ArrayList<>();
        MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        MediaCodecInfo[] codecInfos = codecList.getCodecInfos();

        for (MediaCodecInfo codecInfo : codecInfos) {
            if (!codecInfo.isEncoder()) {
                String[] supportedTypes = codecInfo.getSupportedTypes();
                for (String type : supportedTypes) {
                    if (type.startsWith("video/")) {
                        if (!videoMimeTypes.contains(type)) {
                            videoMimeTypes.add(type);
                            Log.d("SupportedVideoMimeTypes", "Codec: " + codecInfo.getName() + ", MIME type: " + type);
                        }
                    }
                }
            }
        }
        return videoMimeTypes;
    }
}
