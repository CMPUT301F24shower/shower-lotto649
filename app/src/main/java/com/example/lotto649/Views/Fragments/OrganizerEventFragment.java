package com.example.lotto649.Views.Fragments;

import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    ExtendedFloatingActionButton optionsButtons, backButton, viewEntrantsMapButton, qrButton, viewEntrantsButton, editButton, randomButton, cancelButton, viewInvitedEntrantsButton, viewCanceledEntrants, replacementWinnerButton;
    private MutableLiveData<Boolean> hasQrCode;
    private MutableLiveData<Boolean> canDraw;
    private Uri posterUri;

    public OrganizerEventFragment() {
        // Required empty public constructor
    }

    public EventModel getEventFromFirebaseObject(DocumentSnapshot doc) {
        String eventId = doc.getId();
        String description = doc.getString("description");
        Date endDate = doc.getDate("endDate");
        Boolean geo = doc.getBoolean("geo");
        int numMaxEntrants = Objects.requireNonNull(doc.getLong("numberOfMaxEntrants")).intValue();
        int numSpots = Objects.requireNonNull(doc.getLong("numberOfSpots")).intValue();
        String organizerId = doc.getString("organizerId");
        String posterImage = doc.getString("posterImage");
        String qrCode = doc.getString("qrCode");
        Date startDate = doc.getDate("startDate");
        String stateStr = doc.getString("state");
        EventState state = EventState.OPEN;
        if (Objects.equals(stateStr, "WAITING")) {
            state = EventState.WAITING;
        } else if (Objects.equals(stateStr, "CLOSED")) {
            state = EventState.CLOSED;
        }
        String title = doc.getString("title");
        int waitingListSize = Objects.requireNonNull(doc.getLong("waitingListSize")).intValue();
        EventModel newEvent = new EventModel(title, description, numSpots, numMaxEntrants, startDate, endDate, posterImage, geo, qrCode, waitingListSize, state, db);
        newEvent.setOrganizerId(organizerId);
        newEvent.setEventId(eventId);
        return newEvent;
    }

    private void setUpOpenStateButtons(View dialogView, AlertDialog dialog) {
        viewEntrantsMapButton = dialogView.findViewById(R.id.org_dialog_map);
        qrButton = dialogView.findViewById(R.id.org_dialog_view_qr);
        viewEntrantsButton = dialogView.findViewById(R.id.org_dialog_view_entrants);
        editButton = dialogView.findViewById(R.id.org_dialog_edit);
        randomButton = dialogView.findViewById(R.id.org_dialog_choose_winners);
        cancelButton = dialogView.findViewById(R.id.org_dialog_cancel);

        if (canDraw.getValue().equals(Boolean.FALSE)) {
            randomButton.setVisibility(View.GONE);
        } else {
            randomButton.setVisibility(View.VISIBLE);
        }
        if (hasQrCode.getValue().equals(Boolean.FALSE)) {
            qrButton.setVisibility(View.GONE);
        } else {
            qrButton.setVisibility(View.VISIBLE);
        }

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
                String data = "https://lotto649/?eventId=" + eventId;
                Log.e("GDEEP", data);
                Bitmap qrCodeBitmap = QrCodeModel.generateForEvent(data);
                QrFragment qrFragment = QrFragment.newInstance(qrCodeBitmap);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, qrFragment)
                        .commit();
                dialog.dismiss();
            }
        });

        viewEntrantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitingListFragment frag = new WaitingListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", firestoreEventId);
                frag.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(frag);
                dialog.dismiss();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference doc = db.collection("events").document(firestoreEventId);
                doc.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            EventModel event = getEventFromFirebaseObject(document);
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
                event.doDraw();
                canDraw.setValue(Boolean.FALSE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void setUpWaitingStateButtons(View dialogView, AlertDialog dialog) {
        viewEntrantsMapButton = dialogView.findViewById(R.id.org_dialog_map);
        qrButton = dialogView.findViewById(R.id.org_dialog_view_qr);
        viewInvitedEntrantsButton = dialogView.findViewById(R.id.org_dialog_view_invited_entrants);
        viewCanceledEntrants = dialogView.findViewById(R.id.org_dialog_view_canceled_entrants);
        replacementWinnerButton = dialogView.findViewById(R.id.org_dialog_choose_replacement);
        editButton = dialogView.findViewById(R.id.org_dialog_edit);
        cancelButton = dialogView.findViewById(R.id.org_dialog_cancel);

        // TODO: only show replacement button if someone has canceled and needs to be replaced

        if (hasQrCode.getValue().equals(Boolean.FALSE)) {
            qrButton.setVisibility(View.GONE);
        } else {
            qrButton.setVisibility(View.VISIBLE);
        }

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
                String data = "https://lotto649/?eventId=" + eventId;
                Log.e("GDEEP", data);
                Bitmap qrCodeBitmap = QrCodeModel.generateForEvent(data);
                QrFragment qrFragment = QrFragment.newInstance(qrCodeBitmap);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, qrFragment)
                        .commit();
                dialog.dismiss();
            }
        });

        // TODO: show list of invited entrants (make new fragment)
        viewInvitedEntrantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitingListFragment frag = new WaitingListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", firestoreEventId);
                frag.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(frag);
                dialog.dismiss();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference doc = db.collection("events").document(firestoreEventId);
                doc.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            EventModel event = getEventFromFirebaseObject(document);
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

        // TODO: have replacement draw
        replacementWinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.doDraw();
                canDraw.setValue(Boolean.FALSE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
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
                    if (qrButton != null)
                        qrButton.setVisibility(View.VISIBLE);
                } else {
                    if (qrButton != null)
                        qrButton.setVisibility(View.GONE);
                }
            }
        });
        canDraw = new MutableLiveData<Boolean>(Boolean.TRUE);
        canDraw.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (Objects.equals(changedValue, Boolean.TRUE)) {
                    if (randomButton != null)
                        randomButton.setVisibility(View.VISIBLE);
                } else {
                    if (randomButton != null)
                        randomButton.setVisibility(View.GONE);
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
        optionsButtons = view.findViewById(R.id.organizer_event_options);
        backButton = view.findViewById(R.id.organizer_event_cancel);

        optionsButtons.setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.event_organizer_dialog, null);

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .create();

            setUpOpenStateButtons(dialogView, dialog);

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
                            event = getEventFromFirebaseObject(doc);
                            eventId = doc.getId();
                            Long maxEntrants = (Long) doc.get("numberOfMaxEntrants");
                            int maxNum = 0;
                            if (maxEntrants != null)
                                maxNum = (maxEntrants).intValue();

                            Long waitListSize = (Long) doc.get("waitingListSize");
                            int curNum = 0;
                            if (waitListSize != null)
                                curNum = (waitListSize).intValue();

                            Long numSpots = (Long) doc.get("numberOfSpots");
                            if (numSpots != null)
                                numberOfSpots = (numSpots).intValue();

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

                                if (Objects.equals(doc.getString("state"), "OPEN")) {
                                    Log.e("JASON STATE TEST", Objects.requireNonNull(doc.getString("state")));
                                    if (waitListSize != null && waitListSize > 0) {
                                        canDraw.setValue(Boolean.TRUE);
                                    } else {
                                        canDraw.setValue(Boolean.FALSE);
                                    }
                                } else {
                                    if (Objects.equals(doc.getString("state"), "WAITING")) {
                                        statusText = "PENDING";
                                    } else {
                                        statusText = "CLOSED";
                                    }
                                    canDraw.setValue(Boolean.FALSE);
                                }

                                // Convert milliseconds to days (rounding down)
                                int daysLeftInt = (int) (diffInMillis / (24 * 60 * 60 * 1000));

                                String daysLeftText = Integer.toString(daysLeftInt);

                                Boolean isGeo = doc.getBoolean("geo");
                                String geoLocationText = Boolean.TRUE.equals(isGeo) ? "Requires GeoLocation Tracking" : "";
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

        // viewEntrantsWaitingListButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         MyApp.getInstance().addFragmentToStack(new WaitingListFragment(eventId));
        //     }
        // });
        // // TODO screen for waitlist
        //
        // chooseWinnersButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         event.doDraw();
        //         hasDrawn.setValue(Boolean.TRUE);
        //         // MyApp.getInstance().addFragmentToStack(new WinnerListFragment(eventId));
        //     }
        // });
        // // TODO screen for winnerlist
        //
        // editButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         MyApp.getInstance().addFragmentToStack(new EventFragment(event));
        //     }
        // });

        // viewQrCodeButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         String data = "https://lotto649/?eventId=" + eventId;
        //         Bitmap qrCodeBitmap = QrCodeModel.generateForEvent(data);
        //         QrFragment qrFragment = QrFragment.newInstance(qrCodeBitmap);
        //         MyApp.getInstance().addFragmentToStack(qrFragment);
        //     }
        // });
        //
        // viewEntrantsMapButton.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         Bundle bundle = new Bundle();
        //         bundle.putString("eventId", firestoreEventId);
        //         MapFragment mapFragment = new MapFragment();
        //         mapFragment.setArguments(bundle);
        //         MyApp.getInstance().addFragmentToStack(mapFragment);
        //     }
        // });
        return view;
    }
}