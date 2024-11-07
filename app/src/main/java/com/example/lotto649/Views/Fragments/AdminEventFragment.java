package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class AdminEventFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String firestoreEventId;

    /**
     * Public empty constructor for BrowseEventsFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public AdminEventFragment() {
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
        View view = inflater.inflate(R.layout.fragment_admin_view_event, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        TextView name = view.findViewById(R.id.admin_event_name);
        TextView status = view.findViewById(R.id.admin_event_status);
        TextView location = view.findViewById(R.id.admin_event_location);
        TextView spotsAvail = view.findViewById(R.id.admin_event_spots);
        TextView numAttendees = view.findViewById(R.id.admin_event_attendees);
        TextView dates = view.findViewById(R.id.admin_event_dates);
        TextView geoLocation = view.findViewById(R.id.admin_event_geo);
        TextView description = view.findViewById(R.id.admin_event_description);
        Button deleteImageButton = view.findViewById(R.id.admin_delete_event_image);
        Button deleteQRButton = view.findViewById(R.id.admin_delete_event_qr);
        Button deleteEventButton = view.findViewById(R.id.admin_delete_event);

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
                            Date startDate = doc.getDate("startDate");
                            Date endDate = doc.getDate("endDate");
                            String datesText = "Enter between " + startDate.toString() + " - " + endDate.toString();
                            Boolean isGeo = doc.getBoolean("geo");
                            String geoLocationText = isGeo ? "" : "Requires GeoLocation Tracking";
                            String descriptionText = doc.getString("description");
                            name.setText(nameText);
                            // TODO: set to actual event status
                            status.setText("OPEN");
                            // TODO: set to actual location
                            location.setText("LOCATION");
                            spotsAvail.setText(spotsAvailText);
                            numAttendees.setText(numAttendeesText);
                            dates.setText(datesText);
                            geoLocation.setText(geoLocationText);
                            description.setText(descriptionText);
                        }
                    }
                });

        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventsRef
                        .document(firestoreEventId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                BrowseEventsFragment frag = new BrowseEventsFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.flFragment, frag, null)
                                        .addToBackStack(null)
                                        .commit();
                                //     add success log
                            }
                        });
            }
        });


        return view;
    }
}
