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

import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminFacilityFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;
    private String userDeviceId;

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

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_facility, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");

        TextView name = view.findViewById(R.id.admin_facility_name);
        TextView address  = view.findViewById(R.id.admin_facility_address);
        Button deleteButton = view.findViewById(R.id.admin_delete_facility);

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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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