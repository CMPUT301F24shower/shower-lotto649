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
     * Called when the Firestore query is completed.
     * This method provides user details obtained from Firestore.
     *
     * @param name  The user's name, retrieved from Firestore or set as a default.
     * @param email The user's email, retrieved from Firestore or set as a default.
     * @param phone The user's phone number, retrieved from Firestore or set as a default.
     */
    void onCallback(String name, String email, String phone);

}
