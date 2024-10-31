package com.example.lotto649.Models;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public interface AdminInterface {
    //
    // Methods to remove inappropriate items
    void remove(FirebaseFirestore db, EventModel event);
    void removeImage(FirebaseFirestore db, EventModel event);
    void remove(FirebaseFirestore db, UserModel user);
    void removeImage(FirebaseFirestore db, UserModel user);
    void remove(FirebaseFirestore db, FacilityModel facility);
    void removeImage(FirebaseFirestore db, FacilityModel facility);
    void remove(QrCodeModel qrcode);

    // Methods to browse items
    ArrayList<EventModel> browseEvents(FirebaseFirestore db);
    ArrayList<UserModel> browseProfiles(FirebaseFirestore db);
    ArrayList<Object> browseImages(FirebaseFirestore db);//needs edits
}
