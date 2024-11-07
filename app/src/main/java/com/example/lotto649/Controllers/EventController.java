package com.example.lotto649.Controllers;

import com.example.lotto649.AbstractClasses.AbstractController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.Views.Fragments.HomeFragment;
//import com.example.lotto649.Views.Fragments.EventsFragment;

import java.util.Date;

public class EventController extends AbstractController {

    public EventController(EventModel event) {
        super(event);
    }

    @Override
    public EventModel getModel() {
        return (EventModel) super.getModel();
    }

    public void updateTitle(String title) {
        getModel().setTitle(title);
    }

    public void updateDescription(String description) {
        getModel().setDescription(description);
    }

    public void updateNumberOfSpots(int spots) {
        getModel().setNumberOfSpots(spots);
    }

    public void updateNumberOfMaxEntrants(int maxEntrants) { getModel().setNumberOfMaxEntrants(maxEntrants); }

    public void updateStartDate(Date startDate) {
        getModel().setStartDate(startDate);
    }

    public void updateEndDate(Date endDate) {
        getModel().setEndDate(endDate);
    }

    public void updateCost(double cost) {
        getModel().setCost(cost);
    }

    public void updateGeo(boolean geo) {
        getModel().setGeo(geo);
    }

    public void saveEventToFirestore() {
        getModel().saveEventToFirestore();
        MyApp.getInstance().replaceFragment(new HomeFragment());
    }

    public void removeEventFromFirestore() {
        getModel().removeEventFromFirestore();
        MyApp.getInstance().replaceFragment(new HomeFragment());
    }
}
