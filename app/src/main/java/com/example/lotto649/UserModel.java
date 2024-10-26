package com.example.lotto649;

import android.content.Context;
import android.provider.Settings;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * UserModel is the data model representing a user in the application.
 * It manages user-specific data such as name, email, phone, and device ID,
 * and interacts with Firestore to save and update user data.
 * <p>
 * This class extends `AbstractModel`, allowing it to notify associated views when data changes,
 * and provides methods to handle Firestore integration for saving and updating user information.
 * </p>
 */
public class UserModel extends AbstractModel {
    private String name;
    private String email;
    private String phone;
    private String deviceId;

    // Firestore instance for saving and updating user data
    private FirebaseFirestore db;
    private boolean savedToFirestore = false;

    /**
     * No-argument constructor for Firestore deserialization.
     * This constructor is required for creating instances of the `UserModel` when
     * reading from Firestore.
     */
    public UserModel() {
        // No initialization required
    }

    /**
     * Constructor to create a new user model without the given name, email, and phone number.
     * They will all be set to ""
     * Also retrieves the device ID of the current device and immediately saves the user to Firestore.
     *
     * @param context the application context used to retrieve the device ID
     * @param db the firestore database containing user info
     */
    public UserModel(Context context, FirebaseFirestore db) {
        this.name = "";
        this.email = "";
        this.phone = "";
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.db = db;
    }

    /**
     * Constructor to create a new user model with the given name, email, and phone number.
     * Also retrieves the device ID of the current device and immediately saves the user to Firestore.
     *
     * @param context the application context used to retrieve the device ID
     * @param name the name of the user
     * @param email the email of the user
     * @param phone the phone number of the user (optional)
     */
    public UserModel(Context context, String name, String email, String phone, FirebaseFirestore db) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.db = db;
//        saveUserToFirestore();
    }

    /**
     * Constructor to create a new user model with the given name and email.
     *
     * @param context the application context used to retrieve the device ID
     * @param name the name of the user
     * @param email the email of the user
     */
    public UserModel(Context context, String name, String email) {
        this(context, name, email, null, null);
    }

    /**
     * Saves the current user data to Firestore.
     * This method is called during initialization to persist the user data.
     */
    public void saveUserToFirestore(String name, String email, String phone) {
        if (savedToFirestore) return;
        db.collection("users")
                .document(deviceId)
                .set(new HashMap<String, String>() {{
                        put("name", name);
                        put("email", email);
                        put("phone", phone);
                }})  // Saves the current user object to Firestore
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User saved successfully!");
                    savedToFirestore = true;
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error saving user: " + e.getMessage());
                });
    }

    /**
     * Updates Firestore with the current user's information.
     * This method is used whenever a setter modifies the user data to synchronize the changes.
     */
    public void updateFirestore(String field, String value) {
        if (deviceId == null) return; // Ensure the device ID exists
        if (db == null) return;
        db.collection("users")
                .document(deviceId)
                .update(field, value)
                .addOnSuccessListener(aVoid -> {
                    // TODO: Add error handling to test for failure

                })
                .addOnFailureListener(e -> {
                    // TODO: Add error handling to test for failure

                });
    }

    /**
     * Gets the user's name.
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name and updates Firestore.
     * Notifies the views about the change.
     *
     * @param name the new name of the user
     */
    public void setName(String name) {
        this.name = name;
        updateFirestore("name", name);
        notifyViews();
    }

    /**
     * Gets the user's email.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email and updates Firestore.
     * Notifies the views about the change.
     *
     * @param email the new email of the user
     */
    public void setEmail(String email) {
        this.email = email;
        updateFirestore("email", email);
        notifyViews();
    }

    /**
     * Gets the user's phone number.
     *
     * @return the phone number of the user
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the user's phone number and updates Firestore.
     * Notifies the views about the change.
     *
     * @param phone the new phone number of the user (can be null)
     */
    public void setPhone(String phone) {
        this.phone = (phone == null) ? "" : phone;
        updateFirestore("phone", phone);
        notifyViews();
    }

    /**
     * Gets the device ID associated with the current user.
     *
     * @return the device ID of the user's device
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Gets if the user has been saved to firebase
     *
     * @return a boolean representing if the user has been saved to firestore
     */
    public boolean getSavedToFirestore() {
        return savedToFirestore;
    }

    /**
     * Sets the boolean tracking whether the user is saved to firestore to true
     */
    public void setSavedToFirestore() {
        // Once a user has been saved to firestore they should not be removed at any point
        savedToFirestore = true;
    }


    // No setter for device ID, as it is immutable after being set
}
