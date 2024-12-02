/**
 * Fragment for sending custom notifications to users based on their status.
 * <p>
 * This fragment allows an admin to create and send notifications to users in specific status groups
 * (e.g., "All," "Selected," "Cancelled"). Notifications are sent using Firestore to dynamically fetched
 * user groups. The fragment provides fields for entering a notification title and message, along with
 * a dropdown for selecting the recipient group.
 * </p>
 *
 * <p>
 * The `CustomNotificationFragment` is designed for use in the admin interface of the application.
 * It integrates with Firebase Firestore to retrieve relevant user data and send notifications
 * to appropriate collections.
 * </p>
 */
package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment for sending custom notifications to users based on their status.
 * <p>
 * This fragment allows an admin to create and send notifications to users in specific status groups
 * (e.g., "All," "Selected," "Cancelled"). Notifications are sent using Firestore to dynamically fetched
 * user groups. The fragment provides fields for entering a notification title and message, along with
 * a dropdown for selecting the recipient group.
 * </p>
 *
 * <p>
 * The `CustomNotificationFragment` is designed for use in the admin interface of the application.
 * It integrates with Firebase Firestore to retrieve relevant user data and send notifications
 * to appropriate collections.
 * </p>
 */
public class CustomNotificationFragment extends Fragment {

    private Spinner statusDropdown;
    private EditText titleInput, descriptionInput;
    private FirebaseFirestore db;

    /**
     * Default constructor for `CustomNotificationFragment`.
     * <p>
     * Ensures proper instantiation of the fragment by the Android system.
     * </p>
     */
    public CustomNotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Initializes the view for this fragment.
     * <p>
     * This method inflates the layout for the custom notification feature, initializes UI components,
     * and sets up click listeners for navigation and notification submission.
     * </p>
     *
     * @param inflater  The `LayoutInflater` object to inflate views in the fragment.
     * @param container The parent view that this fragment's UI will be attached to.
     * @param savedInstanceState A `Bundle` containing the saved state of the fragment, if available.
     * @return A `View` object representing the root view of the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String eventId = getArguments().getString("eventId");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_custom_noti, container, false);

        // Initialize UI components
        statusDropdown = view.findViewById(R.id.status_dropdown);
        titleInput = view.findViewById(R.id.title_input);
        descriptionInput = view.findViewById(R.id.description_input);
        ExtendedFloatingActionButton backButton = view.findViewById(R.id.back_button);
        ExtendedFloatingActionButton sendButton = view.findViewById(R.id.send_button);

        // Set up listeners
        backButton.setOnClickListener(v -> {
            // Handle back navigation
            getParentFragmentManager().popBackStack();
        });

        sendButton.setOnClickListener(v -> {
            // Handle sending the notification
            String selectedStatus = statusDropdown.getSelectedItem().toString();
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            getCollectionForStatus(selectedStatus, title, description, eventId);
        });
        return view;
    }

    /**
     * Determines the Firestore collection to target based on the selected status.
     * <p>
     * Depending on the status ("All," "Selected," or "Cancelled"), this method sends notifications
     * to users in corresponding Firestore collections.
     * </p>
     *
     * @param status    The selected status group ("All," "Selected," or "Cancelled").
     * @param title     The title of the notification.
     * @param message   The message body of the notification.
     * @param eventId   The ID of the event associated with the notification.
     */
    private void getCollectionForStatus(String status, String title, String message, String eventId) {
        switch (status) {
            case "All": {
                sendNotificationsToCollection("signUps", title, message, eventId);
                break;
            }
            case "Selected": {
                sendNotificationsToCollection("winners", title, message, eventId);
                sendNotificationsToCollection("enrolled", title, message, eventId);
                break;
            }
            case "Cancelled": {
                sendNotificationsToCollection("cancelled", title, message, eventId);
                break;
            } default: {
                Toast.makeText(getContext(), "Invalid status selected", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    /**
     * Sends notifications to a specific Firestore collection.
     * <p>
     * Queries the given Firestore collection for users associated with the provided `eventId`
     * and creates a new document in the "custom" collection to store the notification data.
     * </p>
     *
     * @param collectionName The name of the Firestore collection to query.
     * @param title          The title of the notification.
     * @param message        The message body of the notification.
     * @param eventId        The ID of the event associated with the notification.
     */
    private void sendNotificationsToCollection(String collectionName, String title, String message, String eventId) {
        db = FirebaseFirestore.getInstance();

        db.collection(collectionName).whereEqualTo("eventId", eventId).get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                    // Task succeeded and documents exist
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String deviceId = doc.getString("userId");

                        if (deviceId != null) {
                            // Create a new document in the "custom" collection
                            Map<String, Object> notificationData = new HashMap<>();
                            notificationData.put("userId", deviceId);
                            notificationData.put("title", title);
                            notificationData.put("message", message);
                            notificationData.put("hasSeenNoti", false);
                            notificationData.put("eventId", eventId);

                            db.collection("custom").add(notificationData)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d("Custom Notifications", "Notification sent to: " + deviceId);
                                        MyApp.getInstance().popFragment();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreError", "Error sending notification: " + e.getMessage());
                                        MyApp.getInstance().popFragment();
                                    });
                        } else {
                            Log.w("FirestoreWarning", "User ID is null for document: " + doc.getId());
                        }
                    }
            } else {
                // Task failed
                Log.e("FirestoreError", "Error fetching users: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                Toast.makeText(getContext(), "Error fetching users: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
