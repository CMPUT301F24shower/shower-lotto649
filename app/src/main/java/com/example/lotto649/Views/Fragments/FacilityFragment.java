/**
 * FacilityFragment works with the Facility model, controller, and view to integrate them
 * into a working application.
 */
package com.example.lotto649.Views.Fragments;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Controllers.FacilityController;
import com.example.lotto649.Models.FacilityModel;
import com.example.lotto649.R;
import com.example.lotto649.Views.FacilityView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * FacilityFragment class manages the facility details of the user in the application.
 * This fragment is responsible for displaying and updating the user's facility information
 * using a form. The facility information is retrieved from and saved to Firebase Firestore.
 * The class also manages the interactions between the view,
 * model, and controller in a Model-View-Controller (MVC) pattern.
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 */
public class FacilityFragment extends Fragment {
    private FacilityView facilityView;
    private FacilityModel facility;
    private FacilityController facilityController;
    private String deviceId;
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;
    private TextInputEditText nameInput;
    private TextInputEditText addressInput;
    private Button save;
    private String initialFacilityNameInput;
    private String initialAddressInput;

    /**
     * Empty constructor that fragment needs.
     */
    public FacilityFragment(){
        // require a empty public constructor
    }

    /**
     * Called to create the view hierarchy for this fragment.
     * This method inflates the layout for the facility fragment and initializes the UI components.
     *
     * @param inflater  LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the facility fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility, container, false);

        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize UI components
        nameInput = view.findViewById(R.id.inputFacilityName);
        addressInput = view.findViewById(R.id.inputAddress);
        save = view.findViewById(R.id.facility_save_button);

        // add text watchers to inputs
        nameInput.addTextChangedListener(facilityNameWatcher);
        addressInput.addTextChangedListener(addressWatcher);

        // get initial values of input text
        initialFacilityNameInput = nameInput.getEditableText().toString();
        initialAddressInput = addressInput.getEditableText().toString();

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
        facility = new FacilityModel(deviceId);

        // get facility information from Firestore, if it exists
        // if not, do nothing
        facilitiesRef.document(deviceId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String nameText = doc.getString("facility");
                        String addressText = doc.getString("address");
                        facility.setFacilityName(nameText);
                        facility.setAddress(addressText);
                        initialFacilityNameInput = nameText;
                        initialAddressInput = addressText;
                        SetSaveButtonColor(true);
                        Log.d("Firestore", String.format("deviceId %s got name=%s, address=%s ", deviceId, nameText, addressText));
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

        // Initialize MVC components
        facilityView = new FacilityView(facility, this);
        facilityController = new FacilityController(facility, db);

        // Click save to save inputted information
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset error message
                nameInput.setError(null);
                String facilityName = nameInput.getEditableText().toString();
                String address = addressInput.getEditableText().toString();
                // If no facility name given, error
                if (facilityName.isEmpty()) {
                    nameInput.setError("Please enter a facility name");
                    return;
                }
                facilityController.updateFacilityName(facilityName);
                facilityController.updateAddress(address);
                facilityController.saveToFirestore();
                initialFacilityNameInput = facilityName;
                initialAddressInput = address;
                SetSaveButtonColor(true);
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
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nameInput.setText(facility.getFacilityName());
                addressInput.setText(facility.getAddress());
            }
        });
    }

    /**
     * Finds if the information in the EditText components is the same as in Firestore or not.
     *
     * @return true if either of the facility name or address changed from the saved version
     */
    private boolean DidInfoRemainConstant() {
        return Objects.equals(nameInput.getEditableText().toString(), initialFacilityNameInput) && Objects.equals(addressInput.getEditableText().toString(), initialAddressInput);
    }

    /**
     * Sets the save button's colour depending on whether information on the page changed.
     *
     * @param isEqual if the facility information inputted is the same as in Firestore
     */
    private void SetSaveButtonColor(boolean isEqual) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RippleDrawable saveBackgroundColor = (RippleDrawable) save.getBackground();
            if (isEqual) {
                if (saveBackgroundColor.getEffectColor().getDefaultColor() != getResources().getColor(R.color.lightSurfaceContainerHigh, null)) {
                    save.setBackgroundColor(getResources().getColor(R.color.lightSurfaceContainerHigh, null));
                    save.setTextColor(getResources().getColor(R.color.lightPrimary, null));
                }
            }
            else {
                if (saveBackgroundColor.getEffectColor().getDefaultColor() != getResources().getColor(R.color.lightOnSurface, null)) {
                    save.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    save.setTextColor(getResources().getColor(R.color.black, null));
                }
            }
        }
    }

    /**
     * Watches the facility name EditText for changes, and calls to possibly change the save button colour
     */
    private TextWatcher facilityNameWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonColor(DidInfoRemainConstant());
        }
    };

    /**
     * Watches the address EditText for changes, and calls to possibly change the save button colour
     */
    private TextWatcher addressWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonColor(DidInfoRemainConstant());
        }
    };
}