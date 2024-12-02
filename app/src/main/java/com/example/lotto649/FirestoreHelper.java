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

/**
 * Helper class for doing common operations with our Firestore databse.
 *
 * <p>This class includes methods to get sizes of certain collections, and
 * deleting entries in the databse.</p>
 * This code was adapted from the firebase docs:
 * https://firebase.google.com/docs/storage/android/upload-files
 */
public class FirestoreHelper {
    private static FirestoreHelper instance;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference facilitiesRef;
    private CollectionReference signUpRef;
    private FirebaseStorage storageRef;
    private Context context;
    boolean waitingForWaitList;

    // Private constructor to prevent instantiation

    /**
     * Constructor for the singleton helper object
     */
    private FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");
        facilitiesRef = db.collection("facilities");
        signUpRef = db.collection("signUps");
        storageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        waitingForWaitList = false;
    }

    // Get the singleton instance

    /**
     * gets the current instance of the FirestoreHelper
     *
     * @return the instance of the FirestoreHelper
     */
    public static synchronized FirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreHelper();
        }
        return instance;
    }

    /**
     * Deletes a facility from db, and call to delete events hosted at said facility
     *
     * @param facilityId the id of the facility to delete
     */
    public void deleteFacility(String facilityId) {
        // TODO make sure this works if the user doesnt have a facility
        facilitiesRef.document(facilityId).delete();
        deleteEventsFromFacility(facilityId);
    }

    // Initialize with context (call this from your Activity)
    /**
     * Initializes the singleton using a given context
     *
     * @param context the context of the Activity
     */
    public void init(Context context) {
        if (this.context == null) {
            this.context = context.getApplicationContext(); // Ensure the application context is used
        }
    }

    /**
     * Deletes all events at a given facility.
     *
     * @param facilityOwner the id of the organizer of the facility
     */
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

    /**
     * Deletes a poster from Firebase Storage
     *
     * @param posterString the URL of the poster to delete
     */
    public void deletePosterFromEvent(String posterString) {
        if (!posterString.isEmpty()) {
            StorageReference imageRef = storageRef.getReferenceFromUrl(posterString);
            imageRef.delete();
        }
    }

    // TODO use custom notification code here instead
    /**
     * Marks entries in the signUps collection as deleted, and then deletes them
     *
     * @param eventId the event for which we want signUps to be deleted
     */
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

    /**
     * Gets the current size of the waiting list for the given event
     *
     * @param eventId the id of the given event
     * @param data a MutableLiveData object to be used by caller
     */
    public void getWaitlistSize(String eventId, MutableLiveData<Integer> data) {
        db.collection("signUps")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        data.setValue(task.getResult().size()); // Store the result
                    } else {
                        data.setValue(0);
                    }
                });
    }

    /**
     * Gets the current size of the winners list for the given event
     *
     * @param eventId the id of the given event
     * @param data a MutableLiveData object to be used by caller
     */
    public void getWinnersSize(String eventId, MutableLiveData<Integer> data) {
        db.collection("winners")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("JASON REDRAW", "get winners size success");
                        data.setValue(task.getResult().size()); // Store the result
                    } else {
                        Log.e("JASON REDRAW", "get winners size fail - set to 0");
                        data.setValue(0);
                    }
                });
    }

    /**
     * Gets the current size of the enrolled list for the given event
     *
     * @param eventId the id of the given event
     * @param data a MutableLiveData object to be used by caller
     */
    public void getEnrolledSize(String eventId, MutableLiveData<Integer> data) {
        db.collection("enrolled")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        data.setValue(task.getResult().size()); // Store the result
                    } else {
                        data.setValue(0);
                    }
                });
    }

    /**
     * Gets the current size of the not selected list for the given event
     *
     * @param eventId the id of the given event
     * @param data a MutableLiveData object to be used by caller
     */
    public void getNotSelectedSize(String eventId, MutableLiveData<Integer> data) {
        db.collection("notSelected")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        data.setValue(task.getResult().size()); // Store the result
                    } else {
                        data.setValue(0);
                    }
                });
    }


}
