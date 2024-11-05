package com.example.lotto649.Controllers;

import com.example.lotto649.AbstractClasses.AbstractController;
import com.example.lotto649.Models.EventModel;

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

    public void updateCost(double cost) {
        getModel().setCost(cost);
    }

    public void saveEventToFirestore() {
        getModel().saveEventToFirestore();
    }
}