package com.example.lotto649.Models;

import android.content.Context;
import com.example.lotto649.AbstractClasses.AbstractModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EventModel represents an event in the application with attributes such as title, location,
 * cost, description, number of spots, event type, and poster image.
 * This model class also handles saving and updating event data in Firestore.
 */
public class EventModel extends AbstractModel {
    private String title;
    private String facilityId;
    private double cost;
    private String description;
    private int numberOfSpots;
    private String eventType;
    private Object posterImage; // Placeholder for image class
    private String qrCodePath;

    private final ArrayList<UserModel> waitingList = new ArrayList<>();

    private FirebaseFirestore db;
    private boolean savedToFirestore = false;
    private String eventId;

    /**
     * Callback interface for handling FacilityModel retrieval asynchronously.
     */
    public interface FacilityCallback {
        void onCallback(FacilityModel facility);
    }

    /**
     * Constructor to create an EventModel instance.
     * Automatically generates a QR code and initializes the Firestore database instance.
     *
     * @param context the application context
     * @param title the title of the event
     * @param facilityId the ID of the FacilityModel document representing the event location
     * @param cost the cost to attend the event
     * @param description a description of the event
     * @param numberOfSpots the number of spots available for the event
     * @param eventType the type/category of the event
     * @param db the Firestore database instance
     */
    public EventModel(Context context, String title, String facilityId, double cost, String description, int numberOfSpots, String eventType, FirebaseFirestore db) {
        this.title = title;
        this.facilityId = facilityId;
        this.cost = cost;
        this.description = description;
        this.numberOfSpots = numberOfSpots;
        this.eventType = eventType;
        this.posterImage = null;
        this.db = db;
        generateQrCode();
        saveEventToFirestore();
    }

    /**
     * Saves the event data to Firestore.
     * If the event has already been saved, this method does nothing.
     */
    public void saveEventToFirestore() {
        if (savedToFirestore) return;

        db.collection("events")
                .add(new HashMap<String, Object>() {{
                    put("title", title);
                    put("facilityId", facilityId);
                    put("cost", cost);
                    put("description", description);
                    put("numberOfSpots", numberOfSpots);
                    put("eventType", eventType);
                    put("qrCodePath", qrCodePath);
                    put("posterImage", posterImage);
                    put("waitingList", serializeWaitingList());
                }})
                .addOnSuccessListener(documentReference -> {
                    eventId = documentReference.getId();
                    savedToFirestore = true;
                    System.out.println("Event saved successfully with ID: " + eventId);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error saving event: " + e.getMessage());
                });
    }

    /**
     * Updates a specific field in Firestore for this event.
     *
     * @param field the field to update
     * @param value the new value for the specified field
     */
    public void updateFirestore(String field, Object value) {
        if (eventId == null || db == null) return;

        db.collection("events")
                .document(eventId)
                .update(field, value)
                .addOnSuccessListener(aVoid -> System.out.println("Event field updated successfully!"))
                .addOnFailureListener(e -> {
                    System.err.println("Error updating event: " + e.getMessage());
                });
    }

    /**
     * Fetches the full FacilityModel from Firestore using the stored facility ID.
     *
     * @param callback callback to handle the fetched FacilityModel asynchronously
     */
    public void fetchFacility(FacilityCallback callback) {
        if (facilityId == null || db == null) return;

        DocumentReference facilityRef = db.collection("facilities").document(facilityId);
        facilityRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    FacilityModel facility = documentSnapshot.toObject(FacilityModel.class);
                    callback.onCallback(facility);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error fetching facility: " + e.getMessage());
                });
    }

    /**
     * Retrieves the title of the event.
     *
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the event and updates Firestore.
     *
     * @param title the new title of the event
     */
    public void setTitle(String title) {
        this.title = title;
        updateFirestore("title", title);
        notifyViews();
    }

    /**
     * Retrieves the facility ID associated with this event.
     *
     * @return the facility ID as a string
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the facility ID associated with this event and updates Firestore.
     *
     * @param facilityId the new facility ID
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
        updateFirestore("facilityId", facilityId);
        notifyViews();
    }

    /**
     * Retrieves the cost of the event.
     *
     * @return the cost as a double
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the cost of the event and updates Firestore.
     *
     * @param cost the new cost for the event
     */
    public void setCost(double cost) {
        this.cost = cost;
        updateFirestore("cost", cost);
        notifyViews();
    }

    /**
     * Retrieves the description of the event.
     *
     * @return the event description as a string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event and updates Firestore.
     *
     * @param description the new description of the event
     */
    public void setDescription(String description) {
        this.description = description;
        updateFirestore("description", description);
        notifyViews();
    }

    /**
     * Retrieves the number of available spots for the event.
     *
     * @return the number of spots as an integer
     */
    public int getNumberOfSpots() {
        return numberOfSpots;
    }

    /**
     * Sets the number of available spots for the event and updates Firestore.
     *
     * @param numberOfSpots the new number of spots
     */
    public void setNumberOfSpots(int numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
        updateFirestore("numberOfSpots", numberOfSpots);
        notifyViews();
    }

    /**
     * Retrieves the event type (e.g., "conference" or "workshop").
     *
     * @return the event type as a string
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the event type and updates Firestore.
     *
     * @param eventType the new event type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
        updateFirestore("eventType", eventType);
        notifyViews();
    }

    /**
     * Retrieves the poster image associated with the event.
     *
     * @return the poster image as an object (update this type as needed)
     */
    public Object getPosterImage() {
        return posterImage;
    }

    /**
     * Sets the poster image for the event and updates Firestore.
     *
     * @param posterImage the new poster image (update type as needed)
     */
    public void setPosterImage(Object posterImage) {
        this.posterImage = posterImage;
        updateFirestore("posterImage", posterImage);
        notifyViews();
    }

    /**
     * Retrieves the path to the QR code generated for this event.
     *
     * @return the QR code path as a string
     */
    public String getQrCodePath() {
        return qrCodePath;
    }

    /**
     * Retrieves the list of users on the waiting list for this event.
     *
     * @return an ArrayList of UserModel objects representing the waiting list
     */
    public ArrayList<UserModel> getWaitingList() {
        return waitingList;
    }

    /**
     * Adds an entrant to the waiting list and saves the updated waiting list to Firestore.
     *
     * @param entrant the user to add to the waiting list
     */
    public void addToWaitingList(UserModel entrant) {
        waitingList.add(entrant);
        updateFirestore("waitingList", serializeWaitingList());
        notifyViews();
    }

    /**
     * Serializes the waiting list to a list of user IDs for Firestore storage.
     *
     * @return a list of serialized waiting list entries as strings
     */
    private List<String> serializeWaitingList() {
        return waitingList.stream()
                .map(UserModel::getDeviceId) // Assuming UserModel has a getDeviceId() method
                .collect(Collectors.toList());
    }

    /**
     * Generates a QR code for the event (mock implementation).
     */
    private void generateQrCode() {
        this.qrCodePath = QrCodeModel.generateForEvent(this);
    }
}
