package com.example.lotto649.Controllers;

import androidx.fragment.app.FragmentActivity;

import com.example.lotto649.AbstractClasses.AbstractController;
import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.EventsModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.Views.Fragments.EventFragment;

import java.util.ArrayList;

public class EventsController extends AbstractController {

    public EventsController(EventsModel events) {
        super(events);
    }

    @Override
    public EventsModel getModel() {
        return (EventsModel) super.getModel();
    }

    public ArrayList<EventModel> getMyEvents() {
        return getModel().getMyEvents();
    }

    public void addEvent() {
        EventFragment eventFragment = new EventFragment();
        MyApp.getInstance().replaceFragment(eventFragment);
    }

}
