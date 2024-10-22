package com.example.lotto649;

import android.content.Context;
import android.provider.Settings;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The Data Model for Our Users
 *
 * Main purpose of this class is to manage the user data
 */
public class UserModel {
    private String name;
    private String email;
    private String phone;
    private String deviceId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // No-argument constructor
    public UserModel() {

    }

    public UserModel(Context context, String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        saveUserToFirestore();
    }
    public UserModel(Context context, String name, String email) {
        this(context, name, email, null);
        }



    public void saveUserToFirestore() {
        db.collection("users")
                .document(deviceId)
                .set(this)  // Set the current user object to Firestore
                .addOnSuccessListener(aVoid -> {
                    // Handle success (you can log or notify the controller)
                    System.out.println("User saved successfully!");
                })
                .addOnFailureListener(e -> {
                    // Handle failure (you can log or notify the controller)
                    System.err.println("Error saving user: " + e.getMessage());
                });
    }

    private void updateFirestore() {
//        db.collection("users")
//                .document(deviceId)
//                .update("name", this.name, "email", this.email, "phone", (this.phone == null) ? "" : this.phone)
//                .addOnSuccessListener(aVoid -> {
//                    // Handle success, e.g., log or notify
//                    System.out.println("User updated successfully!");
//                })
//                .addOnFailureListener(e -> {
//                    // Handle failure, e.g., log or notify
//                    System.err.println("Error updating user: " + e.getMessage());
//                });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateFirestore();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        updateFirestore();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null) {
            this.phone = "";
        } else {
            this.phone = phone;
        }
        updateFirestore();
    }

    public String getDeviceId() {
        return deviceId;
    }

    // No setter for device ID, it should not be updated

}
