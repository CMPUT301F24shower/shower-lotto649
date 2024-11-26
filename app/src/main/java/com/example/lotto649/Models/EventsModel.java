package com.example.lotto649.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.MyApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

// TODO this isnt a model, just use array adapter and firestore calls from another class
/**
 * Model class for managing a collection of events.
 * Interacts with Firebase Firestore to fetch and manage event data.
 */
public class EventsModel extends AbstractModel {
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
     * Constructs an EventsModel with an empty list of events.
     */
    public EventsModel() {
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

    /**
     * Retrieves the events for the current user (organizer) and updates the myEvents list.
     *
     * @param callback Callback to handle the user's events once fetched.
     */
    public void getMyEvents(MyEventsCallback callback) {
        // TODO why are we using callbacks, we dont need them
        EventsModel.fetchEventsByOrganizerId(new EventsModel.EventFetchCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> documents) {
                for (DocumentSnapshot document : documents) {
                    // TODO use constructor here, why are we doing this
                    EventModel event = document.toObject(EventModel.class);
                    event.setEventId(document.getId());
                    event.setDb(db);
                    Log.e("Ohm", "Doc Id " + event.getEventId());
                    myEvents.add(event);
                }
                callback.onEventsFetched(myEvents);
            }
        }, db);
    }
}
