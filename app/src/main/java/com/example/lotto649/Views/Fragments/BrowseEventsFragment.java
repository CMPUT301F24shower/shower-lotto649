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

import com.example.lotto649.EventState;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseEventsArrayAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
public class BrowseEventsFragment extends Fragment {
    private ArrayList<String> eventIdList;
    private ArrayList<EventModel> dataList;
    private ListView browseEventsList;
    private BrowseEventsArrayAdapter eventsAdapter;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private ExtendedFloatingActionButton backButton;

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

        backButton = view.findViewById(R.id.back_button);
        browseEventsList = view.findViewById(R.id.browse_events_list);
        eventsAdapter = new BrowseEventsArrayAdapter(view.getContext(), dataList, getViewLifecycleOwner());
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
                        String facilityId = doc.getString("organizerId");
                        String description = doc.getString("description");
                        Long numSpotsLong = ((Long) doc.get("numberOfSpots"));
                        int numberOfSpots = 0;
                        if (numSpotsLong != null)
                            numberOfSpots = numSpotsLong.intValue();
                        Long numMaxLong = ((Long) doc.get("numberOfMaxEntrants"));
                        int numberOfMaxEntrants = 0;
                        if (numMaxLong != null)
                            numberOfMaxEntrants = numMaxLong.intValue();
                        Date startDate = doc.getDate("startDate");
                        Date endDate = doc.getDate("endDate");
                        String posterImageUriString = doc.getString("posterImage");
                        String qrCodeHash = doc.getString("qrCode");
                        boolean geo = Boolean.TRUE.equals(doc.getBoolean("geo"));
                        String stateStr = doc.getString("state");
                        EventState state = EventState.OPEN;
                        if (Objects.equals(stateStr, "WAITING")) {
                            state = EventState.WAITING;
                        } else if (Objects.equals(stateStr, "CLOSED")) {
                            state = EventState.CLOSED;
                        }
                        EventModel event =  new EventModel(title, description, numberOfSpots,
                                numberOfMaxEntrants, startDate, endDate, posterImageUriString, geo, qrCodeHash,
                                state, null);
                        event.setEventId(eventId);
                        dataList.add(event);
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
                MyApp.getInstance().addFragmentToStack(frag);
            }
        });

        backButton.setOnClickListener(new ExtendedFloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

        return view;
    }
}