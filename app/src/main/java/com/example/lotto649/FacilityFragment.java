/**
 * Code used from the following source for menu bar
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 */

package com.example.lotto649;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacilityFragment extends Fragment {
    private FacilityView facilityView;
    private FacilityModel facility;
    private FacilityController facilityController;
    private String deviceId;
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;
    private TextInputEditText nameInput;
    private TextInputEditText addressInput;
    private Button cancel;
    private Button save;
    private String initialFacilityNameInput;
    private String initialAddressInput;


    public FacilityFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility, container, false);

        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize UI components
        nameInput = view.findViewById(R.id.inputFacilityName);
        addressInput = view.findViewById(R.id.inputAddress);
        cancel = view.findViewById(R.id.facility_cancel_button);
        save = view.findViewById(R.id.facility_save_button);

        // get initial values of input text
        initialFacilityNameInput = nameInput.getText().toString();
        initialAddressInput = addressInput.getText().toString();

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
        facility = new FacilityModel(deviceId);

        // get facility information from Firestore, if it exists
        // if not, do nothing
        facilitiesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        // if (doc.getString("deviceId").equals(deviceId)) {
                        if (Objects.equals(doc.getString("deviceId"), deviceId)) {
                            String nameText = doc.getString("facility");
                            String addressText = doc.getString("address");
                            facility.setFacilityName(nameText);
                            facility.setAddress(addressText);
                            Log.d("Firestore", String.format("deviceId %s got name=%s, address=%s ", deviceId, nameText, addressText));
                        }
                    }
                }
            }
        });

        // Initialize MVC components
        facilityView = new FacilityView(facility, this);
        facilityController = new FacilityController(facility);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: return to previous states, don't just clear
                nameInput.setText(initialFacilityNameInput);
                addressInput.setText(initialAddressInput);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset error message
                nameInput.setError(null);
                String facilityName = nameInput.getText().toString();
                String address = addressInput.getText().toString();
                if (facilityName.isEmpty()) {
                    nameInput.setError("Please enter a facility name");
                    return;
                }
                facilityController.updateFacilityName(facilityName);
                facilityController.updateAddress(address);
                facilityController.saveToFirestore();
                initialFacilityNameInput = facilityName;
                initialAddressInput = address;
            }
        });

        return view;
    }

    /**
     * Updates the UI with the facility details.
     *
     * @param facility The facility model containing the details to be displayed
     */
    public void showFacilityDetails(FacilityModel facility) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nameInput.setText(facility.getFacilityName());
                addressInput.setText(facility.getAddress());
            }
        });
    }
}
