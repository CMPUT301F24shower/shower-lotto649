package com.example.lotto649;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
}
