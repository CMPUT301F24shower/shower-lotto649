package com.example.lotto649.Models;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

/**
 * Interface for administrative actions, including removing inappropriate items
 * and browsing existing entries in the database.
 */
public interface AdminInterface {

    /**
     * Removes an inappropriate event from the database.
     *
     * @param db    The FirebaseFirestore instance for database access.
     * @param event The EventModel representing the event to be removed.
     */
    void remove(FirebaseFirestore db, EventModel event);

    /**
     * Removes an inappropriate image associated with an event from the database.
     *
     * @param db    The FirebaseFirestore instance for database access.
     * @param event The EventModel representing the event whose image is to be removed.
     */
    void removeImage(FirebaseFirestore db, EventModel event);

    /**
     * Removes an inappropriate user profile from the database.
     *
     * @param db   The FirebaseFirestore instance for database access.
     * @param user The UserModel representing the user profile to be removed.
     */
    void remove(FirebaseFirestore db, UserModel user);

    /**
     * Removes an inappropriate image associated with a user profile from the database.
     *
     * @param db   The FirebaseFirestore instance for database access.
     * @param user The UserModel representing the user profile whose image is to be removed.
     */
    void removeImage(FirebaseFirestore db, UserModel user);

    /**
     * Removes an inappropriate facility entry from the database.
     *
     * @param db       The FirebaseFirestore instance for database access.
     * @param facility The FacilityModel representing the facility to be removed.
     */
    void remove(FirebaseFirestore db, FacilityModel facility);

    /**
     * Removes an inappropriate image associated with a facility from the database.
     *
     * @param db       The FirebaseFirestore instance for database access.
     * @param facility The FacilityModel representing the facility whose image is to be removed.
     */
    void removeImage(FirebaseFirestore db, FacilityModel facility);

    /**
     * Removes an inappropriate hashed QR code entry from the database.
     *
     * @param qrCode The QrCodeModel representing the QR code to be removed.
     */
    void remove(QrCodeModel qrCode);

    /**
     * Browses and retrieves a list of all events from the database.
     *
     * @param db The FirebaseFirestore instance for database access.
     * @return An ArrayList of EventModel representing all events in the database.
     */
    ArrayList<EventModel> browseEvents(FirebaseFirestore db);

    /**
     * Browses and retrieves a list of all user profiles from the database.
     *
     * @param db The FirebaseFirestore instance for database access.
     * @return An ArrayList of UserModel representing all user profiles in the database.
     */
    ArrayList<UserModel> browseProfiles(FirebaseFirestore db);

    /**
     * Browses and retrieves a list of all images in the database.
     *
     * Note: The type of images may vary, so the method returns a generic ArrayList of Objects.
     * It is recommended to update this method once the exact image type is defined.
     *
     * @param db The FirebaseFirestore instance for database access.
     * @return An ArrayList of Objects representing images in the database.
     */
    ArrayList<Object> browseImages(FirebaseFirestore db);
}
