package com.example.lotto649.Controllers;

import static android.app.PendingIntent.getActivity;

import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.lotto649.AbstractClasses.AbstractController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.EventsModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.example.lotto649.Views.Fragments.EventFragment;

import java.util.ArrayList;

/**
 * Controller class for managing a collection of events represented by the EventsModel.
 * Provides methods for accessing, adding, and editing events.
 */
public class EventsController extends AbstractController {

    /**
     * Constructs an EventsController with the specified EventsModel.
     *
     * @param events The EventsModel instance to be managed by this controller.
     */
    public EventsController(EventsModel events) {
        super(events);
    }

    /**
     * Retrieves the EventsModel associated with this controller.
     *
     * @return The EventsModel instance.
     */
    @Override
    public EventsModel getModel() {
        return (EventsModel) super.getModel();
    }

    /**
     * Retrieves the events specific to the current user.
     *
     * @param events Callback interface for handling the retrieval of user's events.
     */
    public void getMyEvents(EventsModel.MyEventsCallback events) {
        getModel().getMyEvents(events);
    }

    // TODO this isnt a controller, move code to fragment
    /**
     * Navigates to the fragment for creating a new event.
     * Replaces the current fragment with the EventFragment for event creation.
     */
    public void addEvent() {
        EventFragment eventFragment = new EventFragment();
        MyApp.getInstance().addFragmentToStack(eventFragment);
    }

    /**
     * Navigates to the fragment for editing an existing event.
     * Replaces the current fragment with the EventFragment, pre-populated with the provided event.
     *
     * @param event The EventModel instance representing the event to be edited.
     */
    public void editEvent(EventModel event) {
        EventFragment eventFragment = new EventFragment(event);
        MyApp.getInstance().addFragmentToStack(eventFragment);
    }
}
