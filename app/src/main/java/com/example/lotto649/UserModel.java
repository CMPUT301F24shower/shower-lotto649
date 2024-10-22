package com.example.lotto649;

import android.content.Context;
import android.provider.Settings;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * No-argument constructor for Firestore deserialization.
     * This constructor is required for creating instances of the `UserModel` when
     * reading from Firestore.
     */
    public UserModel() {
        // No initialization required
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
    public UserModel(Context context, String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        saveUserToFirestore();
    }

    /**
     * Constructor to create a new user model with the given name and email.
     *
     * @param context the application context used to retrieve the device ID
     * @param name the name of the user
     * @param email the email of the user
     */
    public UserModel(Context context, String name, String email) {
        this(context, name, email, null);
    }

    /**
     * Saves the current user data to Firestore.
     * This method is called during initialization to persist the user data.
     */
    public void saveUserToFirestore() {
        db.collection("users")
                .document(deviceId)
                .set(this)  // Saves the current user object to Firestore
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User saved successfully!");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error saving user: " + e.getMessage());
                });
    }

    /**
     * Updates Firestore with the current user's information.
     * This method is used whenever a setter modifies the user data to synchronize the changes.
     */
    private void updateFirestore() {
        if (deviceId == null) return; // Ensure the device ID exists

        db.collection("users")
                .document(deviceId)
                .update("name", this.name, "email", this.email, "phone", (this.phone == null) ? "" : this.phone)
                .addOnSuccessListener(aVoid -> {
                    // Log success (optional)
                })
                .addOnFailureListener(e -> {
                    // Log failure (optional)
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
        updateFirestore();
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
        updateFirestore();
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
        updateFirestore();
        notifyViews();
    }

    /**
     * Updates the current user's data with the provided user model.
     * This method is typically used when synchronizing user data.
     *
     * @param user the user model containing updated information
     */
    public void update(UserModel user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        updateFirestore();
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

    // No setter for device ID, as it is immutable after being set
}
