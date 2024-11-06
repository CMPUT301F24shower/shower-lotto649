package com.example.lotto649.Models;

import java.util.List;

/**
 * EntrantInterface defines the contract for managing entrant-specific details,
 * including personal information, event participation, invitations, and notifications.
 * It also includes Firestore integration for storing and updating entrant data.
 */
public interface EntrantInterface {

    /**
     * Retrieves the entrant's name.
     *
     * @return the entrant's name
     */
    String getName();

    /**
     * Sets the entrant's name.
     *
     * @param name the name to set for the entrant
     */
    void setName(String name);

    /**
     * Retrieves the entrant's email address.
     *
     * @return the entrant's email
     */
    String getEmail();

    /**
     * Sets the entrant's email address.
     *
     * @param email the email to set for the entrant
     */
    void setEmail(String email);

    /**
     * Retrieves the entrant's phone number.
     *
     * @return the entrant's phone number
     */
    String getPhone();

    /**
     * Sets the entrant's phone number.
     *
     * @param phone the phone number to set for the entrant
     */
    void setPhone(String phone);

    /**
     * Retrieves the URL of the entrant's profile picture.
     *
     * @return the profile picture URL of the entrant
     */
    String getProfilePictureUrl();

    /**
     * Sets the URL of the entrant's profile picture.
     *
     * @param profilePictureUrl the URL to set for the entrant's profile picture
     */
    void setProfilePictureUrl(String profilePictureUrl);

    // Event Participation

    /**
     * Retrieves the list of event IDs the entrant has joined.
     *
     * @return a list of event IDs the entrant has joined
     */
    List<String> getEventsJoined();

    /**
     * Adds an event to the list of events the entrant has joined.
     *
     * @param eventId the ID of the event to join
     */
    void joinEvent(String eventId);

    /**
     * Removes an event from the list of events the entrant has joined.
     *
     * @param eventId the ID of the event to leave
     */
    void leaveEvent(String eventId);

    // Invitation Management

    /**
     * Retrieves the list of pending invitations for the entrant.
     *
     * @return a list of invitation IDs
     */
    List<String> getInvitations();

    /**
     * Adds an invitation to the list of invitations for the entrant.
     *
     * @param invitationId the ID of the invitation to add
     */
    void addInvitation(String invitationId);

    /**
     * Accepts a pending invitation.
     *
     * @param invitationId the ID of the invitation to accept
     */
    void acceptInvitation(String invitationId);

    /**
     * Declines a pending invitation.
     *
     * @param invitationId the ID of the invitation to decline
     */
    void declineInvitation(String invitationId);

    // Notification Management

    /**
     * Checks if notifications are enabled for the entrant.
     *
     * @return true if notifications are enabled, false otherwise
     */
    boolean isNotificationsEnabled();

    /**
     * Enables notifications for the entrant.
     */
    void enableNotifications();

    /**
     * Disables notifications for the entrant.
     */
    void disableNotifications();

    /**
     * Retrieves the list of notifications received by the entrant.
     *
     * @return a list of notification messages
     */
    List<String> getNotifications();

    /**
     * Adds a notification message to the entrant's notifications list.
     *
     * @param notification the notification message to add
     */
    void addNotification(String notification);

    // Firestore Integration

    /**
     * Saves the entrant's data to Firestore.
     */
    void saveEntrantToFirestore();

    /**
     * Updates a specific field of the entrant's data in Firestore.
     *
     * @param field the field to update in Firestore
     * @param value the value to update in the specified field
     */
    void updateEntrantInFirestore(String field, Object value);

    /**
     * Deletes the entrant's data from Firestore.
     */
    void deleteEntrantFromFirestore();
}
