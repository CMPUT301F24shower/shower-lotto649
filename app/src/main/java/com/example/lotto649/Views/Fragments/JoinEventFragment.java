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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

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

        eventsRef.document(firestoreEventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            String nameText = doc.getString("title");
                            int maxNum = ((Long) doc.get("numberOfMaxEntrants")).intValue();
                            String spotsAvailText;
                            if (maxNum == -1) {
                                spotsAvailText = "OPEN";
                            } else {
                                spotsAvailText = Integer.toString(maxNum - ((List<String>) doc.get("waitingList")).size()) + " Spots Available";
                            }
                            String numAttendeesText = Integer.toString(((Long) doc.get("numberOfSpots")).intValue()) + " Attendees";
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Date startDate = doc.getDate("startDate");
                            Date endDate = doc.getDate("endDate");
                            String datesText = "Enter between " + df.format(startDate) + " - " + df.format(endDate);
                            Boolean isGeo = doc.getBoolean("geo");
                            String geoLocationText = isGeo ? "Requires GeoLocation Tracking" : "";
                            String descriptionText = doc.getString("description");
                            name.setText(nameText);
                            // TODO: set to actual event status
                            status.setText("OPEN");
                            // TODO: set to actual location
                            location.setText("LOCATION");
                            spotsAvail.setText(spotsAvailText);
                            numAttendees.setText(numAttendeesText);
                            dates.setText(datesText);
                            if (isGeo) {
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
                String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                Map<String, Object> signUp = new HashMap<>();
                signUp.put("eventId", firestoreEventId);
                signUp.put("userId", deviceId);
                signUp.put("timestamp", FieldValue.serverTimestamp());
                // TODO add geolocation data here
                db.collection("signUps").document(firestoreEventId+"_"+deviceId).set(signUp);
                // TODO this could maybe be on success
                joinButton.setVisibility(View.GONE);
                unjoinButton.setVisibility(View.VISIBLE);
            }
        });

        unjoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                // TODO add geolocation data here
                db.collection("signUps").document(firestoreEventId+"_"+deviceId).delete();
                joinButton.setVisibility(View.VISIBLE);
                unjoinButton.setVisibility(View.GONE);
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
