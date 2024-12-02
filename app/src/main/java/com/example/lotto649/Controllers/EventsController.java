package com.example.lotto649.Controllers;

import com.example.lotto649.AbstractClasses.AbstractController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.HomePageModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.Views.Fragments.EventFragment;

/**
 * Controller class for managing a collection of events represented by the HomePageModel.
 * Provides methods for accessing, adding, and editing events.
 */
public class EventsController extends AbstractController {

    /**
     * Constructs an EventsController with the specified HomePageModel.
     *
     * @param events The HomePageModel instance to be managed by this controller.
     */
    public EventsController(HomePageModel events) {
        super(events);
    }

    /**
     * Retrieves the HomePageModel associated with this controller.
     *
     * @return The HomePageModel instance.
     */
    @Override
    public HomePageModel getModel() {
        return (HomePageModel) super.getModel();
    }

    /**
     * Retrieves the events specific to the current user.
     *
     * @param events Callback interface for handling the retrieval of user's events.
     */
    public void getMyEvents(HomePageModel.MyEventsCallback events) {
        getModel().getMyEvents(events);
    }

    /**
     * Navigates to the fragment for creating a new event.
     * Replaces the current fragment with the EventFragment for event creation.
     * <p>
     * Outstanding issue, this should be done in fragment class, not controller
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

