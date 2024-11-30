package com.example.lotto649.Views.Fragments;

import android.graphics.Bitmap;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.lotto649.EventState;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.QrCodeModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrganizerEventFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String firestoreEventId;
    private ImageView posterImage;
    private String eventId;
    private int numberOfSpots;
    private EventModel event;
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
    ExtendedFloatingActionButton backButton;
    private MutableLiveData<Boolean> hasQrCode;
    private MutableLiveData<Boolean> hasDrawn;
    private Uri posterUri;

    public OrganizerEventFragment() {
        // Required empty public constructor
    }

    public EventModel getEventFromFirebaseObject(DocumentSnapshot doc) {
        String eventId = doc.getId();
        String description = doc.getString("description");
        Date endDate = doc.getDate("endDate");
        Boolean geo = doc.getBoolean("geo");
        int numMaxEntrants = doc.getLong("numberOfMaxEntrants").intValue();
        int numSpots = doc.getLong("numberOfSpots").intValue();
        String organizerId = doc.getString("organizerId");
        String posterImage = doc.getString("posterImage");
        String qrCode = doc.getString("qrCode");
        Date startDate = doc.getDate("startDate");
        String stateStr = doc.getString("state");
        EventState state = EventState.OPEN;
        if (stateStr.equals("WAITING")) {
            state = EventState.WAITING;
        } else if (stateStr.equals("CLOSED")) {
            state = EventState.CLOSED;
        }
        String title = doc.getString("title");
        int waitingListSize = doc.getLong("waitingListSize").intValue();
        EventModel newEvent = new EventModel(title, description, numSpots, numMaxEntrants, startDate, endDate, posterImage, geo, qrCode, waitingListSize, state, db);
        newEvent.setOrganizerId(organizerId);
        newEvent.setEventId(eventId);
        return newEvent;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firestoreEventId = getArguments().getString("firestoreEventId");

        View view = inflater.inflate(R.layout.fragment_organizer_view_event, container, false);

        hasQrCode = new MutableLiveData<Boolean>(Boolean.TRUE);
        hasQrCode.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (Objects.equals(changedValue, Boolean.TRUE)) {
                    viewQrCodeButton.setVisibility(View.VISIBLE);
                } else {
                    viewQrCodeButton.setVisibility(View.GONE);
                }
            }
        });
        hasDrawn = new MutableLiveData<Boolean>(Boolean.FALSE);
        hasDrawn.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (Objects.equals(changedValue, Boolean.FALSE)) {
                    chooseWinnersButton.setVisibility(View.VISIBLE);
                } else {
                    chooseWinnersButton.setVisibility(View.GONE);
                }
            }
        });

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        // TODO lots of these are not displaying the right stuff

        name = view.findViewById(R.id.organizer_event_name);
        status = view.findViewById(R.id.organizer_event_status);
        location = view.findViewById(R.id.organizer_event_location);
        spotsAvail = view.findViewById(R.id.organizer_event_spots);
        daysLeft = view.findViewById(R.id.organizer_event_dates);
        geoLocation = view.findViewById(R.id.organizer_event_geo);
        description = view.findViewById(R.id.organizer_event_description);
        viewQrCodeButton = view.findViewById(R.id.view_qr_code_button);
        chooseWinnersButton = view.findViewById(R.id.choose_winners_button);
        viewEntrantsMapButton = view.findViewById(R.id.view_entrants_map_button);
        posterImage = view.findViewById(R.id.organizer_event_poster);
        viewEntrantsWaitingListButton = view.findViewById(R.id.view_entrants_list_button);
        editButton = view.findViewById(R.id.edit_event_button);
        backButton = view.findViewById(R.id.back_button);

        eventsRef.document(firestoreEventId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            String nameText = doc.getString("title");
                            event = getEventFromFirebaseObject(doc);
                            eventId = doc.getId();
                            int maxNum = ((Long) doc.get("numberOfMaxEntrants")).intValue();
                            int curNum = ((Long) doc.get("waitingListSize")).intValue();
                            numberOfSpots = ((Long) doc.get("numberOfSpots")).intValue();
                            String spotsAvailText;
                            String statusText;
                            if (maxNum == -1) {
                                spotsAvailText = "OPEN";
                                statusText = "OPEN";
                            } else if (maxNum <= curNum) {
                                spotsAvailText = "FULL";
                                statusText = "PENDING";
                            } else {
                                spotsAvailText = Integer.toString(maxNum - (int) doc.getLong("waitingListSize").intValue()) + " Spots Available";
                                statusText = "OPEN";
                            }

                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Date startDate = doc.getDate("startDate");
                            Date endDate = doc.getDate("endDate");

                            if (startDate != null && endDate != null) {
                                // Calculate the difference in milliseconds
                                long diffInMillis = endDate.getTime() - startDate.getTime();
                                if (diffInMillis <= 0)
                                    statusText = "PENDING";

                                if (doc.getString("state").equals("OPEN")) {
                                    hasDrawn.setValue(Boolean.FALSE);
                                } else {
                                    if (doc.getString("state").equals("WAITING")) {
                                        statusText = "PENDING";
                                    } else {
                                        statusText = "CLOSED";
                                    }
                                    hasDrawn.setValue(Boolean.TRUE);
                                }

                                // Convert milliseconds to days (rounding down)
                                int daysLeftInt = (int) (diffInMillis / (24 * 60 * 60 * 1000));

                                String daysLeftText = Integer.toString(daysLeftInt);

                                Boolean isGeo = doc.getBoolean("geo");
                                String geoLocationText = isGeo ? "Requires GeoLocation Tracking" : "";
                                String descriptionText = doc.getString("description");


                                name.setText(nameText);
                                status.setText(statusText);
                                event.getLocation(address -> {
                                    Log.e("Ohm", "Addy: " + address);
                                    location.setText((address != null) ? address : "Address not found.");
                                });
                                spotsAvail.setText(spotsAvailText);
                                daysLeft.setText(daysLeftText);
                                if (isGeo) {
                                    geoLocation.setVisibility(View.VISIBLE);
                                    geoLocation.setText(geoLocationText);
                                } else {
                                    geoLocation.setVisibility(View.GONE);
                                }
                                description.setText(descriptionText);
                                String qrCodeHash = doc.getString("qrCode");
                                if (qrCodeHash.isEmpty()) {
                                    hasQrCode.setValue(Boolean.FALSE);
                                } else {
                                    hasQrCode.setValue(Boolean.TRUE);
                                }
                            }
                        }
                    }
                });

        viewEntrantsWaitingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().addFragmentToStack(new WaitingListFragment(eventId));
            }
        });
        // TODO screen for waitlist

        chooseWinnersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.doDraw();
                hasDrawn.setValue(Boolean.TRUE);
                // MyApp.getInstance().addFragmentToStack(new WinnerListFragment(eventId));
            }
        });
        // TODO screen for winnerlist

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().addFragmentToStack(new EventFragment(event));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

        viewQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "https://lotto649/?eventId=" + eventId;
                Bitmap qrCodeBitmap = QrCodeModel.generateForEvent(data);
                QrFragment qrFragment = QrFragment.newInstance(qrCodeBitmap);
                MyApp.getInstance().addFragmentToStack(qrFragment);
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