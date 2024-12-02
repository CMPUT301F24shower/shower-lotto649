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

import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class CustomNotificationFragment extends Fragment {

    private Spinner statusDropdown;
    private EditText descriptionInput;
    private FirebaseFirestore db;

    public CustomNotificationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String eventId = getArguments().getString("eventId");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_custom_noti, container, false);

        // Initialize UI components
        statusDropdown = view.findViewById(R.id.status_dropdown);
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
            String description = descriptionInput.getText().toString().trim();

            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            String collectionName = getCollectionForStatus(selectedStatus);

            if (collectionName != null) {
                sendNotificationsToCollection(collectionName, description);
            } else {
                Toast.makeText(getContext(), "Invalid status selected", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private String getCollectionForStatus(String status) {
        switch (status) {
            case "All":
                return "signUps";
            case "Selected":
                return "winners";
            case "Cancelled":
                return "cancelled";
            default:
                return null;
        }
    }

    private void sendNotificationsToCollection(String collectionName, String message) {
        db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection(collectionName);

        collection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && !task.getResult().isEmpty()) {
                    // Task succeeded and documents exist
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String deviceId = doc.getString("deviceId");

                        if (deviceId != null) {
                            // Create a new document in the "custom" collection
                            Map<String, Object> notificationData = new HashMap<>();
                            notificationData.put("deviceId", deviceId);
                            notificationData.put("message", message);

                            db.collection("custom").add(notificationData)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(getContext(), "Notification sent to: " + deviceId, Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreError", "Error sending notification: " + e.getMessage());
                                        Toast.makeText(getContext(), "Error sending notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.w("FirestoreWarning", "Device ID is null for document: " + doc.getId());
                        }
                    }
                } else {
                    // Task succeeded but no documents found
                    Log.w("FirestoreWarning", "No documents found in collection: " + collectionName);
                    Toast.makeText(getContext(), "No users found in the selected collection", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Task failed
                Log.e("FirestoreError", "Error fetching users: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                Toast.makeText(getContext(), "Error fetching users: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
