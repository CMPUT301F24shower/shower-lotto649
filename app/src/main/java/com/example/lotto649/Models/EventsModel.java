package com.example.lotto649.Models;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.MyApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EventsModel extends AbstractModel {
    private AtomicReference<ArrayList<EventModel>> myEvents;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface EventFetchCallback {
        void onCallback(List<DocumentSnapshot> events);
    }

    public EventsModel() {
        myEvents = new AtomicReference<>(new ArrayList<>());
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

    public ArrayList<EventModel> getMyEvents() {
        EventsModel.fetchEventsByOrganizerId(new EventsModel.EventFetchCallback() {
            @Override
            public void onCallback(List<DocumentSnapshot> documents) {
                Log.e("Ohm", "onCallBack");

                myEvents.set(new ArrayList<>());
                for (DocumentSnapshot document : documents) {
                    String title = document.getString("title");
                    String facilityId = document.getString("facilityId");
                    String organizerId = document.getString("organizerId");
                    double cost = document.getDouble("cost") != null ? document.getDouble("cost") : 0.0;
                    String description = document.getString("description");
                    int numberOfSpots = document.getLong("numberOfSpots") != null ? document.getLong("numberOfSpots").intValue() : 0;
                    int numberOfMaxEntrants = document.getLong("numberOfMaxEntrants") != null ? document.getLong("numberOfMaxEntrants").intValue() : 0;
                    Date startDate = document.getDate("startDate");
                    Date endDate = document.getDate("endDate");
                    Object qrCode = document.get("qrCode");
                    String posterImage = document.getString("posterImage");
                    boolean geo = Boolean.TRUE.equals(document.get("geo"));
                    ArrayList<UserModel> waitingList = (ArrayList<UserModel>) document.get("waitingList");

                    Log.e("Ohm", title);

                    EventModel event = new EventModel(null, title, facilityId, cost, description, numberOfSpots, numberOfMaxEntrants, startDate, endDate, posterImage, geo, qrCode, waitingList, db);
                    event.setEventId(document.getId());
                    event.setOrganizerId(organizerId);
                    myEvents.get().add(event);
                    Log.e("Ohm", String.valueOf(myEvents.get().size()));
                }
            }
        }, db);

        Log.e("Ohm", "Model get events");
        return myEvents.get();
    }
}
