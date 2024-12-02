package com.example.lotto649.Models;

import android.util.Log;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.EventState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;


/**
 * EventModel represents an event in the application with attributes such as title, location,
 * description, number of spots, event type, and poster image.
 * This model class also handles saving and updating event data in Firestore.
 */
public class EventModel extends AbstractModel implements Serializable {
    private String title;
    private String organizerId;
    private String description;
    private int numberOfSpots;
    private int numberOfMaxEntrants;
    private Date startDate;
    private Date endDate;
    private String posterImage;
    private boolean geo;
    private String qrCode;
    private EventState state = EventState.OPEN;

    private FirebaseFirestore db;
    private boolean savedToFirestore = false;
    private String eventId;
    public String getEventId() {
        return eventId;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
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
        this.organizerId = "";
        this.description = "";
        this.numberOfSpots = 0;
        this.numberOfMaxEntrants = -1;
        this.startDate = new Date();
        this.endDate =  new Date();
        this.posterImage = "";
        this.geo = false;
    }

    /**
     * Constructs a new EventModel with the Firestore database instance.
     * <p>
     * This constructor initializes the event's title, facility ID, description, number of spots,
     * event type, and poster image with default values. It also generates a QR code for the event and
     * saves the event data to Firestore.
     *
     * @param db      the Firestore database instance used to store event data
     */
    public EventModel(FirebaseFirestore db) {
        clear();
        this.organizerId = MyApp.getInstance().getUserModel().getDeviceId();
        this.db = db;
    }

    /**
     * Constructor to create an EventModel instance.
     * Automatically generates a QR code and initializes the Firestore database instance.
     *
     * @param title the title of the event
     * @param description a description of the event
     * @param numberOfSpots the number of spots available for the event
     * @param db the Firestore database instance
     */
    public EventModel(String title, String description, int numberOfSpots,
                      Date startDate, Date endDate, boolean geo, EventState state, FirebaseFirestore db) {
        this(title, description, numberOfSpots,
                -1, startDate, endDate, null, geo, null, state, db);
    }

    public EventModel(String title, String description, int numberOfSpots,
                      int numberOfMaxEntrants, Date startDate, Date endDate, boolean geo, EventState state, FirebaseFirestore db) {
        this(title, description, numberOfSpots,
                numberOfMaxEntrants, startDate, endDate, null, geo, null, state, db);
    }

    /**
     * Constructor to create an EventModel instance.
     * Automatically generates a QR code and initializes the Firestore database instance.
     *
     * @param title the title of the event
     * @param description a description of the event
     * @param numberOfSpots the number of spots available for the event
     * @param db the Firestore database instance
     */
    public EventModel(String title, String description, int numberOfSpots,
                      int numberOfMaxEntrants, Date startDate, Date endDate, String posterImage, boolean geo, String qrCodeUrl,
                      EventState state, FirebaseFirestore db) {
        this.title = title;
        this.organizerId = MyApp.getInstance().getUserModel().getDeviceId();
        this.description = description;
        this.numberOfSpots = numberOfSpots;
        this.numberOfMaxEntrants = numberOfMaxEntrants;
        this.startDate = startDate;
        this.endDate = endDate;
        this.posterImage = posterImage;
        this.geo = geo;
        this.db = db;
        this.qrCode = qrCodeUrl;
        this.state = state;
    }

    /**
     * Saves the event data to Firestore.
     * If the event has already been saved, this method does nothing.
     */
    public void saveEventToFirestore(OnSuccessListener<String> onSuccess) {
        // TODO move to helper
        if (savedToFirestore) return;

        Task<DocumentReference> task = db.collection("events")
                .add(new HashMap<String, Object>() {{
                    put("title", title);
                    put("organizerId", organizerId);
                    put("description", description);
                    put("numberOfSpots", numberOfSpots);
                    put("numberOfMaxEntrants", numberOfMaxEntrants);
                    put("startDate", startDate);
                    put("endDate", endDate);
                    put("qrCode", qrCode);
                    put("posterImage", posterImage);
                    put("geo",geo);
                    put("state", state.name());
                }})
                .addOnSuccessListener(documentReference -> {
                    eventId = documentReference.getId();
                    savedToFirestore = true;
                    System.out.println("Event saved successfully with ID: " + eventId);

                    if (onSuccess != null) {
                        onSuccess.onSuccess(eventId);
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error saving event: " + e.getMessage());
                });
        }

    // TODO can we remove this variable
    /**
     * Retrieves if saved to firestore.
     *
     * @return if saved to firestore as a boolean
     */
    public boolean getSavedToFirestore() { return savedToFirestore; }

    /**
     * Sets the Firestore save status.
     *
     * @param savedToFirestore the new Firestore save status
     */
    public void setSavedToFirestore(boolean savedToFirestore) {
        this.savedToFirestore = savedToFirestore;
    }

    /**
     * Removes the event data to Firestore.
     * If the event has already been removed, this method does nothing.
     */
    public void removeEventFromFirestore() {
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
        clear();
        notifyViews();
    }

    // TODO this should never change
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
     * Retrieves the organizer ID associated with this event.
     *
     * @return the organizer ID as a string
     */
    public String getOrganizerId() {
        return organizerId;
    }

    public void getLocation(Consumer<String> callback) {
        db.collection("facilities").document(organizerId).get().addOnSuccessListener(
                doc -> {
                    if (doc.exists()) {
                        String address = doc.getString("address");
                        callback.accept(address); // Pass the result to the callback
                    } else {
                        callback.accept(null); // No document found
                    }
                }).addOnFailureListener(e -> {
            Log.e("Ohm", "Error fetching location: ", e);
            callback.accept(null); // Handle errors
        });
    }

    public void setState(EventState state) {
        this.state = state;
        updateFirestore("state", state.name());
        notifyViews();
    }

    public EventState getState() {
        return this.state;
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
    // TODO this should never be set

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
        // updateFirestore("posterImage", posterImage);
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

        Log.e("GDEEP", "Updating Firestore with QR Code");
        updateFirestore("qrCode", qrCode);
        notifyViews();
    }


    /**
     * Retrieves the list of users on the waiting list for this event.
     *
     * @return an ArrayList of UserModel objects representing the waiting list
     */
    public ArrayList<String> getWaitingList() {
        ArrayList<String> waitingList = new ArrayList<>();
        db.collection("signUps").whereEqualTo("eventId",eventId)
            .get()
            .addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            waitingList.add(doc.getString("userId"));
                        }
                    }
                }
            );
        return waitingList;
    }


    public void doDraw() {
        if (!getState().equals(EventState.OPEN)) return;

        db.collection("signUps")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        Collections.shuffle(docs);
                        int i = 0;
                        for (DocumentSnapshot doc : docs) {
                            // TODO this is untested
                            HashMap<String, Object> data = new HashMap<>(doc.getData());
                            data.put("hasSeenNoti", false);
                            if (i++ < numberOfSpots) {
                                Log.d("JASON LOTTERY", "Choosing winner " + doc.getString("userId"));
                                db.collection("winners").document(doc.getId()).set(data);
                            } else {
                                Log.d("JASON LOTTERY", "Choosing loser " + doc.getString("userId"));
                                db.collection("notSelected").document(doc.getId()).set(data);
                            }
                        }
                    }
                });
        state = EventState.WAITING;
        updateFirestore("state", "WAITING");
        notifyViews();
    }

    public void doReplacementDraw() {
        // This only draws 1 additional user
        if (!getState().equals(EventState.WAITING)) return;

        db.collection("notSelected")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        Collections.shuffle(docs);
                        int i = 0;
                        for (DocumentSnapshot doc : docs) {
                            if (i == 0) {
                                HashMap<String, Object> data = new HashMap<>(doc.getData());
                                data.put("hasSeenNoti", false);
                                db.collection("winners").document(doc.getId()).set(data);
                                db.collection("notSelected").document(doc.getId()).delete();
                                i++;
                            }
                        }
                    }
                });
        notifyViews();
    }

    /**
     * Generates a QR code for the event (mock implementation).
     */
    private void generateQrCode() {
        this.qrCode = QrCodeModel.generateHash(this.qrCode);
    }
}
