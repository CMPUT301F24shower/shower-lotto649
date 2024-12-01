package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
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

public class CustomNotificationFragment extends Fragment {

    private Spinner statusDropdown;
    private EditText descriptionInput;

    public CustomNotificationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

            // Placeholder for sending logic
            Toast.makeText(getContext(), "Notification sent!\nStatus: " + selectedStatus + "\nDescription: " + description, Toast.LENGTH_LONG).show();
        });

        return view;
    }
}
