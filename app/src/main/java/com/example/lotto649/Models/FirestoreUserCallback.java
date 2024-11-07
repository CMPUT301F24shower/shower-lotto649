/**
 * FirestoreUserCallback is an interface used to handle the asynchronous callback
 * after querying Firestore for a user's data.
 * <p>
 * This callback is invoked when the Firestore query for a user's data completes.
 * It provides a {@link UserModel} object that either contains the existing user data
 * or a new default user if no data is found in Firestore.
 * </p>
 */
package com.example.lotto649.Models;

public interface FirestoreUserCallback {
    /**
     * Called when the Firestore query for user data is completed.
     *
     * <p>This method provides the user details obtained from Firestore, including the user's
     * name, email, phone, and profile image URL.</p>
     *
     * @param name          The name of the user, retrieved from Firestore or provided as a default.
     * @param email         The email address of the user, retrieved from Firestore or provided as a default.
     * @param phone         The phone number of the user, retrieved from Firestore or provided as a default.
     * @param profileImage  The URL of the user's profile image, retrieved from Firestore or provided as a default.
     */
    void onCallback(String name, String email, String phone, String profileImage);
}
