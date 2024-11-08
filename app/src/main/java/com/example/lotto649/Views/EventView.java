package com.example.lotto649.Views;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.AbstractClasses.AbstractView;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Views.Fragments.EventFragment;

public class EventView extends AbstractView {

    private final EventFragment eventFragment;

    public EventView(EventModel event, EventFragment fragment) {
        this.eventFragment = fragment;
        startObserving(event);
    }

    @Override
    public EventModel getModel() {
        return (EventModel) super.getModel();
    }

    @Override
    public void update(AbstractModel whoUpdatedMe) {
        EventModel event = getModel();
        eventFragment.showEventDetails(event);
    }
}
