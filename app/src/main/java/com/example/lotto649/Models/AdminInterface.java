package com.example.lotto649.Models;

import com.google.firebase.firestore.FirebaseFirestore;

public interface AdminInterface {
    void remove(FirebaseFirestore db, EventModel event);
    void removeImage(FirebaseFirestore db, EventModel event);
    void remove(FirebaseFirestore db, UserModel user);
    void removeImage(FirebaseFirestore db, UserModel user);
    void remove(FirebaseFirestore db, FacilityModel facility);
    void removeImage(FirebaseFirestore db, FacilityModel facility);
    void remove(QrCodeModel qrcode);
}
