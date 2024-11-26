package com.example.lotto649.Models;

/**
 * Callback interface for checking if a user has admin privileges.
 * Implement this interface to handle the result of a Firestore query that determines admin status.
 */
public interface FirestoreIsAdminCallback {

    /**
     * Called when the admin status check is complete.
     *
     * @param isAdmin True if the user has admin privileges, false otherwise.
     */
    void onCallback(boolean isAdmin);
}

// TODO this callback isnt needed