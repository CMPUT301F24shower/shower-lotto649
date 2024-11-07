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
     * This method provides the {@link UserModel} obtained from Firestore.
     *
     * @param user The UserModel object representing the user data, either retrieved
     *             from Firestore or created as a new default user.
     *             TODO: This needs updating
     */
    void onCallback(String name, String email, String phone, String profileImage);
}
