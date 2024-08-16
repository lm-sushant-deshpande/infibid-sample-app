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

/**
 * The {@code TargetingResponseParser} class is responsible for parsing a JSON response
 * and generating URL query parameters from the parsed data.
 * It uses the Gson library to parse the JSON and handles the conversion of JSON objects to maps and maps to query parameters.
 */
public class TargetingResponseParser {
    private static final String TAG = "TargetingModelParser"; // Tag for logging purposes.

    private final Gson gson = new Gson(); // Gson instance for parsing JSON.

    /**
     * Parses the given JSON response string and generates URL query parameters from the
     * {@code lm_targeting} JSON object within the response.
     *
     * @param jsonResponse The JSON response string to be parsed.
     * @return A string containing the generated URL query parameters.
     * @throws Exception If there is an error in parsing the JSON response.
     */
    public String parseAndGenerateQueryParams(String jsonResponse) throws Exception {
        try {
            // Parse the JSON response string into a JsonObject.
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            // Get the 'lm_targeting' JSON object from the parsed response.
            JsonObject lmTargetingJson = jsonObject.getAsJsonObject("lm_targeting");

            // Convert JSON object to a map of string key-value pairs.
            Map<String, String> targetingMap = convertJsonObjectToMap(lmTargetingJson);
            Log.d(TAG, "Generated Map: " + targetingMap);

            // Convert the map to URL query parameters.
            String queryParams = convertMapToQueryParams(targetingMap);

            Log.d(TAG, "Generated Query Params: " + queryParams);
            return queryParams;

        } catch (JsonSyntaxException e) {
            // Handle JSON parsing errors.
            throw new Exception("Failed to parse JSON response: " + e.getMessage(), e);
        }
    }



    /**
     * Converts the given {@link JsonObject} to a {@link Map} with string keys and values.
     * This method iterates over each entry in the JSON object and adds it to the map.
     *
     * @param jsonObject The JSON object to be converted to a map.
     * @return A map containing the JSON object's key-value pairs as strings.
     */
    private Map<String, String> convertJsonObjectToMap(JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        // Iterate over each entry in the JSON object and add to the map.
        for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }



    /**
     * Converts the given {@link Map} of strings to a URL-encoded query parameter string.
     * Each entry in the map is encoded and added to the query parameter string.
     *
     * @param map The map to be converted to a query parameter string.
     * @return A string containing the URL-encoded query parameters.
     */
    public String convertMapToQueryParams(Map<String, String> map) {
        StringBuilder queryParams = new StringBuilder();
        // Iterate over each entry in the map to build the query parameter string.
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                // Encode both key and value to handle special characters.
                String encodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
                String encodedValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                // Append the encoded key-value pair to the query string.
                if (queryParams.length() > 0) {
                    queryParams.append("&");
                }
                queryParams.append(encodedKey).append("=").append(encodedValue);
            } catch (UnsupportedEncodingException e) {
                // Log encoding errors.
                Log.e(TAG, "Encoding error: " + e.getMessage());
            }
        }
        return queryParams.toString();
    }
}
