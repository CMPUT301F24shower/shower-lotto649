/**
 * BrowseFacilitiesFragment class represents a fragment for the admin to browse all facilities in the application.
 * <p>
 * This fragment shows a list view of every facility, selecting the event will show its full details and allow for it to be deleted.
 * This page is only accessible to users with 'admin' status
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 */
package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Models.FacilityModel;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseFacilitiesArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * BrowseFacilitiesFragment class represents a fragment for the admin to browse all facilities in the application.
 * <p>
 * This fragment shows a list view of every facility, selecting the event will show its full details and allow for it to be deleted.
 * This page is only accessible to users with 'admin' status
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 */
public class BrowseFacilitiesFragment extends Fragment {
    private ArrayList<FacilityModel> dataList;
    private ListView browseFacilityList;
    private BrowseFacilitiesArrayAdapter facilitiesAdapter;
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;

    /**
     * Public empty constructor for BrowseFacilitiesFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public BrowseFacilitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_facilities, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");

        // fill dataList from Firestore
        dataList = new ArrayList<FacilityModel>();

        browseFacilityList = view.findViewById(R.id.browse_facilities_list);
        facilitiesAdapter = new BrowseFacilitiesArrayAdapter(view.getContext(), dataList);
        browseFacilityList.setAdapter(facilitiesAdapter);

        facilitiesRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String deviceIdText = doc.getId();
                        String nameText = doc.getString("facility");
                        String addressText = doc.getString("address");
                        dataList.add(new FacilityModel(deviceIdText, nameText, addressText));
                    }
                    facilitiesAdapter.notifyDataSetChanged();
                }
            }
        });

        browseFacilityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FacilityModel chosenFacility = (FacilityModel) browseFacilityList.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putString("facilityDeviceId", chosenFacility.getDeviceId());
                AdminFacilityFragment frag = new AdminFacilityFragment();
                frag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, frag, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // TODO needs a back button

        return view;
    }
}
