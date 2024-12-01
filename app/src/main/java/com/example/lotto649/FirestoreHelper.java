package com.example.lotto649;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.CountDownLatch;

public class FirestoreHelper {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private CollectionReference facilitiesRef;
    private CollectionReference signUpRef;
    FirebaseStorage storageRef;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");
        facilitiesRef = db.collection("facilities");
        signUpRef = db.collection("signUps");
        storageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
    }


    public void deleteFacility(String facilityId) {
        // TODO make sure this works if the user doesnt have a facility
        facilitiesRef.document(facilityId).delete();
        deleteEventsFromFacility(facilityId);

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

    public void markSignupsAsDeleted(String eventId) {
        Query usersSignedUp = signUpRef.whereEqualTo("eventId", eventId);
        usersSignedUp.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    doc.getReference().update("eventDeleted", true);
                }
            }
        });
    }

    public int getWaitlistSize(String eventId) {
        CountDownLatch latch = new CountDownLatch(1);
        final Integer[] result = {null}; // Use an array to hold the result since variables need to be effectively final

        db.collection("signUps")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result[0] = task.getResult().size(); // Store the result
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        result[0] = 0; // Default value for failure
                    }
                    latch.countDown(); // Signal that the operation is complete
                });

        try {
            latch.await(); // Wait until the Firestore query completes
        } catch (InterruptedException e) {
            Log.e("Firestore", "Interrupted while waiting for Firestore result", e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        return result[0];
    }

    public int getWinnersSize(String eventId) {
        CountDownLatch latch = new CountDownLatch(1);
        final Integer[] result = {null}; // Use an array to hold the result since variables need to be effectively final

        db.collection("winners")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result[0] = task.getResult().size(); // Store the result
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        result[0] = 0; // Default value for failure
                    }
                    latch.countDown(); // Signal that the operation is complete
                });

        try {
            latch.await(); // Wait until the Firestore query completes
        } catch (InterruptedException e) {
            Log.e("Firestore", "Interrupted while waiting for Firestore result", e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        return result[0];
    }

    public int getEnrolledSize(String eventId) {
        CountDownLatch latch = new CountDownLatch(1);
        final Integer[] result = {null}; // Use an array to hold the result since variables need to be effectively final

        db.collection("enrolled")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result[0] = task.getResult().size(); // Store the result
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        result[0] = 0; // Default value for failure
                    }
                    latch.countDown(); // Signal that the operation is complete
                });

        try {
            latch.await(); // Wait until the Firestore query completes
        } catch (InterruptedException e) {
            Log.e("Firestore", "Interrupted while waiting for Firestore result", e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        return result[0];
    }

    public int getNotSelectedSize(String eventId) {
        CountDownLatch latch = new CountDownLatch(1);
        final Integer[] result = {null}; // Use an array to hold the result since variables need to be effectively final

        db.collection("notSelected")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result[0] = task.getResult().size(); // Store the result
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        result[0] = 0; // Default value for failure
                    }
                    latch.countDown(); // Signal that the operation is complete
                });

        try {
            latch.await(); // Wait until the Firestore query completes
        } catch (InterruptedException e) {
            Log.e("Firestore", "Interrupted while waiting for Firestore result", e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        return result[0];
    }


}
