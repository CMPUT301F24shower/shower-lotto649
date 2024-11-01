package com.example.lotto649.Models;

import android.content.Context;
import com.example.lotto649.AbstractClasses.AbstractModel;
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
    private FacilityModel location;
    private double cost;
    private String description;
    private int numberOfSpots;
    private String eventType;
    private Object posterImage; // edit when image class found
    private String qrCodePath;

    private final ArrayList<UserModel> waitingList = new ArrayList<>();

    private FirebaseFirestore db;
    private boolean savedToFirestore = false;
    private String eventId;

    /**
     * Constructor to create an EventModel instance.
     * Automatically generates a QR code and initializes the Firestore database instance.
     *
     * @param context the application context
     * @param title the title of the event
     * @param location the FacilityModel object representing the event location
     * @param cost the cost to attend the event
     * @param description a description of the event
     * @param numberOfSpots the number of spots available for the event
     * @param eventType the type/category of the event
     * @param db the Firestore database instance
     */
    public EventModel(Context context, String title, FacilityModel location, double cost, String description, int numberOfSpots, String eventType, FirebaseFirestore db) {
        this.title = title;
        this.location = location;
        this.cost = cost;
        this.description = description;
        this.numberOfSpots = numberOfSpots;
        this.eventType = eventType;
        this.posterImage = null; // edit when image class found
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
                    put("location", location.getAddress());
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
                .addOnFailureListener(e -> System.err.println("Error updating event: " + e.getMessage()));
    }

    // Getters and setters with Firestore updates

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        updateFirestore("title", title);
        notifyViews();
    }

    public FacilityModel getLocation() {
        return location;
    }

    public void setLocation(FacilityModel location) {
        this.location = location;
        updateFirestore("location", location.getAddress());
        notifyViews();
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
        updateFirestore("cost", cost);
        notifyViews();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateFirestore("description", description);
        notifyViews();
    }

    public int getNumberOfSpots() {
        return numberOfSpots;
    }

    public void setNumberOfSpots(int numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
        updateFirestore("numberOfSpots", numberOfSpots);
        notifyViews();
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
        updateFirestore("eventType", eventType);
        notifyViews();
    }

    public Object getPosterImage() { // edit when image class found
        return posterImage;
    }

    public void setPosterImage(Object posterImage) { // edit when image class found
        this.posterImage = posterImage;
        updateFirestore("posterImage", posterImage);
        notifyViews();
    }

    public String getQrCodePath() {
        return qrCodePath;
    }

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
     * Serializes the waiting list to a list of user IDs or names for Firestore storage.
     *
     * @return a list of serialized waiting list entries
     */
    private List<String> serializeWaitingList() {
        return waitingList.stream()
                .map(UserModel::getDeviceId)
                .collect(Collectors.toList());
    }

    /**
     * Generates a QR code for the event (mock implementation).
     */
    private void generateQrCode() {
        this.qrCodePath = QrCodeModel.generateForEvent(this);
    }
}
