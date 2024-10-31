package com.example.lotto649.Models;

import java.util.ArrayList;

public interface OrganizerInterface {
    // Method to create or manage events
    void createOrUpdateEvent(String eventId, String eventName, String eventDetails);

    // Method to upload or update event posters
    void uploadOrUpdateEventPoster(String eventId, byte[] posterData);

    // Method to generate and store QR codes for events
    String generateAndStoreQRCode(String eventId);

    // Method to view or manage entrants on waiting ArrayLists
    ArrayList<String> getWaitingArrayList(String eventId);
    void addToWaitingArrayList(String eventId, String entrantId);
    void removeFromWaitingArrayList(String eventId, String entrantId);

    // Method to set limits for the number of entrants
    void setEntrantLimit(String eventId, int limit);

    // Method to send notifications to entrants
    void sendNotification(String entrantId, String message);

    // Method to select or replace entrants for an event
    void selectEntrant(String eventId, String entrantId);
    void replaceEntrant(String eventId, String oldEntrantId, String newEntrantId);

    // Method to view selected, cancelled, and final attendees for an event
    ArrayList<String> getSelectedAttendees(String eventId);
    ArrayList<String> getCancelledAttendees(String eventId);
    ArrayList<String> getFinalAttendees(String eventId);
}
