package com.example.InfiBidSampleApp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TargetingResponseParser {
    private static final String TAG = "TargetingModelParser";

    private final Gson gson = new Gson();

    // Method to parse JSON response and generate query parameters
    public String parseAndGenerateQueryParams(String jsonResponse) throws Exception {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonObject lmTargetingJson = jsonObject.getAsJsonObject("lm_targeting");

            // Convert JSON object directly to map and generate query parameters
            Map<String, String> targetingMap = convertJsonObjectToMap(lmTargetingJson);
            Log.d(TAG, "Generated Map: " + targetingMap);

            String queryParams = convertMapToQueryParams(targetingMap);

            Log.d(TAG, "Generated Query Params: " + queryParams);
            return queryParams;

        } catch (JsonSyntaxException e) {
            throw new Exception("Failed to parse JSON response: " + e.getMessage(), e);
        }
    }

    // Method to convert JSON object to Map
    private Map<String, String> convertJsonObjectToMap(JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }

    // Method to convert Map to URL query params
    public String convertMapToQueryParams(Map<String, String> map) {
        StringBuilder queryParams = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                String encodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
                String encodedValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                if (queryParams.length() > 0) {
                    queryParams.append("&");
                }
                queryParams.append(encodedKey).append("=").append(encodedValue);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Encoding error: " + e.getMessage());
            }
        }
        return queryParams.toString();
    }

}
