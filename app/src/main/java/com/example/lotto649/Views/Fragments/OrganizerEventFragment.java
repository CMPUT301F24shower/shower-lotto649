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
import com.example.lotto649.FirestoreHelper;
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
    ExtendedFloatingActionButton optionsButtons, backButton, viewEntrantsMapButton, qrButton, viewEntrantsButton, editButton, randomButton, cancelButton, viewInvitedEntrantsButton, viewCanceledEntrants, replacementWinnerButton, viewFinalEntrants;
    private MutableLiveData<Boolean> hasQrCode;
    private MutableLiveData<Boolean> canDraw;
    private MutableLiveData<Boolean> canReplacementDraw;
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
        EventModel newEvent = new EventModel(title, description, numSpots, numMaxEntrants, startDate, endDate, posterImage, geo, qrCode, state, db);
        newEvent.setOrganizerId(organizerId);
        newEvent.setEventId(eventId);
        return newEvent;
    }

    private void hideOpenStateButtons() {
        if (viewEntrantsButton != null) {
            viewEntrantsButton.setVisibility(View.GONE);
        }
        if (randomButton != null) {
            randomButton.setVisibility(View.GONE);
        }
        if (qrButton != null) {
            qrButton.setVisibility(View.GONE);
        }
        if (viewEntrantsMapButton != null) {
            viewEntrantsMapButton.setVisibility(View.GONE);
        }
        if (editButton != null) {
            editButton.setVisibility(View.GONE);
        }
        if (cancelButton != null) {
            cancelButton.setVisibility(View.GONE);
        }
    }

    private void hideWaitingStateButtons() {
        if (viewInvitedEntrantsButton != null) {
            viewInvitedEntrantsButton.setVisibility(View.GONE);
        }
        if (replacementWinnerButton != null) {
            replacementWinnerButton.setVisibility(View.GONE);
        }
        if (qrButton != null) {
            qrButton.setVisibility(View.GONE);
        }
        if (editButton != null) {
            editButton.setVisibility(View.GONE);
        }
        if (viewCanceledEntrants != null) {
            viewCanceledEntrants.setVisibility(View.GONE);
        }
        if (viewEntrantsMapButton != null) {
            viewEntrantsMapButton.setVisibility(View.GONE);
        }
        if (cancelButton != null) {
            cancelButton.setVisibility(View.GONE);
        }
    }

    private void hideClosedStateButtons() {
        if (viewEntrantsMapButton != null) {
            viewEntrantsMapButton.setVisibility(View.GONE);
        }
        if (cancelButton != null) {
            cancelButton.setVisibility(View.GONE);
        }
        if (viewCanceledEntrants != null) {
            viewCanceledEntrants.setVisibility(View.GONE);
        }
        if (viewFinalEntrants != null) {
            viewFinalEntrants.setVisibility(View.GONE);
        }
    }

    private void showOpenStateButtons() {
        if (viewEntrantsButton != null) {
            viewEntrantsButton.setVisibility(View.VISIBLE);
        }
        if (randomButton != null) {
            randomButton.setVisibility(View.VISIBLE);
        }
        if (qrButton != null) {
            qrButton.setVisibility(View.VISIBLE);
        }
        if (viewEntrantsMapButton != null) {
            viewEntrantsMapButton.setVisibility(View.VISIBLE);
        }
        if (editButton != null) {
            editButton.setVisibility(View.VISIBLE);
        }
        if (cancelButton != null) {
            cancelButton.setVisibility(View.VISIBLE);
        }
    }

    private void showWaitingStateButtons() {
        if (viewInvitedEntrantsButton != null) {
            viewInvitedEntrantsButton.setVisibility(View.VISIBLE);
        }
        if (replacementWinnerButton != null) {
            replacementWinnerButton.setVisibility(View.VISIBLE);
        }
        if (qrButton != null) {
            qrButton.setVisibility(View.VISIBLE);
        }
        if (editButton != null) {
            editButton.setVisibility(View.VISIBLE);
        }
        if (viewCanceledEntrants != null) {
            viewCanceledEntrants.setVisibility(View.VISIBLE);
        }
        if (viewEntrantsMapButton != null) {
            viewEntrantsMapButton.setVisibility(View.VISIBLE);
        }
        if (cancelButton != null) {
            cancelButton.setVisibility(View.VISIBLE);
        }
    }

    private void showClosedStateButtons() {
        if (viewEntrantsMapButton != null) {
            viewEntrantsMapButton.setVisibility(View.VISIBLE);
        }
        if (cancelButton != null) {
            cancelButton.setVisibility(View.VISIBLE);
        }
        if (viewCanceledEntrants != null) {
            viewCanceledEntrants.setVisibility(View.VISIBLE);
        }
        if (viewFinalEntrants != null) {
            viewFinalEntrants.setVisibility(View.VISIBLE);
        }
    }

    private void setUpOpenStateButtons(AlertDialog dialog) {
        hideWaitingStateButtons();
        hideClosedStateButtons();
        showOpenStateButtons();
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

    private void setUpWaitingStateButtons(AlertDialog dialog) {
        hideOpenStateButtons();
        hideClosedStateButtons();
        showWaitingStateButtons();
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

        viewInvitedEntrantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WinnerListFragment frag = new WinnerListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", firestoreEventId);
                frag.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(frag);
                dialog.dismiss();
            }
        });

        viewCanceledEntrants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelledListFragment frag = new CancelledListFragment();
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

        replacementWinnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.doReplacementDraw();
                canReplacementDraw.setValue(Boolean.FALSE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void setUpClosedStateButtons(AlertDialog dialog) {
        hideWaitingStateButtons();
        hideOpenStateButtons();
        showClosedStateButtons();
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

        viewFinalEntrants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnrolledListFragment frag = new EnrolledListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", firestoreEventId);
                frag.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(frag);
                dialog.dismiss();
            }
        });

        viewCanceledEntrants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelledListFragment frag = new CancelledListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("firestoreEventId", firestoreEventId);
                frag.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(frag);
                dialog.dismiss();
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
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.event_organizer_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        viewEntrantsMapButton = dialogView.findViewById(R.id.org_dialog_map);
        qrButton = dialogView.findViewById(R.id.org_dialog_view_qr);
        viewInvitedEntrantsButton = dialogView.findViewById(R.id.org_dialog_view_invited_entrants);
        viewCanceledEntrants = dialogView.findViewById(R.id.org_dialog_view_canceled_entrants);
        replacementWinnerButton = dialogView.findViewById(R.id.org_dialog_choose_replacement);
        editButton = dialogView.findViewById(R.id.org_dialog_edit);
        cancelButton = dialogView.findViewById(R.id.org_dialog_cancel);
        viewEntrantsButton = dialogView.findViewById(R.id.org_dialog_view_entrants);
        randomButton = dialogView.findViewById(R.id.org_dialog_choose_winners);
        viewFinalEntrants = dialogView.findViewById(R.id.org_dialog_view_final_enrolled);

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
        canReplacementDraw = new MutableLiveData<Boolean>(Boolean.FALSE);
        canReplacementDraw.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (Objects.equals(changedValue, Boolean.TRUE)) {
                    if (replacementWinnerButton != null) {
                        replacementWinnerButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (replacementWinnerButton != null) {
                        replacementWinnerButton.setVisibility(View.GONE);
                    }
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


            if (event.getState().equals(EventState.OPEN)) {
                setUpOpenStateButtons(dialog);
            } else if (event.getState().equals(EventState.WAITING)) {
                setUpWaitingStateButtons(dialog);
            } else {
                setUpClosedStateButtons(dialog);
            }

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

                            FirestoreHelper.getInstance().getWaitlistSize(firestoreEventId);
                            int curNum = FirestoreHelper.getInstance().getCurrWaitlistSize().getValue();

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
                                spotsAvailText = Integer.toString(maxNum - curNum) + " Spots Available";
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
                                    FirestoreHelper.getInstance().getWaitlistSize(firestoreEventId);
                                    int waitListSize = FirestoreHelper.getInstance().getCurrWaitlistSize().getValue();
                                    if (waitListSize > 0) {
                                        canDraw.setValue(Boolean.TRUE);
                                    } else {
                                        canDraw.setValue(Boolean.FALSE);
                                    }
                                } else {
                                    if (Objects.equals(doc.getString("state"), "WAITING")) {
                                        // TODO: set if replacement button can be clicked
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

                                FirestoreHelper.getInstance().getWinnersSize(firestoreEventId);
                                int numWinners = FirestoreHelper.getInstance().getCurrWinnersSize().getValue();
                                FirestoreHelper.getInstance().getNotSelectedSize(firestoreEventId);
                                int numNotSelected = FirestoreHelper.getInstance().getCurrNotSelectedSize().getValue();
                                FirestoreHelper.getInstance().getEnrolledSize(firestoreEventId);
                                int numEnrolled = FirestoreHelper.getInstance().getCurrEnrolledSize().getValue();

                                Log.e("JASON REDRAW", "numWinners: " + Integer.toString(numWinners));
                                Log.e("JASON REDRAW", "numNotSelected: " + Integer.toString(numNotSelected));
                                Log.e("JASON REDRAW", "numEnrolled: " + Integer.toString(numEnrolled));
                                Log.e("JASON REDRAW", "numSpots: " + Integer.toString(numSpots.intValue()));

                                if (numNotSelected == 0) {
                                    canReplacementDraw.setValue(Boolean.FALSE);
                                } else {
                                    if (numWinners + numEnrolled < numSpots) {
                                        canReplacementDraw.setValue(Boolean.TRUE);
                                    } else {
                                        canReplacementDraw.setValue(Boolean.FALSE);
                                    }
                                }

                                if (numWinners == 0 && event.getState().equals(EventState.WAITING)) {
                                    // can't be in the waiting state and these be zero, so only change state here
                                    if (numNotSelected != 0 || numEnrolled != 0){
                                        event.setState(EventState.CLOSED);
                                    }
                                }
                            }
                        }
                    }
                });
        return view;
    }
}