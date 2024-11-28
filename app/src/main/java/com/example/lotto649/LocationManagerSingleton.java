package com.example.lotto649;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

public class LocationManagerSingleton {

    private static LocationManagerSingleton instance;
    private Context context;
    private boolean locationTrackingEnabled = false;
    private GeoPoint geoPoint;

    // Private constructor to prevent instantiation
    private LocationManagerSingleton() {}

    // Get the singleton instance
    public static synchronized LocationManagerSingleton getInstance() {
        if (instance == null) {
            instance = new LocationManagerSingleton();
        }
        return instance;
    }

    // Initialize with context (call this from your Activity)
    public void init(Context context) {
        if (this.context == null) {
            this.context = context.getApplicationContext(); // Ensure the application context is used
        }
    }

    // Set location tracking flag
    public void setLocationTrackingEnabled(boolean enabled) {
        locationTrackingEnabled = enabled;
    }

    // Get location tracking flag
    public boolean isLocationTrackingEnabled() {
        return locationTrackingEnabled;
    }

    // Safely use context in singleton methods
    public Context getContext() {
        return context;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
