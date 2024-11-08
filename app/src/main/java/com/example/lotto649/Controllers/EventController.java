package com.example.lotto649.Controllers;

import android.graphics.Bitmap;

import com.example.lotto649.AbstractClasses.AbstractController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.Views.Fragments.HomeFragment;
//import com.example.lotto649.Views.Fragments.EventsFragment;

import java.util.Date;

/**
 * Controller class for managing an EventModel instance.
 * Provides methods to update event properties and save/remove events from Firestore.
 */
public class EventController extends AbstractController {

    /**
     * Constructs an EventController for the specified EventModel.
     *
     * @param event The EventModel instance to be managed by this controller.
     */
    public EventController(EventModel event) {
        super(event);
    }

    /**
     * Retrieves the EventModel associated with this controller.
     *
     * @return The EventModel instance.
     */
    @Override
    public EventModel getModel() {
        return (EventModel) super.getModel();
    }

    /**
     * Updates the title of the event.
     *
     * @param title The new title for the event.
     */
    public void updateTitle(String title) {
        getModel().setTitle(title);
    }

    /**
     * Updates the description of the event.
     *
     * @param description The new description for the event.
     */
    public void updateDescription(String description) {
        getModel().setDescription(description);
    }

    /**
     * Updates the number of spots available for the event.
     *
     * @param spots The new number of available spots.
     */
    public void updateNumberOfSpots(int spots) {
        getModel().setNumberOfSpots(spots);
    }

    /**
     * Updates the maximum number of entrants allowed for the event.
     *
     * @param maxEntrants The maximum number of entrants.
     */
    public void updateNumberOfMaxEntrants(int maxEntrants) { getModel().setNumberOfMaxEntrants(maxEntrants); }

    /**
     * Updates the start date of the event.
     *
     * @param startDate The new start date for the event.
     */
    public void updateStartDate(Date startDate) {
        getModel().setStartDate(startDate);
    }

    /**
     * Updates the end date of the event.
     *
     * @param endDate The new end date for the event.
     */
    public void updateEndDate(Date endDate) {
        getModel().setEndDate(endDate);
    }

    /**
     * Updates the cost of the event.
     *
     * @param cost The new cost for the event.
     */
    public void updateCost(double cost) {
        getModel().setCost(cost);
    }

    /**
     * Updates the geographic requirement (geo-location) for the event.
     *
     * @param geo The new geographic requirement status.
     */
    public void updateGeo(boolean geo) {
        getModel().setGeo(geo);
    }

    /**
     * Updates the URI of the event's poster image.
     *
     * @param posterUri The URI of the new poster image.
     */
    public void updatePoster(String posterUri) {
        getModel().setPosterImage(posterUri);
    }
    /**
     * Updates QR Code hash
     */
    public void updateQrCode(String qrCodeHash){
        getModel().setQrCode(qrCodeHash);
    }

    /**
     * Saves the current event to Firestore and navigates to the HomeFragment.
     */
    public void saveEventToFirestore() {
        getModel().saveEventToFirestore();
        MyApp.getInstance().replaceFragment(new HomeFragment());
    }

    /**
     * Removes the current event from Firestore and navigates to the HomeFragment.
     */
    public void removeEventFromFirestore() {
        getModel().removeEventFromFirestore();
        MyApp.getInstance().replaceFragment(new HomeFragment());
    }

    /**
     * Navigates back to the HomeFragment, which serves as the main events screen.
     */
    public void returnToEvents() {
        MyApp.getInstance().replaceFragment(new HomeFragment());
    }
}
