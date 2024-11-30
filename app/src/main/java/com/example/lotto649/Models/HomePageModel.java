package com.example.lotto649.Models;

import android.util.Log;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.EventState;
import com.example.lotto649.MyApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// TODO this isnt a model, just use array adapter and firestore calls from another class
/**
 * Model class for managing a collection of events.
 * Interacts with Firebase Firestore to fetch and manage event data.
 */
public class HomePageModel extends AbstractModel {
    // TODO name this myCreatedEvents
    private ArrayList<EventModel> myEvents;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Callback interface for fetching event documents.
     */
    public interface EventFetchCallback {

        /**
         * Called when events are fetched from Firestore.
         *
         * @param events List of DocumentSnapshot objects representing events.
         */
        void onCallback(List<DocumentSnapshot> events);
    }

    /**
     * Callback interface for retrieving the current user's events.
     */
    public interface MyEventsCallback {

        /**
         * Called when the user's events have been fetched.
         *
         * @param events ArrayList of EventModel instances representing the user's events.
         */
        void onEventsFetched(ArrayList<EventModel> events);
    }

    /**
     * Constructs an HomePageModel with an empty list of events.
     */
    public HomePageModel() {
        myEvents = new ArrayList<>();
    }

    /**
     * Fetches events associated with the current organizer (by organizer ID) from Firestore.
     *
     * @param callback Callback to handle the fetched events.
     * @param db       FirebaseFirestore instance for database operations.
     */
    public static void fetchEventsByOrganizerId(EventFetchCallback callback, FirebaseFirestore db) {
        String organizerId = MyApp.getInstance().getUserModel().getDeviceId();

        db.collection("events")
                .whereEqualTo("organizerId", organizerId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> eventList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            eventList.add(document);  // Add each document to the list
                        }
                        callback.onCallback(eventList);  // Pass the list to the callback
                    }
                });
    }

    public EventModel getEventFromFirebaseObject(DocumentSnapshot doc) {
        String eventId = doc.getId();
        String description = doc.getString("description");
        Date endDate = doc.getDate("endDate");
        Boolean geo = doc.getBoolean("geo");
        int numMaxEntrants = Objects.requireNonNull(doc.getLong("numberOfMaxEntrants")).intValue();
        int numSpots = Objects.requireNonNull(doc.getLong("numberOfSpots")).intValue();
        String organizerId = doc.getString("organizerId");
        String posterImage = doc.getString("posterImage");
        String qrCode = doc.getString("qrCode");
        Date startDate = doc.getDate("startDate");
        String stateStr = doc.getString("state");
        EventState state = EventState.OPEN;
        if (Objects.equals(stateStr, "WAITING")) {
            state = EventState.WAITING;
        } else if (Objects.equals(stateStr, "CLOSED")) {
            state = EventState.CLOSED;
        }
        String title = doc.getString("title");
        int waitingListSize = Objects.requireNonNull(doc.getLong("waitingListSize")).intValue();
        EventModel newEvent = new EventModel(title, description, numSpots, numMaxEntrants, startDate, endDate, posterImage, geo, qrCode, waitingListSize, state, db);
        newEvent.setOrganizerId(organizerId);
        newEvent.setEventId(eventId);
        return newEvent;
    }

    /**
     * Retrieves the events for the current user (organizer) and updates the myEvents list.
     *
     * @param callback Callback to handle the user's events once fetched.
     */
    public void getMyEvents(MyEventsCallback callback) {
        // TODO why are we using callbacks, we dont need them
        HomePageModel.fetchEventsByOrganizerId(new HomePageModel.EventFetchCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> documents) {
                for (DocumentSnapshot document : documents) {
                    EventModel event = getEventFromFirebaseObject(document);
                    myEvents.add(event);
                }
                callback.onEventsFetched(myEvents);
            }
        }, db);
    }
}
