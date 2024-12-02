package com.example.lotto649.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.lotto649.Controllers.EventsController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.HomePageModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseEventsArrayAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * HomeFragment class displays the user's events and allows the user to add new events.
 * <p>
 * This fragment fetches and displays the user's events from Firestore and allows interaction with them.
 * If the user has no events, a message is shown. The fragment also conditionally shows the "add event" button
 * based on the user's account and facility status.
 * </p>
 */
public class HomeFragment extends Fragment {
    private EventsController eventsController;
    private ExtendedFloatingActionButton addButton;
    private BrowseEventsArrayAdapter eventAdapter;
    private HomePageModel events;

    /**
     * Required empty public constructor.
     */
    public HomeFragment() {
        // No-arg constructor
    }

    /**
     * Initializes the fragment's view hierarchy and fetches the necessary data.
     * <p>
     * This method inflates the layout, checks the user's account and facility status,
     * initializes UI components, and sets up observers for the "add event" button visibility.
     * It also handles the fetching and displaying of the user's events from Firestore.
     * </p>
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
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

        MutableLiveData<Boolean> noAccepts = new MutableLiveData<>(Boolean.TRUE);
        ConstraintLayout layout = view.findViewById(R.id.home_view);
        Context context = getContext();

        TextView textView;
        if (context != null) {
            textView = new TextView(context);
        } else {
            textView = null;
        }
        noAccepts.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            /**
             * Observes changes in the availability of events and updates the UI accordingly.
             * <p>
             * If the user has no events, a TextView is added to the layout to inform the user that they must create
             * an account and a facility to add events. If events are available, the TextView is hidden.
             * </p>
             */
            @Override
            public void onChanged(Boolean aBoolean) {
                if (Objects.equals(aBoolean, Boolean.TRUE)) {
                    if (textView != null) {
                        textView.setId(View.generateViewId()); // Generate an ID for the TextView
                        textView.setText("You do not have any events. If you cannot see an add button you must create an account and a facility");
                        textView.setTextSize(24);
                        textView.setGravity(Gravity.CENTER);
                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); // Update with your color

                        // Set layout params for the TextView to match parent constraints
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT
                        );

                        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

                        textView.setLayoutParams(params);

                        // Add the TextView to the layout
                        layout.addView(textView);
                    }
                } else {
                    if (textView != null) {
                        textView.setVisibility(View.GONE);
                    }
                }
            }
        });

        userRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot userDoc = task.getResult();
            boolean userExists = userDoc != null && userDoc.exists();
            if (!userExists) {
                addButton.setVisibility(View.GONE);
            }
        }).addOnFailureListener(task -> addButton.setVisibility(View.GONE));
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
            /**
             * Fetches the user's events and displays them in a ListView.
             * <p>
             * Uses the `EventsController` to retrieve the user's events asynchronously and populate a `ListView`.
             * The events are displayed using a custom adapter (`BrowseEventsArrayAdapter`), and the visibility
             * of a "no events" message is updated accordingly.
             * </p>
             */
            @Override
            public void onEventsFetched(ArrayList<EventModel> events) {
                Log.w("Ohm", "Events fetched: " + events.size());

                if (isAdded()) {
                    // Initialize and set the adapter with fetched events
                    if (!events.isEmpty()) {
                        noAccepts.setValue(Boolean.FALSE);
                    }
                    eventAdapter = new BrowseEventsArrayAdapter(requireContext(), events, getViewLifecycleOwner());
                    eventsList.setAdapter(eventAdapter);
                }
            }
        });

        addButton.setOnClickListener(v -> eventsController.addEvent());

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Handles interaction with the events list, including viewing or editing events.
             * <p>
             * If the user clicks on an event in the list, the corresponding fragment is opened based on whether
             * the event is organized by the current user or not.
             * </p>
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventModel event = (EventModel) adapterView.getItemAtPosition(i);

                String eventId = event.getEventId();
                String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                String organizerId = event.getOrganizerId();
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", eventId);

                if (Objects.equals(organizerId, deviceId)) {
                    OrganizerEventFragment frag = new OrganizerEventFragment();
                    frag.setArguments(bundle);
                    MyApp.getInstance().addFragmentToStack(frag);
                } else {
                    eventsController.editEvent(event);
                }
            }
        });

        return view;
    }
}
