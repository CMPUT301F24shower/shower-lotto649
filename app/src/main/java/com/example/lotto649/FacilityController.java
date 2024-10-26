package com.example.lotto649;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class FacilityController extends AbstractController {
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;

    public FacilityController(FacilityModel facility) {
        super(facility);
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
    }

    @Override
    public FacilityModel getModel() {
        return (FacilityModel) super.getModel();
    }

    public void updateFacilityName(String name) {
        getModel().setFacilityName(name);
    }

    public void updateAddress(String address) {
        getModel().setAddress(address);
    }

    /**
     * Save the user to firestore for data retention
     */
    public void saveToFirestore() {
        HashMap<String, String> data = new HashMap<>();
        data.put("facility", getModel().getFacilityName());
        data.put("address", getModel().getAddress());
        facilitiesRef.document(getModel().getDeviceId())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Facility successfully written!");
                    }
                });
    }
}