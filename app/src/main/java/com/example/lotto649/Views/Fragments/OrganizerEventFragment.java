package com.example.lotto649.Views.Fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    ExtendedFloatingActionButton optionsButtons;
    ExtendedFloatingActionButton backButton;
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
        // TODO lots of these are not displaying the right stuff

        name = view.findViewById(R.id.organizer_event_name);
        status = view.findViewById(R.id.organizer_event_status);
        location = view.findViewById(R.id.organizer_event_location);
        spotsAvail = view.findViewById(R.id.organizer_event_spots);
        daysLeft = view.findViewById(R.id.organizer_event_dates);
        geoLocation = view.findViewById(R.id.organizer_event_geo);
        description = view.findViewById(R.id.organizer_event_description);
        optionsButtons = view.findViewById(R.id.organizer_event_options);
        backButton = view.findViewById(R.id.organizer_event_cancel);

        optionsButtons.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.event_organizer_dialog, null);

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .create();

            ExtendedFloatingActionButton viewEntrantsMapButton = dialogView.findViewById(R.id.org_dialog_map);
            ExtendedFloatingActionButton qrButton = dialogView.findViewById(R.id.org_dialog_view_qr);
            ExtendedFloatingActionButton viewEntrantsButton = dialogView.findViewById(R.id.org_dialog_view_entrants);
            ExtendedFloatingActionButton editButton = dialogView.findViewById(R.id.org_dialog_edit);
            ExtendedFloatingActionButton randomButton = dialogView.findViewById(R.id.org_dialog_choose_winners);
            ExtendedFloatingActionButton cancelButton = dialogView.findViewById(R.id.org_dialog_cancel);

            viewEntrantsMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("eventId", firestoreEventId);
                    MapFragment mapFragment = new MapFragment();
                    mapFragment.setArguments(bundle);
                    MyApp.getInstance().addFragmentToStack(mapFragment);
                    dialog.dismiss();
                }
            });

            qrButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO add new QR code stuff here
                    QrFragment qrFragment = QrFragment.newInstance(qrCodeBitmap);
                }
            });

            viewEntrantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("firestoreEventId", firestoreEventId);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference doc = db.collection("events").document(firestoreEventId);
                    doc.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                EventModel event = document.toObject(EventModel.class);
                                EventFragment frag = new EventFragment(event);
//                                frag.setArguments(bundle);
                                MyApp.getInstance().addFragmentToStack(frag);
                            } else {
                                Toast.makeText(getContext(), "Unable to fetch event from firestore", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Unable to fetch event from firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.dismiss();
                }
            });

            randomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            // Show the dialog
            dialog.show();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

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

        return view;
    }
}