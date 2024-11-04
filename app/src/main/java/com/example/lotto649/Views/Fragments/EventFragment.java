package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.lotto649.Controllers.EventController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
import com.example.lotto649.Views.EventView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

public class EventFragment extends Fragment {

    private EventView eventView;
    private EventController eventController;
    private FirebaseFirestore db;
    private EventModel event;

    private TextInputLayout titleInputLayout, descriptionInputLayout, spotsInputLayout, costInputLayout;
    private TextInputEditText titleEditText, descriptionEditText, spotsEditText, costEditText;
    private ExtendedFloatingActionButton saveButton;

    public EventFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // Initialize UI elements
        titleInputLayout = view.findViewById(R.id.textFieldTitle);
        descriptionInputLayout = view.findViewById(R.id.textFieldDescription);
        spotsInputLayout = view.findViewById(R.id.textFieldSpots);
        costInputLayout = view.findViewById(R.id.textFieldCost);

        titleEditText = (TextInputEditText) titleInputLayout.getEditText();
        descriptionEditText = (TextInputEditText) descriptionInputLayout.getEditText();
        spotsEditText = (TextInputEditText) spotsInputLayout.getEditText();
        costEditText = (TextInputEditText) costInputLayout.getEditText();
        saveButton = view.findViewById(R.id.saveButton);

        // Initialize MVC components
        db = FirebaseFirestore.getInstance();
        event = new EventModel(getContext(), FirebaseFirestore.getInstance());
        eventView = new EventView(event, this);
        eventController = new EventController(event);

        // Set up the save button click listener
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            int spots = Integer.parseInt(spotsEditText.getText().toString());
            double cost = Double.parseDouble(costEditText.getText().toString());

            eventController.updateTitle(title);
            eventController.updateDescription(description);
            eventController.updateNumberOfSpots(spots);
            eventController.updateCost(cost);
            eventController.saveEventToFirestore();
        });

        return view;
    }

    // Inside onCreateView() after initializing costEditText
    costEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            String input = s.toString();
            // Check if the input has more than 2 decimal places
            if (input.contains(".")) {
                int decimalIndex = input.indexOf(".");
                if (input.length() - decimalIndex > 3) {
                    s.delete(decimalIndex + 3, input.length());
                }
            }
        }
    });

    public void showEventDetails(EventModel event) {
        titleEditText.setText(event.getTitle());
        descriptionEditText.setText(event.getDescription());
        spotsEditText.setText(String.valueOf(event.getNumberOfSpots()));
        costEditText.setText(String.valueOf(event.getCost()));
    }
}
