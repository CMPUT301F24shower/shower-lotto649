package com.example.lotto649.Models;

import java.util.ArrayList;

// TODO never used, lets remove it
/**
 * Interface for organizers to create, manage, and handle event-related operations.
 * Provides methods for event creation, QR code generation, waiting list management,
 * attendee selection, and notification sending.
 */
public interface OrganizerInterface {

    /**
     * Creates or updates an event with the specified details.
     *
     * @param eventId      The unique identifier of the event.
     * @param eventName    The name of the event.
     * @param eventDetails Details about the event.
     */
    void createOrUpdateEvent(String eventId, String eventName, String eventDetails);

    /**
     * Uploads or updates the poster image for an event.
     *
     * @param eventId    The unique identifier of the event.
     * @param posterData Byte array representing the poster image data.
     */
    void uploadOrUpdateEventPoster(String eventId, byte[] posterData);

    /**
     * Generates and stores a QR code for the specified event.
     *
     * @param eventId The unique identifier of the event.
     * @return A string representation of the generated QR code.
     */
    String generateAndStoreQRCode(String eventId);

    /**
     * Retrieves the waiting list of entrants for the specified event.
     *
     * @param eventId The unique identifier of the event.
     * @return An ArrayList of entrant IDs on the waiting list.
     */
    ArrayList<String> getWaitingArrayList(String eventId);

    /**
     * Adds an entrant to the waiting list for a specified event.
     *
     * @param eventId   The unique identifier of the event.
     * @param entrantId The unique identifier of the entrant to add.
     */
    void addToWaitingArrayList(String eventId, String entrantId);

    /**
     * Removes an entrant from the waiting list for a specified event.
     *
     * @param eventId   The unique identifier of the event.
     * @param entrantId The unique identifier of the entrant to remove.
     */
    void removeFromWaitingArrayList(String eventId, String entrantId);

    /**
     * Sets a limit for the number of entrants allowed for a specific event.
     *
     * @param eventId The unique identifier of the event.
     * @param limit   The maximum number of entrants allowed.
     */
    void setEntrantLimit(String eventId, int limit);

    /**
     * Sends a notification to a specific entrant.
     *
     * @param entrantId The unique identifier of the entrant.
     * @param message   The notification message to send.
     */
    void sendNotification(String entrantId, String message);

    /**
     * Selects an entrant for an event.
     *
     * @param eventId   The unique identifier of the event.
     * @param entrantId The unique identifier of the entrant to select.
     */
    void selectEntrant(String eventId, String entrantId);

    /**
     * Replaces an existing entrant with a new entrant for a specified event.
     *
     * @param eventId      The unique identifier of the event.
     * @param oldEntrantId The unique identifier of the entrant to be replaced.
     * @param newEntrantId The unique identifier of the new entrant.
     */
    void replaceEntrant(String eventId, String oldEntrantId, String newEntrantId);

    /**
     * Retrieves the list of selected attendees for the specified event.
     *
     * @param eventId The unique identifier of the event.
     * @return An ArrayList of IDs of selected attendees.
     */
    ArrayList<String> getSelectedAttendees(String eventId);

    /**
     * Retrieves the list of cancelled attendees for the specified event.
     *
     * @param eventId The unique identifier of the event.
     * @return An ArrayList of IDs of cancelled attendees.
     */
    ArrayList<String> getCancelledAttendees(String eventId);

    /**
     * Retrieves the final list of attendees for the specified event.
     *
     * @param eventId The unique identifier of the event.
     * @return An ArrayList of IDs of final attendees.
     */
    ArrayList<String> getFinalAttendees(String eventId);
}
