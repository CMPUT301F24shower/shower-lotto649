/**
 * FacilityController serves as the controller for the Facility class.
 */
package com.example.lotto649.Controllers;

import com.example.lotto649.AbstractClasses.AbstractController;
import com.example.lotto649.Models.FacilityModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

/**
 * FacilityController is part of the Model-View-Controller (MVC) architecture.
 * It serves as the controller for managing interactions between the facility model
 * and the views or other components in the application.
 * This controller allows for updates to the `FacilityModel` and ensures the model's state is
 * synchronized with any changes made by the user.
 */
public class FacilityController extends AbstractController {
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;


    /**
     * Constructor for the FacilityController class. Takes in a FacilityModel to change.
     * Also takes a Firestore instance and gets the facilities collection.
     *
     * @param facility the facility model to change
     * @param db the Firestore db instance
     */
    public FacilityController(FacilityModel facility, FirebaseFirestore db) {
        super(facility);
        this.db = db;
        facilitiesRef = db.collection("facilities");
    }

    /**
     * Returns the FacilityModel that the controller changes.
     *
     * @return the facility model
     */
    @Override
    public FacilityModel getModel() {
        return (FacilityModel) super.getModel();
    }

    /**
     * Calls the model's setFacilityName function to set the name to a new given name
     *
     * @param name the new name to set the facility's name to
     */
    public void updateFacilityName(String name) {
        getModel().setFacilityName(name);
    }

    /**
     * Calls the model's setAddress function to set the address to a new given address
     *
     * @param address the new address to set the facility's address to
     */
    public void updateAddress(String address) {
        getModel().setAddress(address);
    }

    /**
     * Saves the facility information to Firestore for data retention
     */
    public void saveToFirestore() {
        HashMap<String, String> data = new HashMap<>();
        data.put("facility", getModel().getFacilityName());
        data.put("address", getModel().getAddress());
        facilitiesRef.document(getModel().getDeviceId())
                .set(data, SetOptions.merge());
                // TODO: add in success listener (currently breaks testing)
                // .addOnSuccessListener(aVoid -> {
                    // Log.d("Firestore", "Facility successfully written!");
                // });
    }
}