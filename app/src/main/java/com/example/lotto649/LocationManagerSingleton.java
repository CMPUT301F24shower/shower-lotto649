package com.example.lotto649;

import android.content.Context;

import com.google.firebase.firestore.GeoPoint;

/**
 * Creates a singleton location manager that stores the current user's location permissions
 */
public class LocationManagerSingleton {

    private static LocationManagerSingleton instance;
    private Context context;
    private boolean locationTrackingEnabled = false;
    private GeoPoint geoPoint;

    /**
     * Private constructor to prevent instantiation
     */
    private LocationManagerSingleton() {
    }

    /**
     * Gets the singleton instance of the location manager
     *
     * @return the instance of the location manager
     */
    public static synchronized LocationManagerSingleton getInstance() {
        if (instance == null) {
            instance = new LocationManagerSingleton();
        }
        return instance;
    }

    // Initialize with context (call this from your Activity)

    /**
     * Initializes the location manager singleton with the given context
     *
     * @param context the given context
     */
    public void init(Context context) {
        if (this.context == null) {
            this.context = context.getApplicationContext(); // Ensure the application context is used
        }
    }

    // Set location tracking flag

    /**
     * Gets if this user can be location tracked
     *
     * @return whether this user can be location tracked
     */
    public boolean isLocationTrackingEnabled() {
        return locationTrackingEnabled;
    }

    // Get location tracking flag

    /**
     * Sets whether this user can be location tracked
     *
     * @param enabled whether this user can be location tracked
     */
    public void setLocationTrackingEnabled(boolean enabled) {
        locationTrackingEnabled = enabled;
    }

    // Safely use context in singleton methods

    /**
     * Gets the context of the singleton
     *
     * @return the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Gets the location of the current user
     *
     * @return the location of the current user
     */
    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    /**
     * Sets the location of the current user
     *
     * @param geoPoint the location of the current user
     */
    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
