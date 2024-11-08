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

public class EventsModel extends AbstractModel {
    private ArrayList<EventModel> myEvents;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface EventFetchCallback {
        void onCallback(List<DocumentSnapshot> events);
    }

    public interface MyEventsCallback {
        void onEventsFetched(ArrayList<EventModel> events);
    }

    public EventsModel() {
        myEvents = new ArrayList<>();
    }

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

    public void getMyEvents(MyEventsCallback callback) {
        EventsModel.fetchEventsByOrganizerId(new EventsModel.EventFetchCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> documents) {
                for (DocumentSnapshot document : documents) {
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
