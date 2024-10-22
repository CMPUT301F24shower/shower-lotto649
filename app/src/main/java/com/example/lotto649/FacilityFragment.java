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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class FacilityFragment extends Fragment {
    String deviceId;
    FirebaseFirestore db;
    CollectionReference facilitiesRef;


    public FacilityFragment(){
        // require a empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility, container, false);
        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        TextInputEditText nameInput = view.findViewById(R.id.inputFacilityName);
        TextInputEditText addressInput = view.findViewById(R.id.inputAddress);
        Button cancel = view.findViewById(R.id.facility_cancel_button);
        Button save = view.findViewById(R.id.facility_save_button);

        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");

        facilitiesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (doc.getString("deviceId").equals(deviceId)) {
                            String nameText = doc.getString("facility");
                            String addressText = doc.getString("address");
                            nameInput.setText(nameText);
                            addressInput.setText(addressText);
                            Log.d("Firestore", String.format("deviceId %s got name=%s, address=%s ", deviceId, nameText, addressText));
                        }
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameInput.getText().clear();
                addressInput.getText().clear();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String facilityName = nameInput.getText().toString();
                String address = addressInput.getText().toString();
                HashMap<String, String> data = new HashMap<>();
                data.put("facility", facilityName);
                data.put("address", address);
                data.put("deviceId", deviceId);
                facilitiesRef.document(deviceId)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firestore", "Facility successfully written!");
                            }
                        });
            }
        });

        return view;
    }
}
