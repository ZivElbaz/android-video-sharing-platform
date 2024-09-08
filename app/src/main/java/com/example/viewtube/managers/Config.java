package com.example.viewtube.managers;

// Configuration class for storing the base URL of the API
public class Config {

    // Base URL for the API
    private static final String BASE_URL = "http://192.168.1.100:12345/";

    // Public method to retrieve the base URL
    public static String getBaseUrl() {
        return BASE_URL; // Return the base URL
    }
}
