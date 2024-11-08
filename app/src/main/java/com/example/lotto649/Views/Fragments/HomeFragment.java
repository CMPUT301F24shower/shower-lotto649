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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.lotto649.Adapters.EventArrayAdapter;
import com.example.lotto649.Controllers.EventsController;
import com.example.lotto649.Models.EventsModel;
import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class HomeFragment extends Fragment {
    private EventsController eventsController;
    private ExtendedFloatingActionButton addButton;
    private EventArrayAdapter eventAdapter;
    private EventsModel events;

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
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        events = new EventsModel();
        eventsController = new EventsController(events);
        addButton = view.findViewById(R.id.addButton);

        ListView eventsList = view.findViewById(R.id.event_contents);

        Log.w("Ohm", "Eve cont");
        Log.w("Ohm", String.valueOf(eventsController.getMyEvents().isEmpty()));
        Log.w("Ohm", String.valueOf(eventsController.getMyEvents().size()));

        eventAdapter = new EventArrayAdapter(getContext(), eventsController.getMyEvents(), new EventArrayAdapter.EventArrayAdapterListener() {
          @Override
          public void onEventsWaitListChanged(){};
        });
        eventsList.setAdapter(eventAdapter);


        addButton.setOnClickListener(v -> eventsController.addEvent());

        return view;
    }
}
