package com.example.lotto649.Models;

import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;

public interface EntrantInterface {
    String getName();
    void setName(String name);

    String getEmail();
    void setEmail(String email);

    String getPhone();
    void setPhone(String phone);

    String getProfilePictureUrl();
    void setProfilePictureUrl(String profilePictureUrl);

    // Event Participation
    List<String> getEventsJoined();
    void joinEvent(String eventId);
    void leaveEvent(String eventId);

    List<String> getInvitations();
    void addInvitation(String invitationId);
    void acceptInvitation(String invitationId);
    void declineInvitation(String invitationId);

    // Notifications
    boolean isNotificationsEnabled();
    void enableNotifications();
    void disableNotifications();
    List<String> getNotifications();
    void addNotification(String notification);

    // Firestore Integration
    void saveEntrantToFirestore();
    void updateEntrantInFirestore(String field, Object value);
    void deleteEntrantFromFirestore();

}
