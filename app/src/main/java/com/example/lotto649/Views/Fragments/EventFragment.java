package com.example.lotto649.Views.Fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.lotto649.FirebaseStorageHelper.uploadPosterImageToFirebaseStorage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.lotto649.Controllers.EventController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.QrCodeModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fragment for displaying and editing details of an event.
 * Allows users to view, modify, and save event information to Firestore.
 */
public class EventFragment extends Fragment {
    private EventController eventController;
    private EventModel event;
    private Context mContext;

    private TextInputLayout titleInputLayout, descriptionInputLayout, lotteryStartDateFieldLayout, lotteryEndDateFieldLayout, spotsInputLayout, maxEntrantsInputLayout;
    private TextInputEditText titleEditText, descriptionEditText, lotteryStartDateFieldText, lotteryEndDateFieldText, spotsEditText, maxEntrantsEditText;
    private String initialTitle, initialDescription, initialStartDate, initialEndDate, initialAttendees, initialMaxEntrants;
    private CheckBox geoCheck;
    private ExtendedFloatingActionButton cancelButton, saveButton;

    private AtomicReference<Date> startDate = new AtomicReference<>(new Date());
    private AtomicReference<Date> endDate = new AtomicReference<>(new Date());
    private boolean isAddingFirstTime;

    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean hasSetImage;
    ImageView posterImage;
    ImageView defaultImage;
    Uri currentImageUri;
    private AtomicReference<String> currentImageUriString;
    private MutableLiveData<Boolean> posterLoadedInFirestore;
    private MutableLiveData<Boolean> saveButtonShow;

    /**
     * Displays details of the provided EventModel in the UI components.
     *
     * @param event The EventModel containing event details to display.
     */
    public void showEventDetails(@NonNull EventModel event) {
        if (isAddingFirstTime) {
            titleEditText.setText("");
            descriptionEditText.setText("");
            lotteryStartDateFieldText.setText("");
            startDate.set(new Date());
            lotteryEndDateFieldText.setText("");
            endDate.set(new Date());
            spotsEditText.setText("");
            maxEntrantsEditText.setText("");
            geoCheck.setChecked(false);
        } else {
            titleEditText.setText(event.getTitle());
            descriptionEditText.setText(event.getDescription());
            Date sd = event.getStartDate();
            String sdHourText = String.format("%02d", sd.getHours());
            String sdMinuteText = String.format("%02d", sd.getMinutes());
            lotteryStartDateFieldText.setText(
                    (sd.getYear() + 1900) + "-" + (sd.getMonth() + 1) + "-" + sd.getDate() + " (" + sdHourText + ":" + sdMinuteText + " MST)"
            );
            startDate.set(sd);
            Date ed = event.getEndDate();
            String edHourText = String.format("%02d", ed.getHours());
            String edMinuteText = String.format("%02d", ed.getMinutes());
            lotteryEndDateFieldText.setText(
                    (ed.getYear() + 1900) + "-" + (ed.getMonth() + 1) + "-" + ed.getDate() + " (" + edHourText + ":" + edMinuteText + " MST)"
            );
            endDate.set(ed);
            spotsEditText.setText(String.valueOf(event.getNumberOfSpots()));
            if (event.getNumberOfMaxEntrants() == -1) {
                maxEntrantsEditText.setText("");
            } else {
                maxEntrantsEditText.setText(String.valueOf(event.getNumberOfMaxEntrants()));
            }
            geoCheck.setChecked(event.getGeo());
        }
    }

    public void setInitialValues() {
        initialTitle = titleEditText.getText().toString();
        initialDescription = descriptionEditText.getText().toString();
        initialStartDate = lotteryStartDateFieldText.getText().toString();
        initialEndDate = lotteryEndDateFieldText.getText().toString();
        initialAttendees = spotsEditText.getText().toString();
        initialMaxEntrants = maxEntrantsEditText.getText().toString();
    }

    /**
     * Called when the fragment is attached to a context. Initializes the event model if null.
     *
     * @param context The context to which the fragment is attached.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("Ohm", "onAttach");
        if (Objects.isNull(event)) {
            this.event = new EventModel(FirebaseFirestore.getInstance());
        }
        mContext = context;
    }

    /**
     * Default constructor, indicating that this is a new event.
     */
    public EventFragment() {
        Log.e("Ohm", "Construct");
        isAddingFirstTime = true;
    }

    /**
     * Constructor to edit an existing event.
     *
     * @param event The existing EventModel to be edited.
     */
    public EventFragment(EventModel event) {
        Log.e("Ohm", "Contruct Event");
        isAddingFirstTime = false;
        this.event = event;
    }

    private void getPosterFromFirebase() {
        StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(currentImageUriString.get());
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            currentImageUri = uri;
            currentImageUriString.set(currentImageUri.toString());
            Glide.with(mContext)
                    .load(uri)
                    .into(posterImage);
            defaultImage.setVisibility(View.GONE);
            posterImage.setVisibility(View.VISIBLE);
            hasSetImage = true;
        }).addOnFailureListener(e -> {
            defaultImage.setVisibility(View.VISIBLE);
            posterImage.setVisibility(View.GONE);
            hasSetImage = false;
            Toast.makeText(getActivity(), "Unable to fetch profile image", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_event, container, false);
        currentImageUriString = new AtomicReference<String>("");
        posterLoadedInFirestore = new MutableLiveData<Boolean>(Boolean.FALSE);
        posterLoadedInFirestore.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (Objects.equals(changedValue, Boolean.TRUE)) {
                    getPosterFromFirebase();
                } else {
                    defaultImage.setVisibility(View.VISIBLE);
                    posterImage.setVisibility(View.GONE);
                    hasSetImage = false;
                }
            }
        });

        hasSetImage = false;
        currentImageUri = null;
        titleInputLayout = view.findViewById(R.id.eventTitle);
        descriptionInputLayout = view.findViewById(R.id.eventDescription);
        lotteryStartDateFieldLayout = view.findViewById(R.id.eventLotteryStartDate);
        lotteryEndDateFieldLayout = view.findViewById(R.id.eventLotteryEndDate);
        spotsInputLayout = view.findViewById(R.id.eventSpots);
        maxEntrantsInputLayout = view.findViewById(R.id.eventMaxEntrants);
        geoCheck = view.findViewById(R.id.eventGeolocation);

        posterImage = view.findViewById(R.id.event_poster);
        defaultImage = view.findViewById(R.id.event_poster_placeholder);
        posterImage.setVisibility(View.GONE);
        defaultImage.setVisibility(View.VISIBLE);

        // TODO: This is hardcoded, but works good on my phone, not sure if this is a good idea or not
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(750, 450);
        posterImage.setLayoutParams(layoutParams);
        posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        defaultImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posterImage.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                if (!hasSetImage) {
                    defaultImage.setVisibility(View.GONE);
                }
            }
        });

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
        cancelButton = view.findViewById(R.id.cancelButton);
        saveButton = view.findViewById(R.id.saveButton);

        if (!isAddingFirstTime) {
            saveButton.setText("Save");
            ((TextView) view.findViewById(R.id.eventFragment)).setText("Edit Event");
        }

        currentImageUriString.set(event.getPosterImage());
        // Update profile Image
        if (!Objects.equals(currentImageUriString.get(), "")) {
            getPosterFromFirebase();
        }

        saveButtonShow = new MutableLiveData<Boolean>(Boolean.FALSE);
        saveButtonShow.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (isAddingFirstTime) {
                    saveButton.setVisibility(View.VISIBLE);
                } else {
                    if (Objects.equals(changedValue, Boolean.TRUE)) {
                        saveButton.setVisibility(View.VISIBLE);
                    } else {
                        saveButton.setVisibility(View.GONE);
                    }
                }
            }
        });

        showEventDetails(event);
        setInitialValues();

        titleEditText.addTextChangedListener(titleWatcher);
        descriptionEditText.addTextChangedListener(descriptionWatcher);
        lotteryStartDateFieldText.addTextChangedListener(startDateWatcher);
        lotteryEndDateFieldText.addTextChangedListener(endDateWatcher);
        spotsEditText.addTextChangedListener(attendeesWatcher);
        maxEntrantsEditText.addTextChangedListener(maxSizeWatcher);

        lotteryStartDateFieldText.setOnClickListener(v -> {
            showDatePickerDialog(lotteryStartDateFieldText, startDate, startDate.get());
        });
        lotteryEndDateFieldText.setOnClickListener(v -> {
            showDatePickerDialog(lotteryEndDateFieldText, endDate, startDate.get());
        });

        eventController = new EventController(event);


        // Set up the cancel button click listener
        cancelButton.setOnClickListener(v -> {
            MyApp.getInstance().popFragment();
        });

        // Set up the save button click listener
        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String spotsStr = spotsEditText.getText().toString();
            String maxEntrantsStr = maxEntrantsEditText.getText().toString();
            boolean geo = geoCheck.isChecked();

            int spots = 0;
            int maxEntrants = -1;

            boolean hasError = false;

            if (title.isBlank()) {
                titleInputLayout.setError("Please enter your event title");
                hasError = true;
            } else {
                titleInputLayout.setError(null);
            }
            if (description.isBlank()) {
                descriptionInputLayout.setError("Please enter your event description");
                hasError = true;
            } else {
                descriptionInputLayout.setError(null);
            }
            if (lotteryStartDateFieldText.getText().toString().isBlank()) {
                lotteryStartDateFieldLayout.setError("Please enter your event lottery start date");
                hasError = true;
            } else {
                lotteryStartDateFieldLayout.setError(null);
            }
            if (lotteryEndDateFieldText.getText().toString().isBlank()) {
                lotteryEndDateFieldLayout.setError("Please enter your event lottery end date");
                hasError = true;
            } else if (!endDate.get().equals(startDate.get()) && endDate.get().before(startDate.get())) {
                lotteryEndDateFieldLayout.setError("End date must be after start date");
                hasError = true;
            } else if (endDate.get().before(new Date())) {
                lotteryStartDateFieldLayout.setError("End date can't be in the past");
                hasError = true;
            } else {
                lotteryEndDateFieldLayout.setError(null);
            }
            if (spotsStr.isBlank()) {
                spotsInputLayout.setError("Please enter valid number of attendees of your event");
                hasError = true;
            } else if (spotsStr.equals("0")){
                spotsInputLayout.setError("Please enter a positive number");
                hasError = true;
            } else {
                spots = Integer.parseInt(spotsStr);
                spotsInputLayout.setError(null);
            }
            if (maxEntrantsStr.equals("0")){
                maxEntrantsInputLayout.setError("Please enter a positive number");
                hasError = true;
            } else if (!maxEntrantsStr.isBlank()) {
                if (Integer.parseInt(maxEntrantsStr) < Integer.parseInt(spotsStr)) {
                    maxEntrantsInputLayout.setError("Waiting List Size must be at least as large as number of possible attendees.");
                    hasError = true;
                }
                else {
                    maxEntrants = Integer.parseInt(maxEntrantsStr);
                }
            }

            if (hasError) {
                return;
            }

            eventController.updateTitle(title);
            eventController.updateDescription(description);
            eventController.updateNumberOfSpots(spots);
            eventController.updateNumberOfMaxEntrants(maxEntrants);
            eventController.updateStartDate(startDate.get());
            eventController.updateEndDate(endDate.get());
            eventController.updateGeo(geo);
            String fileName = event.getEventId() + ".jpg";
            uploadPosterImageToFirebaseStorage(currentImageUri, fileName, currentImageUriString, posterLoadedInFirestore);

            if (currentImageUriString.get().isEmpty()) {
                eventController.updatePoster("");
            } else {
                eventController.updatePoster(currentImageUriString.get());
            }

            if (isAddingFirstTime) {
                eventController.saveEventToFirestore(eventId -> {
                    String data = "https://lotto649/?eventId=" + eventId;
                    Log.e("GDEEP", data);
                    Bitmap qrCodeBitmap = QrCodeModel.generateForEvent(data);
                    String qrCodeHash = QrCodeModel.generateHash(data);

                    eventController.updateQrCode(qrCodeHash);

                    QrFragment qrFragment = QrFragment.newInstance(qrCodeBitmap);
                    MyApp.getInstance().addFragmentToStack(qrFragment);
                });
            } else {
                MyApp.getInstance().popFragment();
            }
            setInitialValues();
            isAddingFirstTime = false;
        });

        return view;
    }

    /**
     * Displays a date picker dialog and updates the selected date in the specified EditText field.
     *
     * @param dateToPick   The EditText field to update with the selected date.
     * @param dateReference The AtomicReference to hold the selected date.
     * @param startdate     The starting date for the date picker.
     */
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

                    timePicker(dateToPick, dateReference, selectedYear, selectedMonth, selectedDay);
                }, year, month, day);

        datePickerDialog.show();
    }

    // https://stackoverflow.com/questions/38604157/android-date-time-picker-in-one-dialog
    private void timePicker(EditText dateToPick, AtomicReference<Date> dateReference, int year, int month, int day) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfDay) -> {
            String hourText = String.format("%02d", hourOfDay);
            String minuteText = String.format("%02d", minuteOfDay);

            dateToPick.setText(dateToPick.getText().toString() + " (" + hourText + ":" + minuteText + " MST)");

            // Update the date reference with the selected date
                Calendar setCal = Calendar.getInstance();
                setCal.set(Calendar.YEAR, year);
                setCal.set(Calendar.MONTH, month);
                setCal.set(Calendar.DAY_OF_MONTH, day);
                setCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                setCal.set(Calendar.MINUTE, minuteOfDay);
                dateReference.set(setCal.getTime());
            }, hour, minute, true);
        timePickerDialog.show();
    }


    private boolean DidInfoRemainConstant() {
        return Objects.equals(titleEditText.getEditableText().toString(), initialTitle)
                && Objects.equals(descriptionEditText.getEditableText().toString(), initialDescription)
                && Objects.equals(lotteryStartDateFieldText.getEditableText().toString(), initialStartDate)
                && Objects.equals(lotteryEndDateFieldText.getEditableText().toString(), initialEndDate)
                && Objects.equals(spotsEditText.getEditableText().toString(), initialAttendees)
                && Objects.equals(maxEntrantsEditText.getEditableText().toString(), initialMaxEntrants);
    }

    private final TextWatcher titleWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    private final TextWatcher descriptionWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    private final TextWatcher startDateWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    private final TextWatcher endDateWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    private final TextWatcher attendeesWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    private final TextWatcher maxSizeWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };


    private void SetSaveButtonVisibility(boolean isEqual) {
        if (isEqual) {
            saveButtonShow.setValue(Boolean.FALSE);
            titleInputLayout.setError(null);
            descriptionInputLayout.setError(null);
            lotteryStartDateFieldLayout.setError(null);
            lotteryEndDateFieldLayout.setError(null);
            spotsInputLayout.setError(null);
            maxEntrantsInputLayout.setError(null);
        } else {
            saveButtonShow.setValue(Boolean.TRUE);
        }
    }


    /**
     * Handles the result of an activity that was started for a result, specifically for picking an image.
     *
     * <p>This method is called when the user selects an image from the gallery or other image sources.
     * If the image selection is successful, the selected image's URI is set to the {@code profileImage}
     * view, and the save button's color is updated to indicate the image has been selected.</p>
     *
     * @param requestCode The request code that was passed to the activity when it was started.
     * @param resultCode  The result code returned by the activity, indicating whether the operation was successful.
     * @param data        The intent containing the result data, which includes the URI of the selected image.
     *                    If the operation was successful, this will not be null and will contain the image URI.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            currentImageUri = data.getData();
            posterImage.setImageURI(currentImageUri);
            saveButtonShow.setValue(Boolean.TRUE);
        }
    }
}
