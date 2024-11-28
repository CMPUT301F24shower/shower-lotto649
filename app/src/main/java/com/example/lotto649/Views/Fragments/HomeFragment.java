/**
 * HomeFragment is a fragment class representing the home screen of the application.
 * <p>
 * This class inflates the layout for the home fragment and displays its content
 * when the fragment is created.
 * </p>
 * <p>
 * Code adapted from the following source for implementing a bottom navigation bar:
 * <a href="https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/">GeeksforGeeks: Bottom Navigation Bar in Android</a>
 * </p>
 */
package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lotto649.Models.HomePageModel;
import com.example.lotto649.Views.ArrayAdapters.EventArrayAdapter;
import com.example.lotto649.Controllers.EventsController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private EventsController eventsController;
    private ExtendedFloatingActionButton addButton;
    private EventArrayAdapter eventAdapter;
    private HomePageModel events;

    /**
     * Required empty public constructor.
     */
    public HomeFragment() {
        // No-arg constructor
    }

    /**
     * Called to inflate the fragment's layout when it is created.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        events = new HomePageModel();
        eventsController = new EventsController(events);
        addButton = view.findViewById(R.id.addButton);

        // Check that the user has created an account and a facility, if they haven't hide the create event button
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        DocumentReference facilityRef = FirebaseFirestore.getInstance().collection("facilities").document(deviceId);

        userRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot userDoc = task.getResult();
            boolean userExists = userDoc != null && userDoc.exists();
            if (!userExists) {
                addButton.setVisibility(View.GONE);
            }
        }).addOnFailureListener(task -> addButton.setVisibility(View.GONE));;
        facilityRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot facilityDoc = task.getResult();
            boolean facilityExists = facilityDoc != null && facilityDoc.exists();
            if (!facilityExists) {
                addButton.setVisibility(View.GONE);
            }
        }).addOnFailureListener(task -> addButton.setVisibility(View.GONE));

        ListView eventsList = view.findViewById(R.id.event_contents);

        // Use the asynchronous method and handle data once it's ready
        eventsController.getMyEvents(new HomePageModel.MyEventsCallback() {
            @Override
            public void onEventsFetched(ArrayList<EventModel> events) {
                Log.w("Ohm", "Events fetched: " + events.size());

                // TODO test that this doesnt introduce any bugs - so far so good
                if (isAdded()) {
                    // Initialize and set the adapter with fetched events
                    eventAdapter = new EventArrayAdapter(requireContext(), events, new EventArrayAdapter.EventArrayAdapterListener() {
                        @Override
                        public void onEventsWaitListChanged() {
                            // Handle waitlist changes if needed
                        }
                    });
                    eventsList.setAdapter(eventAdapter);
                }
            }
        });

        addButton.setOnClickListener(v -> eventsController.addEvent());

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventModel event = (EventModel) adapterView.getItemAtPosition(i);
                eventsController.editEvent(event);
            }
        });

        return view;
    }
}
