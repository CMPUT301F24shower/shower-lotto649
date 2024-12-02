package com.example.lotto649.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.lotto649.Views.ArrayAdapters.BrowseEventsArrayAdapter;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * JoinedEventsFragment class displays the events that the user has joined and allows interaction with them.
 * <p>
 * This fragment fetches and displays a list of events the user is signed up for from Firestore.
 * It shows a message if the user has not joined any events and allows navigation to a specific event's details.
 * </p>
 */
public class JoinedEventsFragment extends Fragment {
    private BrowseEventsArrayAdapter eventAdapter;

    /**
     * Required empty public constructor.
     */
    public JoinedEventsFragment() {
        // No-arg constructor
    }

    /**
     * Initializes the fragment's view hierarchy and fetches the events the user has joined.
     * <p>
     * This method inflates the layout, checks the user's account and facility status,
     * retrieves the events the user has signed up for from Firestore, and updates the UI accordingly.
     * </p>
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_joined_events, container, false);

        // Check that the user has created an account and a facility, if they haven't hide the create event button
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        ListView eventsList = view.findViewById(R.id.event_contents);
        ArrayList<EventModel> eventArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MutableLiveData<Boolean> noJoinedEvents = new MutableLiveData<>(Boolean.TRUE);
        Query query = db.collection("signUps").whereEqualTo("userId", deviceId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String eventId = doc.getString("eventId");
                    if (eventId == null) continue;
                    db.collection("events").document(eventId).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            EventModel event = task2.getResult().toObject(EventModel.class);
                            if (event == null) return;
                            noJoinedEvents.setValue(Boolean.FALSE);
                            event.setEventId(eventId);
                            eventArrayList.add(event);
                            if (eventAdapter != null) {
                                eventAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                            }
                        }
                    });

                }
                Context context = getContext();
                if (context == null) return;
                eventAdapter = new BrowseEventsArrayAdapter(context, eventArrayList, getViewLifecycleOwner());
                eventsList.setAdapter(eventAdapter);
            }
        });

        ConstraintLayout layout = view.findViewById(R.id.joined_events_layout);
        Context context = getContext();

        TextView textView;
        if (context != null) {
            textView = new TextView(context);
        } else {
            textView = null;
        }
        noJoinedEvents.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            /**
             * Observes the status of the joined events and updates the UI accordingly.
             * <p>
             * If the user has not joined any events, a TextView is added to the layout to inform the user
             * to scan a QR code to join an event. If events are available, the TextView is hidden.
             * </p>
             */
            @Override
            public void onChanged(Boolean aBoolean) {
                if (Objects.equals(aBoolean, Boolean.TRUE)) {
                    if (textView != null) {
                        textView.setId(View.generateViewId()); // Generate an ID for the TextView
                        textView.setText("You have not signed up for any events, please scan a QR code to join an event");
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

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Handles interaction with the list of joined events.
             * <p>
             * When the user clicks on a joined event, the details of the event are opened in a new fragment.
             * </p>
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventModel event = (EventModel) adapterView.getItemAtPosition(i);

                String eventId = event.getEventId();
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", eventId);
                JoinEventFragment frag = new JoinEventFragment();
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
