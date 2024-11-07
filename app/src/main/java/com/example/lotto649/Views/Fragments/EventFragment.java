package com.example.lotto649.Views.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.lotto649.Controllers.EventController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.TextWatcher;
import android.app.DatePickerDialog;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class EventFragment extends Fragment {
    private EventController eventController;
    private EventModel event;

    private TextInputLayout titleInputLayout, descriptionInputLayout, lotteryStartDateFieldLayout, lotteryEndDateFieldLayout, spotsInputLayout, maxEntrantsInputLayout, costInputLayout;
    private TextInputEditText titleEditText, descriptionEditText, lotteryStartDateFieldText, lotteryEndDateFieldText, spotsEditText, maxEntrantsEditText, costEditText;
    private CheckBox geoCheck;
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
        View image = view.findViewById(R.id.eventPoster);
        titleInputLayout = view.findViewById(R.id.eventTitle);
        descriptionInputLayout = view.findViewById(R.id.eventDescription);
        lotteryStartDateFieldLayout = view.findViewById(R.id.eventLotteryStartDate);
        lotteryEndDateFieldLayout = view.findViewById(R.id.eventLotteryEndDate);
        spotsInputLayout = view.findViewById(R.id.eventSpots);
        maxEntrantsInputLayout = view.findViewById(R.id.eventMaxEntrants);
        costInputLayout = view.findViewById(R.id.eventCost);
        geoCheck = view.findViewById(R.id.eventGeolocation);

        // Make the fields non-editable (only clickable to show date picker)
        lotteryStartDateFieldLayout.setFocusable(false);
        lotteryEndDateFieldLayout.setFocusable(false);
        lotteryStartDateFieldLayout.setClickable(true);
        lotteryEndDateFieldLayout.setClickable(true);

        // Initialize UI edits
        titleEditText = (TextInputEditText) titleInputLayout.getEditText();
        descriptionEditText = (TextInputEditText) descriptionInputLayout.getEditText();
        lotteryStartDateFieldText = (TextInputEditText) lotteryStartDateFieldLayout.getEditText();
        lotteryEndDateFieldText = (TextInputEditText) lotteryEndDateFieldLayout.getEditText();
        spotsEditText = (TextInputEditText) spotsInputLayout.getEditText();
        maxEntrantsEditText = (TextInputEditText) maxEntrantsInputLayout.getEditText();
        costEditText = (TextInputEditText) costInputLayout.getEditText();
        cancelButton = view.findViewById(R.id.cancelButton);
        saveButton = view.findViewById(R.id.saveButton);

        AtomicReference<Date> startDate = new AtomicReference<>(new Date());
        AtomicReference<Date> endDate = new AtomicReference<>(new Date());
        lotteryStartDateFieldText.setOnClickListener(v -> {
            showDatePickerDialog(lotteryStartDateFieldText, startDate, startDate.get());
        });
        lotteryEndDateFieldText.setOnClickListener(v -> {
            showDatePickerDialog(lotteryEndDateFieldText, endDate, startDate.get());
        });

        // Inside onCreateView() after initializing costEditText
        costEditText.addTextChangedListener(costEditWatcher);
        // Initialize MVC components
        event = new EventModel(requireContext(), FirebaseFirestore.getInstance());

        eventController = new EventController(event);

        // Set up the cancel button click listener
        cancelButton.setOnClickListener(v -> {
            eventController.removeEventFromFirestore();

        });

        // Set up the save button click listener
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String spotsStr = spotsEditText.getText().toString();
            String maxEntrantsStr = maxEntrantsEditText.getText().toString();
            String costStr = costEditText.getText().toString();
            boolean geo = geoCheck.isChecked();

            int spots;
            int maxEntrants = -1;
            double cost = 0.00;

            if (title.isBlank()) {
                titleInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                return;
            }
            if (description.isBlank()) {
                descriptionInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                return;
            }
            if (lotteryStartDateFieldText.getText().toString().isBlank()) {
                lotteryStartDateFieldLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                return;
            }
            if (lotteryEndDateFieldText.getText().toString().isBlank()) {
                lotteryEndDateFieldLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                return;
            }
            if (startDate.get().before(new Date())) {
                Toast.makeText(requireContext(), "Start date can't be in the past.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!endDate.get().equals(startDate.get()) && endDate.get().before(startDate.get())) {
                Toast.makeText(requireContext(), "End date must be greater than or equal to start date.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (spotsStr.isBlank()) {
                spotsInputLayout.setHelperTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                return;
            } else {
                spots = Integer.parseInt(spotsStr);
            }
            if (!maxEntrantsStr.isBlank()) {
                maxEntrants = Integer.parseInt(maxEntrantsStr);
            }
            if (!costStr.isBlank()) {
                cost = Double.parseDouble(costStr);
            }

            eventController.updateTitle(title);
            eventController.updateDescription(description);
            eventController.updateNumberOfSpots(spots);
            eventController.updateNumberOfMaxEntrants(maxEntrants);
            eventController.updateStartDate(startDate.get());
            eventController.updateEndDate(endDate.get());
            eventController.updateCost(cost);
            eventController.updateGeo(geo);
            eventController.saveEventToFirestore();
        });

        return view;
    }

    private void showDatePickerDialog(EditText dateToPick, AtomicReference<Date> dateReference, Date startdate) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(startdate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Create a Calendar instance for the selected date
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    // Format the selected date and set it to the EditText
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    dateToPick.setText(selectedDate);

                    // Update the date reference with the selected date
                    dateReference.set(selectedCalendar.getTime());
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
