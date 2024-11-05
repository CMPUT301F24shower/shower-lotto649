package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.text.Editable;
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
import android.text.TextWatcher;
import android.app.DatePickerDialog;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

public class EventFragment extends Fragment {
    private EventView eventView;
    private EventController eventController;
    private EventModel event;

    private TextInputLayout titleInputLayout, descriptionInputLayout, lotteryStartDateField, lotteryEndDateField, spotsInputLayout, maxEntrantsInputLayout, costInputLayout;
    private TextInputEditText titleEditText, descriptionEditText, spotsEditText, maxEntrantsEditText, costEditText;
    private ExtendedFloatingActionButton cancelButton, saveButton;

    public void showEventDetails(@NonNull EventModel event) {
        titleEditText.setText(event.getTitle());
        descriptionEditText.setText(event.getDescription());
        spotsEditText.setText(String.valueOf(event.getNumberOfSpots()));
        maxEntrantsEditText.setText(String.valueOf(event.getNumberOfMaxEntrants()));
        costEditText.setText(String.valueOf(event.getCost()));
    }

    public EventFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // Initialize UI elements
        View image = view.findViewById(R.id.headerImage);
        titleInputLayout = view.findViewById(R.id.textFieldTitle);
        descriptionInputLayout = view.findViewById(R.id.textFieldDescription);
        lotteryStartDateField = view.findViewById(R.id.textFieldLotteryStartDate);
        lotteryEndDateField = view.findViewById(R.id.textFieldLotteryEndDate);
        spotsInputLayout = view.findViewById(R.id.textFieldSpots);
        maxEntrantsInputLayout = view.findViewById(R.id.textFieldMaxEntrants);
        costInputLayout = view.findViewById(R.id.textFieldCost);

        // Make the fields non-editable (only clickable to show date picker)
        lotteryStartDateField.setFocusable(false);
        lotteryEndDateField.setFocusable(false);

        // Initialize UI edits
        titleEditText = (TextInputEditText) titleInputLayout.getEditText();
        descriptionEditText = (TextInputEditText) descriptionInputLayout.getEditText();
        spotsEditText = (TextInputEditText) spotsInputLayout.getEditText();
        maxEntrantsEditText = (TextInputEditText) maxEntrantsInputLayout.getEditText();
        costEditText = (TextInputEditText) costInputLayout.getEditText();
        cancelButton = view.findViewById(R.id.cancelButton);
        saveButton = view.findViewById(R.id.saveButton);

        // Inside onCreateView() after initializing costEditText
        costEditText.addTextChangedListener(costEditWatcher);
        // Initialize MVC components
        event = new EventModel(getContext(), FirebaseFirestore.getInstance());

        lotteryStartDateField.setOnClickListener(v -> showDatePickerDialog(lotteryStartDateField.getEditText()));
        lotteryEndDateField.setOnClickListener(v -> showDatePickerDialog(lotteryEndDateField.getEditText()));


        eventView = new EventView(event, this);
        eventController = new EventController(event);

        // Set up the cancel button click listener
        cancelButton.setOnClickListener(v -> {

            eventController.removeEventFromFirestore();
        });
        
        // Set up the save button click listener
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            int spots = Integer.parseInt(spotsEditText.getText().toString());
            int maxEntrants = Integer.parseInt(maxEntrantsEditText.getText().toString());
            double cost = Double.parseDouble(costEditText.getText().toString());
            //Date startDate = Date.parse(lotteryStartDateField.getEditText().toString());
            //Date endDate = Date.parse(lotteryEndDateField.getEditText().toString());

            eventController.updateTitle(title);
            eventController.updateDescription(description);
            eventController.updateNumberOfSpots(spots);
            eventController.updateNumberOfMaxEntrants(maxEntrants);
            //eventController.updateStartDate(startDate);
            //eventController.updateEndDate(endDate);
            eventController.updateCost(cost);
            eventController.saveEventToFirestore();
        });

        return view;
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date and set it in the EditText
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    editText.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private TextWatcher costEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override

        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable editable) {
            String input = editable.toString();
            // Check if the input has more than 2 decimal places
            if (input.contains(".")) {
                int decimalIndex = input.indexOf(".");
                if (input.length() - decimalIndex > 3) {
                    editable.delete(decimalIndex + 3, input.length());
                }
            }
        }
    };
}
