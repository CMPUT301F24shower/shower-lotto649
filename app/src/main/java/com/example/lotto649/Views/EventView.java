package com.example.lotto649.Views;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.AbstractClasses.AbstractView;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Views.Fragments.EventFragment;

// TODO use this method same as AccountView is used to update the UI
/**
 * EventView is responsible for observing changes in the EventModel and updating
 * the corresponding EventFragment with the latest event details.
 */
public class EventView extends AbstractView {

    private final EventFragment eventFragment;

    /**
     * Constructs an EventView with the specified model and fragment.
     *
     * @param event The model representing the event data.
     * @param fragment The fragment used to display event details.
     */

    public EventView(EventModel event, EventFragment fragment) {
        this.eventFragment = fragment;
        startObserving(event);
    }

    /**
     * Retrieves the EventModel associated with this view.
     *
     * @return The EventModel instance this view is observing.
     */
    @Override
    public EventModel getModel() {
        return (EventModel) super.getModel();
    }

    /**
     * Updates the view when the observed model changes.
     *
     * @param whoUpdatedMe The model that triggered the update.
     */
    @Override
    public void update(AbstractModel whoUpdatedMe) {
        EventModel event = getModel();
        eventFragment.showEventDetails(event);
    }
}
