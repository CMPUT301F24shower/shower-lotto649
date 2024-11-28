package com.example.lotto649.Views.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrganizerEventFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String firestoreEventId;
    private ImageView posterImage;
    TextView name;
    TextView status;
    TextView location;
    TextView spotsAvail;
    TextView numAttendees;
    TextView daysLeft;
    TextView geoLocation;
    TextView description;
    ExtendedFloatingActionButton viewQrCodeButton;
    ExtendedFloatingActionButton chooseWinnersButton;
    ExtendedFloatingActionButton viewEntrantsMapButton;
    ExtendedFloatingActionButton viewEntrantsWaitingListButton;
    ExtendedFloatingActionButton editButton;
    private Uri posterUri;

    public OrganizerEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firestoreEventId = getArguments().getString("firestoreEventId");

        View view = inflater.inflate(R.layout.fragment_organizer_view_event, container, false);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        name = view.findViewById(R.id.eventTitle);
        status = view.findViewById(R.id.eventStatus);
        location = view.findViewById(R.id.eventLocation);
        spotsAvail = view.findViewById(R.id.eventAvailableSpots);
        daysLeft = view.findViewById(R.id.eventDaysLeft);
        geoLocation = view.findViewById(R.id.geolocationRequirement);
        description = view.findViewById(R.id.eventDescription);
        viewQrCodeButton = view.findViewById(R.id.view_qr_code_button);
        chooseWinnersButton = view.findViewById(R.id.choose_winners_button);
        viewEntrantsMapButton = view.findViewById(R.id.view_entrants_map_button);
        posterImage = view.findViewById(R.id.list_event_poster);
        viewEntrantsWaitingListButton = view.findViewById(R.id.view_entrants_list_button);
        editButton = view.findViewById(R.id.edit_event_button);

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
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Date startDate = doc.getDate("startDate");
                            Date endDate = doc.getDate("endDate");

                            if (startDate != null && endDate != null) {
                                // Calculate the difference in milliseconds
                                long diffInMillis = endDate.getTime() - startDate.getTime();
                                // Convert milliseconds to days (rounding down)
                                int daysLeftInt = (int) (diffInMillis / (24 * 60 * 60 * 1000));

                                String daysLeftText = Integer.toString(daysLeftInt);

                                Boolean isGeo = doc.getBoolean("geo");
                                String geoLocationText = isGeo ? "Requires GeoLocation Tracking" : "";
                                String descriptionText = doc.getString("description");


                                name.setText(nameText);
                                // TODO: set to actual event status
                                status.setText("OPEN");
                                // TODO: set to actual location
                                location.setText("LOCATION");
                                spotsAvail.setText(spotsAvailText);
                                daysLeft.setText(daysLeftText);
                                if (isGeo) {
                                    geoLocation.setVisibility(View.VISIBLE);
                                    geoLocation.setText(geoLocationText);
                                } else {
                                    geoLocation.setVisibility(View.GONE);
                                }
                                description.setText(descriptionText);
                            }
                        }
                    }
                });


        viewEntrantsMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("eventId", firestoreEventId);
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(mapFragment);
            }
        });
        return view;
    }
}