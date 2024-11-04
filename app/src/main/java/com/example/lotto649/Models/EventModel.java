package com.example.lotto649.Models;

import java.util.ArrayList;
import com.example.lotto649.AbstractClasses.AbstractModel;

/**
 * The {@code EventModel} class represents an event with details such as title, location, cost,
 * description, number of spots, type, and associated media like a poster image and QR code.
 * It also manages a waiting list of entrants and allows generating a QR code for the event.
 * <p>
 * Collaborators include:
 * <ul>
 *     <li>FacilityModel</li>
 *     <li>NotificationSys</li>
 *     <li>UserModel (entrant)</li>
 *     <li>QrCodeModel</li>
 * </ul>
 * </p>
 */
public class EventModel extends AbstractModel {
    /**
     * The title of the event.
     */
    private String title;

    /**
     * The location of the event, represented by a FacilityModel.
     */
    private FacilityModel location;

    /**
     * The cost of attending the event.
     */
    private double cost;

    /**
     * The description of the event.
     */
    private String description;

    /**
     * The number of available spots for the event.
     */
    private int numberOfSpots;

    /**
     * The type of event, such as "conference", "workshop", etc.
     */
    private String eventType;

    /**
     * The poster image for the event.
     * (Object type for flexibility; consider changing to an image class later).
     */
    private Object posterImage; // edit to actual class later

    /**
     * The path or reference to the QR code image for the event.
     */
    private String qrCodePath;

    /**
     * The list of entrants on the waiting list.
     */
    private final ArrayList<UserModel> waitingList = new ArrayList<>();

    /**
     * Constructs an EventModel instance with required attributes, excluding poster image.
     *
     * @param title       the title of the event
     * @param location    the FacilityModel representing the location of the event
     * @param cost        the cost to attend the event
     * @param description a brief description of the event
     * @param numberOfSpots the number of available spots for the event
     * @param eventType   the type/category of the event
     */
    public EventModel(String title, FacilityModel location, double cost, String description, int numberOfSpots, String eventType) {
        this.title = title;
        this.location = location;
        this.cost = cost;
        this.description = description;
        this.numberOfSpots = numberOfSpots;
        this.eventType = eventType;
        this.posterImage = null; // edit to default img
        generateQrCode();
    }

    /**
     * Constructs an EventModel instance with all attributes, including poster image.
     *
     * @param title       the title of the event
     * @param location    the FacilityModel representing the location of the event
     * @param cost        the cost to attend the event
     * @param description a brief description of the event
     * @param numberOfSpots the number of available spots for the event
     * @param eventType   the type/category of the event
     * @param posterImage the poster image for the event
     */
    public EventModel(String title, FacilityModel location, double cost, String description, int numberOfSpots, String eventType, Object posterImage) {
        this.title = title;
        this.location = location;
        this.cost = cost;
        this.description = description;
        this.numberOfSpots = numberOfSpots;
        this.eventType = eventType;
        this.posterImage = posterImage;
        generateQrCode();
    }

    /**
     * Generates a QR code for the event.
     * This is a mock implementation, assuming the QRCode utility class is present.
     */
    private void generateQrCode() {
        //this.qrCodePath = QrCodeModel.generateForEvent(this);
    }

    /**
     * Adds an entrant to the waiting list.
     *
     * @param entrant the user to add to the waiting list
     */
    public void addToWaitingList(UserModel entrant) {
        waitingList.add(entrant);
    }

    /**
     * Gets the title of the event.
     *
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the event.
     *
     * @param title the new title of the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the location of the event.
     *
     * @return the location of the event
     */
    public FacilityModel getLocation() {
        return location;
    }

    /**
     * Sets the location of the event.
     *
     * @param location the new location of the event
     */
    public void setLocation(FacilityModel location) {
        this.location = location;
    }

    /**
     * Gets the cost of the event.
     *
     * @return the cost of the event
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the cost of the event.
     *
     * @param cost the new cost of the event
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Gets the description of the event.
     *
     * @return the description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     *
     * @param description the new description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the number of spots available for the event.
     *
     * @return the number of available spots
     */
    public int getNumberOfSpots() {
        return numberOfSpots;
    }

    /**
     * Sets the number of spots available for the event.
     *
     * @param numberOfSpots the new number of spots
     */
    public void setNumberOfSpots(int numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
    }

    /**
     * Gets the type of the event.
     *
     * @return the event type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the type of the event.
     *
     * @param eventType the new event type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the poster image of the event.
     *
     * @return the poster image
     */
    public Object getPosterImage() {
        return posterImage;
    }

    /**
     * Sets the poster image of the event.
     *
     * @param posterImage the new poster image
     */
    public void setPosterImage(Object posterImage) {
        this.posterImage = posterImage;
    }

    /**
     * Gets the path to the QR code image for the event.
     *
     * @return the QR code path
     */
    public String getQrCodePath() {
        return qrCodePath;
    }

    /**
     * Gets the list of entrants on the waiting list.
     *
     * @return the waiting list
     */
    public ArrayList<UserModel> getWaitingList() {
        return waitingList;
    }

    /**
     * Notifies all entrants on the waiting list through the NotificationSys system.
     *
     * @param notificationSys the notification system used to send messages
     */
    public void notifyEntrants(NotificationSys notificationSys) {
        for (UserModel entrant : waitingList) {
            notificationSys.sendNotification(entrant, "You are on the waiting list for " + title);
        }
    }
}
