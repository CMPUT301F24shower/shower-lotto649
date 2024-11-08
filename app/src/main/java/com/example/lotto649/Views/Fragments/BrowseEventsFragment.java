/**
 * BrowseEventsFragment class represents a fragment for the admin to browse all events in the application.
 * <p>
 * This fragment shows a list view of every event, selecting the event will show its full details and allow for it to be deleted.
 * This page is only accessible to users with 'admin' status
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 */

package com.example.lotto649.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseEventsArrayAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class BrowseEventsFragment extends Fragment {
    private ArrayList<String> eventIdList;
    private ArrayList<EventModel> dataList;
    private ListView browseEventsList;
    private BrowseEventsArrayAdapter eventsAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;

    /**
     * Public empty constructor for BrowseFacilitiesFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public BrowseEventsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_browse_events, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        // fill dataList from Firestore
        dataList = new ArrayList<EventModel>();
        eventIdList = new ArrayList<String>();

        browseEventsList = view.findViewById(R.id.browse_events_list);
        eventsAdapter = new BrowseEventsArrayAdapter(view.getContext(), dataList);
        browseEventsList.setAdapter(eventsAdapter);

        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String eventId = doc.getId();
                        String title = doc.getString("title");
                        String facilityId = doc.getString("facilityId");
                        String description = doc.getString("description");
                        int numberOfSpots = ((Long) doc.get("numberOfSpots")).intValue();
                        int numberOfMaxEntrants = ((Long) doc.get("numberOfMaxEntrants")).intValue();
                        Date startDate = doc.getDate("startDate");
                        Date endDate = doc.getDate("endDate");
                        boolean geo = doc.getBoolean("geo");
                        dataList.add(new EventModel(getContext(), title, facilityId, 0, description, numberOfSpots, numberOfMaxEntrants, startDate, endDate, geo, null));
                        eventIdList.add(eventId);
                    }
                    eventsAdapter.notifyDataSetChanged();
                }
            }
        });

        browseEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenEvent = eventIdList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", chosenEvent);
                AdminEventFragment frag = new AdminEventFragment();
                frag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, frag, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}