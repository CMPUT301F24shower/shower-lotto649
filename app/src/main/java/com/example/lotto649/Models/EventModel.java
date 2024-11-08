package com.example.lotto649.Models;

import android.content.Context;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.MyApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;

/**
 * EventModel represents an event in the application with attributes such as title, location,
 * cost, description, number of spots, event type, and poster image.
 * This model class also handles saving and updating event data in Firestore.
 */
public class EventModel extends AbstractModel implements Serializable {
    private String title;
    private String facilityId;
    private String organizerId;
    private double cost;
    private String description;
    private int numberOfSpots;
    private int numberOfMaxEntrants;
    private Date startDate;
    private Date endDate;
    private String posterImage; // Placeholder for image class
    private boolean geo;
    private String qrCode;
    private String qrCodeData;
    private ArrayList<UserModel> waitingList;

    private FirebaseFirestore db;
    private boolean savedToFirestore = false;
    private String eventId;
    public String getEventId() {
        return eventId;
    };

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Callback interface for handling FacilityModel retrieval asynchronously.
     */
    public interface FacilityCallback {
        void onCallback(FacilityModel facility);
    }

    /**
     * No-argument constructor for Firestore deserialization.
     * This constructor is required for creating instances of the `EventModel` when
     * reading from Firestore.
     */
    public EventModel() {
        // No initialization required
    }

    private void clear(){
        this.title = "";
        this.facilityId = "";
        this.cost = 0;
        this.description = "";
        this.numberOfSpots = 0;
        this.numberOfMaxEntrants = -1;
        this.startDate = new Date();
        this.endDate =  new Date();
        this.posterImage = "";
        this.geo = false;
        this.waitingList = new ArrayList<>();
    }

    /**
     * Constructs a new EventModel with the specified context and Firestore database instance.
     * <p>
     * This constructor initializes the event's title, facility ID, cost, description, number of spots,
     * event type, and poster image with default values. It also generates a QR code for the event and
     * saves the event data to Firestore.
     *
     * @param context the context in which this model operates, typically passed from an Activity or Fragment
     * @param db      the Firestore database instance used to store event data
     */
    public EventModel(Context context, FirebaseFirestore db) {
        clear();
        this.organizerId = MyApp.getInstance().getUserModel().getDeviceId();
        this.db = db;
        //generateQrCode();
        //saveEventToFirestore();
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
     * @param db the Firestore database instance
     */
    public EventModel(Context context, String title, String facilityId, double cost, String description, int numberOfSpots,
                      Date startDate, Date endDate, boolean geo, FirebaseFirestore db) {
        this(context, title, facilityId, cost, description, numberOfSpots,
                -1, startDate, endDate, null, geo, null, new ArrayList<UserModel>(), db);
    }

    public EventModel(Context context, String title, String facilityId, double cost, String description, int numberOfSpots,
                      int numberOfMaxEntrants, Date startDate, Date endDate, boolean geo, FirebaseFirestore db) {
        this(context, title, facilityId, cost, description, numberOfSpots,
                numberOfMaxEntrants, startDate, endDate, null, geo, null, new ArrayList<UserModel>(), db);
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
     * @param db the Firestore database instance
     */
    public EventModel(Context context, String title, String facilityId, double cost, String description, int numberOfSpots,
                      int numberOfMaxEntrants, Date startDate, Date endDate, Object posterImage, boolean geo, String qrCodeUrl,
                      ArrayList<UserModel> waitingList, FirebaseFirestore db) {
        this.title = title;
        this.facilityId = facilityId;
        this.organizerId = MyApp.getInstance().getUserModel().getDeviceId();
        this.cost = cost;
        this.description = description;
        this.numberOfSpots = numberOfSpots;
        this.numberOfMaxEntrants = numberOfMaxEntrants;
        this.startDate = startDate;
        this.endDate = endDate;
        this.posterImage = posterImage;
        this.geo = geo;
        this.db = db;
        this.qrCodeData = title + description + numberOfSpots + numberOfMaxEntrants + cost;
        this.waitingList = waitingList;
        //saveEventToFirestore();
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
                    put("organizerId", organizerId);
                    put("cost", cost);
                    put("description", description);
                    put("numberOfSpots", numberOfSpots);
                    put("numberOfMaxEntrants", numberOfMaxEntrants);
                    put("startDate", startDate);
                    put("endDate", endDate);
                    put("qrCode", qrCode);
                    put("posterImage", posterImage);
                    put("geo",geo);
                    put("waitingList", serializeWaitingList());
                }})
                .addOnSuccessListener(documentReference -> {
                    eventId = documentReference.getId();
                    savedToFirestore = true;
                    setQrCode(qrCode);
                    System.out.println("Event saved successfully with ID: " + eventId);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error saving event: " + e.getMessage());
                });
    }

    /**
     * Removes the event data to Firestore.
     * If the event has already been removed, this method does nothing.
     */
    public void removeEventFromFirestore() {
        removeEventFirestore();
        clear();
        notifyViews();
    }

    private void removeEventFirestore(){
        if (eventId == null || eventId.isEmpty()) {
            System.err.println("Event ID is not set. Cannot delete event.");
            return;
        }
        db.collection("events")
                .document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    savedToFirestore = false;
                    System.out.println("Event removed successfully with ID: " + eventId);
                    eventId = null; // Clear the ID after deletion
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error removing event: " + e.getMessage());
                });
    }

    public void setEventId(String eventId){
        //removeEventFirestore();
        this.eventId = eventId;
        //saveEventToFirestore();
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
     * Retrieves the organizer ID associated with this event.
     *
     * @return the organizer ID as a string
     */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the organizer ID associated with this event and updates Firestore.
     *
     * @param organizerId the new organizer ID
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
        updateFirestore("organizerId", organizerId);
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
     * Retrieves the number of max entrants for the event.
     *
     * @return the number of max entrants as an integer
     */
    public int getNumberOfMaxEntrants() {
        return numberOfMaxEntrants;
    }

    /**
     * Sets the number of max entrants for the event and updates Firestore.
     *
     * @param numberOfMaxEntrants the new number of max entrants
     */
    public void setNumberOfMaxEntrants(int numberOfMaxEntrants) {
        this.numberOfMaxEntrants = numberOfMaxEntrants;
        updateFirestore("numberOfMaxEntrants", numberOfMaxEntrants);
        notifyViews();
    }

    /**
     * Retrieves start date for the event lottery.
     *
     * @return start date for the event lottery as a date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date for the event lottery and updates Firestore.
     *
     * @param startDate the new start date for the event lottery
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        updateFirestore("startDate", startDate);
        notifyViews();
    }

    /**
     * Retrieves end date for the event lottery.
     *
     * @return end date for the event lottery as a date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date for the event lottery and updates Firestore.
     *
     * @param endDate the new end date for the event lottery
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        updateFirestore("endDate", endDate);
        notifyViews();
    }

    /**
     * Retrieves the poster image associated with the event.
     *
     * @return the poster image
     */
    public String getPosterImage() {
        return posterImage;
    }

    /**
     * Sets the poster image for the event and updates Firestore.
     *
     * @param posterImage the new poster image
     */
    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
        updateFirestore("posterImage", posterImage);
        notifyViews();
    }

    /**
     * Retrieves the geolocation associated with the event.
     *
     * @return the geolocation as a boolean
     */
    public boolean getGeo() {
        return geo;
    }

    /**
     * Sets the geolocation for the event and updates Firestore.
     *
     * @param geo the geolocation setting
     */
    public void setGeo(boolean geo) {
        this.geo = geo;
        updateFirestore("geo", geo);
        notifyViews();
    }

    /**
     * Retrieves the path to the QR code generated for this event.
     *
     * @return the QR code path as a string
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     * Sets the QR code for the event and updates Firestore.
     *
     * @param qrCode the QR code setting
     */
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
//        updateFirestore("qrCode", qrCode);
//        notifyViews();
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
    public boolean addToWaitingList(UserModel entrant) {
        if (0 < numberOfMaxEntrants && numberOfMaxEntrants <= waitingList.size()) return false;
        waitingList.add(entrant);
        updateFirestore("waitingList", serializeWaitingList());
        notifyViews();
        return true;
    }

    /**
     * Serializes the waiting list to a list of user IDs for Firestore storage.
     *
     * @return a list of serialized waiting list entries as strings
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
        this.qrCode = QrCodeModel.generateHash(this.qrCode);
    }

    /**
     * Serializes the EventModel by writing the eventId first, then the rest of the fields.
     *
     * @param out the output stream to write data to
     * @throws IOException if any I/O error occurs
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject(); // Default serialization
        out.writeObject(eventId); // Serialize eventId as unique identifier
    }

    /**
     * Deserializes the EventModel by reading the eventId first, then the rest of the fields.
     *
     * @param in the input stream to read data from
     * @throws IOException if any I/O error occurs
     * @throws ClassNotFoundException if the class is not found
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Default deserialization
        eventId = (String) in.readObject(); // Deserialize eventId
    }
}
