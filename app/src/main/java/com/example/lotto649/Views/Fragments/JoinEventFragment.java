/**
 * A fragment to display a given event's information.
 * This is used by an admin user to manage an event.
 * This fragment is reached through a list of events in the admin view.
 */
package com.example.lotto649.Views.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.lotto649.FirestoreHelper;
import com.example.lotto649.LocationManagerSingleton;
import com.example.lotto649.MainActivity;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A fragment to display a given event's information.
 * This is used by an admin user to manage an event.
 * This fragment is reached through a list of events in the admin view.
 */
public class JoinEventFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String firestoreEventId;
    private ImageView posterImage;
    TextView name, status, location, spotsAvail, numAttendees, dates, geoLocation, description;
    Button joinButton, unjoinButton;
    ExtendedFloatingActionButton backButton;
    private Uri posterUri;
    private MutableLiveData<Boolean> imageAbleToBeDeleted, qrCodeAbleToBeDeleted;
    private Date startDate, endDate;
    private int curNum;
    private boolean geoRequired;
    String deviceId;
    boolean isWinnerMode;

    /**
     * Public empty constructor for BrowseEventsFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public JoinEventFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     * This method inflates the layout defined in `fragment_browse_events.xml`.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the camera fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get info from bundle
        firestoreEventId = getArguments().getString("firestoreEventId");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_join_event, container, false);
        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        geoRequired = false;

        name = view.findViewById(R.id.admin_event_name);
        status = view.findViewById(R.id.admin_event_status);
        location = view.findViewById(R.id.admin_event_location);
        spotsAvail = view.findViewById(R.id.admin_event_spots);
        numAttendees = view.findViewById(R.id.admin_event_attendees);
        dates = view.findViewById(R.id.admin_event_dates);
        geoLocation = view.findViewById(R.id.admin_event_geo);
        description = view.findViewById(R.id.admin_event_description);
        posterImage = view.findViewById(R.id.admin_event_poster);
        backButton = view.findViewById(R.id.back_button);

        joinButton = view.findViewById(R.id.join_event_wait_list);
        unjoinButton = view.findViewById(R.id.unjoin_event_wait_list);
        isWinnerMode = false;

        eventsRef.document(firestoreEventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc == null || !doc.exists()) {
                                MyApp.getInstance().popFragment();
                            } else {
                                db.collection("winners").document(firestoreEventId + "_" + deviceId).get().addOnCompleteListener(newTask -> {
                                    if (newTask.isSuccessful()) {
                                        DocumentSnapshot document = newTask.getResult();
                                        if (document != null && document.exists()) {
                                            joinButton.setText("Accept & Sign Up");
                                            unjoinButton.setText("Decline");
                                            isWinnerMode = true;
                                            joinButton.setVisibility(View.VISIBLE);
                                            unjoinButton.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                db.collection("enrolled").document(firestoreEventId + "_" + deviceId).get().addOnCompleteListener(newTask -> {
                                    if (newTask.isSuccessful()) {
                                        DocumentSnapshot document = newTask.getResult();
                                        if (document != null && document.exists()) {
                                            joinButton.setVisibility(View.GONE);
                                            unjoinButton.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                // TODO check same thing in enrolled, cancelled ...
                                String nameText = doc.getString("title");
                                Long maxEntrants = (Long) doc.get("numberOfMaxEntrants");
                                int maxNum = 0;
                                if (maxEntrants != null)
                                    maxNum = (maxEntrants).intValue();

                                FirestoreHelper.getInstance().getWaitlistSize(firestoreEventId);
                                curNum = FirestoreHelper.getInstance().getCurrWaitlistSize().getValue();
                                String spotsAvailText;
                                if (maxNum == -1) {
                                    spotsAvailText = "OPEN";
                                } else if (maxNum <= curNum) {
                                    spotsAvailText = "FULL";
                                    joinButton.setVisibility(View.GONE);
                                } else {
                                    // TODO please fix this
//                                spotsAvailText = "TODO";
//                                spotsAvailText = Integer.toString(maxNum - ((List<String>) doc.get("waitingList")).size()) + " Spots Available";
                                    spotsAvailText = Integer.toString(maxNum - curNum) + " Spots Available";
                                }
                                Long numSpots = doc.getLong("numberOfSpots");
                                String numAttendeesText;
                                if (numSpots == null) {
                                    numAttendeesText = "Unknown number of Attendees";
                                } else {
                                    numAttendeesText = numSpots + " Attendees";
                                }
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                startDate = doc.getDate("startDate");
                                endDate = doc.getDate("endDate");
                                String datesText;
                                if (startDate != null && endDate != null) {
                                    datesText = "Enter between " + df.format(startDate) + " - " + df.format(endDate);
                                } else {
                                    datesText = "Error finding dates";
                                }
                                Boolean isGeo = doc.getBoolean("geo");
                                String geoLocationText = Boolean.TRUE.equals(isGeo) ? "Requires GeoLocation Tracking" : "";
                                geoRequired = Boolean.TRUE.equals(isGeo);
                                String descriptionText = doc.getString("description");
                                name.setText(nameText);
                                // TODO: set to actual event status
                                status.setText("OPEN");
                                // TODO: set to actual location
                                location.setText("LOCATION");
                                spotsAvail.setText(spotsAvailText);
                                numAttendees.setText(numAttendeesText);
                                dates.setText(datesText);
                                if (Boolean.TRUE.equals(isGeo)) {
                                    geoLocation.setVisibility(View.VISIBLE);
                                    geoLocation.setText(geoLocationText);
                                } else {
                                    geoLocation.setVisibility(View.GONE);
                                }
                                description.setText(descriptionText);

                                //     poster
                                String posterUriString = doc.getString("posterImage");
                                if (!Objects.equals(posterUriString, "")) {
                                    posterUri = Uri.parse(posterUriString);
                                    StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(posterUriString);
                                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        posterUri = uri;
                                        if (isAdded()) {
                                            Glide.with(getContext())
                                                    .load(uri)
                                                    .into(posterImage);
                                        }
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(900, 450);
                                        posterImage.setLayoutParams(layoutParams);
                                        posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    });
                                } else {
                                    posterUri = null;
                                }
                            }
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWinnerMode) {
                    // Accepted
                    // TODO Delete this entry, create same entry in enrolled
                    db.collection("winners")
                            .document(firestoreEventId + "_" + deviceId)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    DocumentSnapshot doc = task.getResult();
                                    Map<String, Object> data = doc.getData();
                                    if (data != null) {
                                        data.put("hasSeenNoti", false);
                                        db.collection("enrolled").document(firestoreEventId + "_" + deviceId).set(data);
                                        db.collection("winners")
                                                .document(firestoreEventId + "_" + deviceId)
                                                .delete();
                                    }
                                }
                            });

                    MyApp.getInstance().popFragment();
                } else {
                    DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(deviceId);
                    doc.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // TODO do we need entrant to be saved at all
                            if (!document.exists() || !document.getBoolean("entrant")) {
                                MyApp.getInstance().addFragmentToStack(new CreateAccountFragment());
                            } else if (document.exists()) {
                                // Perform signup
                                Date currentDate = new Date();
                                if (currentDate.before(startDate)) {
                                    Toast.makeText(getContext(), "Sorry this event is not accepting signups yet.", Toast.LENGTH_SHORT).show();
                                } else if (currentDate.after(endDate)) {
                                    Toast.makeText(getContext(), "Sorry this event has closed for new signups.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> signUp = new HashMap<>();
                                    signUp.put("eventId", firestoreEventId);

                                    signUp.put("userId", deviceId);
                                    signUp.put("timestamp", FieldValue.serverTimestamp());
                                    signUp.put("eventDeleted", false);
                                    signUp.put("lottoStatus", "Waiting");

                                    // TODO add geolocation data here
                                    if (geoRequired) {
                                        ((MainActivity) getActivity()).getUserLocation(getContext());
                                    }
                                    boolean isLocationEnabled = LocationManagerSingleton.getInstance().isLocationTrackingEnabled();
                                    if (isLocationEnabled) {
                                        // Proceed with location-based functionality
                                        GeoPoint currLocation = LocationManagerSingleton.getInstance().getGeoPoint();
                                        signUp.put("longitude", Double.toString(currLocation.getLongitude()));
                                        signUp.put("latitude", Double.toString(currLocation.getLatitude()));
                                    } else {
                                        // Prompt the user to enable location tracking
                                        if (geoRequired) {
                                            return;
                                        }
                                        // geo not required, just put blank strings
                                        signUp.put("longitude", "");
                                        signUp.put("latitude", "");
                                    }
                                    db.collection("signUps").document(firestoreEventId + "_" + deviceId).set(signUp).addOnSuccessListener(listener -> {
                                        // TODO set flags for entrant state, (in list, chosen, waiting for response...)
                                        joinButton.setVisibility(View.GONE);
                                        unjoinButton.setVisibility(View.VISIBLE);
                                        // TODO make sure this event is added to home screen
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        unjoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWinnerMode) {
                    // Decline
                    // TODO Delete this entry, create same entry in cancelled
                    db.collection("winners")
                            .document(firestoreEventId + "_" + deviceId)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    DocumentSnapshot doc = task.getResult();
                                    Map<String, Object> data = doc.getData();
                                    if (data != null) {
                                        data.put("hasSeenNoti", true);
                                        db.collection("cancelled").document(firestoreEventId + "_" + deviceId).set(data);
                                        db.collection("winners")
                                                .document(firestoreEventId + "_" + deviceId)
                                                .delete();
                                        db.collection("signUps")
                                                .document(firestoreEventId + "_" + deviceId)
                                                .delete();
                                    }
                                }
                            });
                    MyApp.getInstance().popFragment();
                } else {
                    db.collection("signUps").document(firestoreEventId + "_" + deviceId).delete().addOnSuccessListener(listener -> {
                        joinButton.setVisibility(View.VISIBLE);
                        unjoinButton.setVisibility(View.GONE);
                    });
                    db.collection("notSelected")
                            .document(firestoreEventId + "_" + deviceId)
                            .delete();
                }
            }
        });

        String signUpId = firestoreEventId + "_" + Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        db.collection("signUps").document(signUpId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        joinButton.setVisibility(View.GONE);
                        unjoinButton.setVisibility(View.VISIBLE);
                    } else {
                        joinButton.setVisibility(View.VISIBLE);
                        unjoinButton.setVisibility(View.GONE);
                    }
                });
        return view;
    }
}
