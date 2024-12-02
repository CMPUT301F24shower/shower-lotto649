package com.example.lotto649.Models;

import android.content.Context;
import android.provider.Settings;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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
    private boolean entrant;
    private boolean organizer;
    private boolean admin;
    private String deviceId;
    private String profileImage;

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
        this.entrant = true;
        this.organizer = false;
        this.admin = false;
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.db = db;
        this.profileImage = "";
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
        this.entrant = true;
        this.organizer = false;
        this.admin = false;
        this.deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.db = db;
        this.profileImage = "";

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
     * Users are default set to entrants
     */
    public void saveUserToFirestore() {
        if (savedToFirestore) return;
        db.collection("users")
                .document(deviceId)
                .set(new HashMap<String, Object>() {{
                        put("name", name);
                        put("email", email);
                        put("phone", phone);
                        put("entrant", entrant);
                        put("organizer", organizer);
                        put("profileImage", profileImage);
                }}, SetOptions.merge())  // Saves the current user object to Firestore
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
    public void updateFirestore(String field, Object value) {
        if (deviceId == null) return; // Ensure the device ID exists
        if (db == null) return;
        db.collection("users")
                .document(deviceId)
                .update(field, value)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
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
     * Sets the user's entrant privilege and updates Firestore.
     * Notifies the views about the change.
     *
     * @param bool the new privilege of the user (must be true or false)
     */
    public void setEntrant(Boolean bool) {
        this.entrant = bool;
        updateFirestore("entrant", bool);
        notifyViews();
    }

    /**
     * Gets the user's entrant privilege.
     *
     * @return the entrant privilege of the user
     */
    public Boolean getEntrant() {
        return entrant;
    }

    /**
     * Sets the user's organizer privilege and updates Firestore.
     * Notifies the views about the change.
     *
     * @param bool the new privilege of the user (must be true or false)
     */
    public void setOrganizer(Boolean bool) {
        updateFirestore("organizer", bool);
        notifyViews();
    }

    /**
     * Gets the user's organizer privilege.
     *
     * @return the organizer privilege of the user
     */
    public Boolean getOrganizer() {
        return organizer;
    }

    /**
     * Sets the user's admin privilege and updates Firestore.
     * Notifies the views about the change.
     *
     * @param bool the new privilege of the user (must be true or false)
     */
    public void setAdmin(Boolean bool) {
        updateFirestore("admin", bool);
        notifyViews();
    }

    /**
     * Gets the user's admin privilege.
     *
     * @return the admin privilege of the user
     */
    public Boolean getAdmin() {
        return admin;
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

    /**
     * Get the users initials for the generated profile pic should they not have an image saved
     * @return The users initials
     */
    public String getInitials() {
        if (name == null || name.isEmpty()) {
            return "";
        }
        StringBuilder initials = new StringBuilder();
        String[] nameParts = name.split(" ");

        for (String part : nameParts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    /**
     * Gets the user's profile image uri
     *
     * @return the profile image uri
     */
    public String getProfileImage() {
        return profileImage;
    }

    /**
     * Sets the users profile image uri
     *
     * @param profileImage the new profile image uri
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
