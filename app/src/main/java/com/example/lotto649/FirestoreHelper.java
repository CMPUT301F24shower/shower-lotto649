package com.example.lotto649;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FirestoreHelper {
    private static FirestoreHelper instance;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference facilitiesRef;
    private CollectionReference signUpRef;
    private FirebaseStorage storageRef;
    private MutableLiveData<Integer> currWaitlistSize;
    private MutableLiveData<Integer> currWinnersSize;
    private MutableLiveData<Integer> currEnrolledSize;
    private MutableLiveData<Integer> currNotSelectedSize;
    private Context context;
    boolean waitingForWaitList;
    String waitlistEventId;
    boolean spinlock;

    // Private constructor to prevent instantiation
    private FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");
        facilitiesRef = db.collection("facilities");
        signUpRef = db.collection("signUps");
        storageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        currWaitlistSize = new MutableLiveData<Integer>(0);
        currWinnersSize = new MutableLiveData<Integer>(0);
        currEnrolledSize = new MutableLiveData<Integer>(0);
        currNotSelectedSize = new MutableLiveData<Integer>(0);
        waitingForWaitList = false;
    }

    // Get the singleton instance
    public static synchronized FirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreHelper();
        }
        return instance;
    }

    public void deleteFacility(String facilityId) {
        // TODO make sure this works if the user doesnt have a facility
        facilitiesRef.document(facilityId).delete();
        deleteEventsFromFacility(facilityId);
    }

    // Initialize with context (call this from your Activity)
    public void init(Context context) {
        if (this.context == null) {
            this.context = context.getApplicationContext(); // Ensure the application context is used
        }
    }

    public MutableLiveData<Integer> getCurrWaitlistSize() {
        return currWaitlistSize;
    }

    public MutableLiveData<Integer> getCurrWinnersSize() {
        return currWinnersSize;
    }

    public MutableLiveData<Integer> getCurrEnrolledSize() {
        return currEnrolledSize;
    }

    public MutableLiveData<Integer> getCurrNotSelectedSize() {
        return currNotSelectedSize;
    }

    public void deleteEventsFromFacility(String facilityOwner) {
        Query eventsInFacility = eventsRef.whereEqualTo("organizerId", facilityOwner);

        eventsInFacility.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        markSignupsAsDeleted(document.getId());
                        deletePosterFromEvent(document.getString("posterImage"));
                        document.getReference().delete();
                    }
                }
            }
        });
    }

    public void deletePosterFromEvent(String posterString) {
        if (!posterString.isEmpty()) {
            StorageReference imageRef = storageRef.getReferenceFromUrl(posterString);
            imageRef.delete();
        }
    }

    // TODO use custom notification code here instead
    public void markSignupsAsDeleted(String eventId) {
        db.collection("signUps").whereEqualTo("eventId", eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Map<String, Object> data = doc.getData();
                    data.put("hasSeenNoti", false);
                    db.collection("cancelled").document(doc.getId()).set(data);
                    db.collection("signUps")
                            .document(doc.getId())
                            .delete();
                }
            }
        });
        db.collection("winners").whereEqualTo("eventId", eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Map<String, Object> data = doc.getData();
                    data.put("hasSeenNoti", false);
                    db.collection("cancelled").document(doc.getId()).set(data);
                    db.collection("winners")
                            .document(doc.getId())
                            .delete();
                }
            }
        });
        db.collection("enrolled").whereEqualTo("eventId", eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Map<String, Object> data = doc.getData();
                    data.put("hasSeenNoti", false);
                    db.collection("cancelled").document(doc.getId()).set(data);
                    db.collection("enrolled")
                            .document(doc.getId())
                            .delete();
                }
            }
        });
        db.collection("notSelected").whereEqualTo("eventId", eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Map<String, Object> data = doc.getData();
                    data.put("hasSeenNoti", false);
                    db.collection("cancelled").document(doc.getId()).set(data);
                    db.collection("notSelected")
                            .document(doc.getId())
                            .delete();
                }
            }
        });
    }

    public void getWaitlistSize(String eventId) {
        Log.e("JASON LATCH", "start");
        waitingForWaitList = true;
        waitlistEventId = eventId;

        db.collection("signUps")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currWaitlistSize.setValue(task.getResult().size()); // Store the result
                    }
                    waitingForWaitList = false;
                });
    }

    public void getWinnersSize(String eventId) {
        db.collection("winners")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currWinnersSize.setValue(task.getResult().size()); // Store the result
                    }
                });
    }

    public void getEnrolledSize(String eventId) {
        db.collection("enrolled")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currEnrolledSize.setValue(task.getResult().size()); // Store the result
                    }
                });
    }

    public void getNotSelectedSize(String eventId) {
        db.collection("notSelected")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currNotSelectedSize.setValue(task.getResult().size()); // Store the result
                    }
                });
    }


}
