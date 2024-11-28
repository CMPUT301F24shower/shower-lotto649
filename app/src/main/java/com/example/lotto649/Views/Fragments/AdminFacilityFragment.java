/**
 * A fragment to display a given facility's information.
 * This is used by an admin user to manage a facility.
 * This fragment is reached through a list of facilities in the admin view.
 */
package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lotto649.FirestoreHelper;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A fragment to display a given facility's information.
 * This is used by an admin user to manage a facility.
 * This fragment is reached through a list of facilities in the admin view.
 */
public class AdminFacilityFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;
    private CollectionReference eventsRef;
    private String userDeviceId;
    FirestoreHelper firestoreHelper;

    /**
     * Public empty constructor for BrowseEventsFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public AdminFacilityFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     * This method inflates the layout defined in `fragment_browse_events.xml`.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the camera fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get info from bundle
        userDeviceId = getArguments().getString("facilityDeviceId");
        firestoreHelper = new FirestoreHelper();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_facility, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
        eventsRef = db.collection("events");

        TextView name = view.findViewById(R.id.admin_facility_name);
        TextView address  = view.findViewById(R.id.admin_facility_address);
        Button deleteButton = view.findViewById(R.id.admin_delete_facility);
        ExtendedFloatingActionButton backButton = view.findViewById(R.id.back_button);

        facilitiesRef.document(userDeviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            String nameText = doc.getString("facility");
                            String addressText = doc.getString("address");
                            name.setText(nameText);
                            address.setText(addressText);
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO this might need popFragment instead to make sure andorid back button doesnt mess stuff up
                // Havent tested yet tho
                MyApp.getInstance().replaceFragment(new BrowseFacilitiesFragment());
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestoreHelper.deleteEventsFromFacility(userDeviceId);

                facilitiesRef
                        .document(userDeviceId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                BrowseFacilitiesFragment frag = new BrowseFacilitiesFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.flFragment, frag, null)
                                        .addToBackStack(null)
                                        .commit();
                            //     add success log
                            }
                        });
            }
        });
        return view;
    }
}
