package com.example.lotto649.Views.Fragments;

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

import com.example.lotto649.Views.ArrayAdapters.BrowseEventsArrayAdapter;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class JoinedEventsFragment extends Fragment {
    private BrowseEventsArrayAdapter eventAdapter;

    /**
     * Required empty public constructor.
     */
    public JoinedEventsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_joined_events, container, false);

        // Check that the user has created an account and a facility, if they haven't hide the create event button
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        ListView eventsList = view.findViewById(R.id.event_contents);
        ArrayList<EventModel> eventArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("signUps").whereEqualTo("userId", deviceId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean noEvents = true;
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    noEvents = false;
                    String eventId = doc.getString("eventId");
                    if (eventId == null) continue;
                    db.collection("events").document(eventId).get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            EventModel event = task2.getResult().toObject(EventModel.class);
                            if (event == null) return;
                            event.setEventId(eventId);
                            eventArrayList.add(event);
                            if (eventAdapter != null) {
                                eventAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                            }
                        }
                    });

                }
                if (noEvents) {
                    ConstraintLayout layout = view.findViewById(R.id.joined_events_layout);
                    TextView textView = new TextView(getContext());
                    textView.setId(View.generateViewId()); // Generate an ID for the TextView
                    textView.setText("No events to display\nPlease scan a QR code\nto join an event");
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
                Context context = getContext();
                if (context == null) return;
                eventAdapter = new BrowseEventsArrayAdapter(context, eventArrayList);
                eventsList.setAdapter(eventAdapter);
            }
        });

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventModel event = (EventModel) adapterView.getItemAtPosition(i);

                String eventId = event.getEventId();
                Bundle bundle = new Bundle();
                // TODO check that this is a valid event first
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
