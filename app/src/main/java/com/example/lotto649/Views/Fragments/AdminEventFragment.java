/**
 * A fragment to display a given event's information.
 * This is used by an admin user to manage an event.
 * This fragment is reached through a list of events in the admin view.
 */
package com.example.lotto649.Views.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A fragment to display a given event's information.
 * This is used by an admin user to manage an event.
 * This fragment is reached through a list of events in the admin view.
 */
public class AdminEventFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String firestoreEventId;
    private ImageView posterImage;
    private Uri posterUri;

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
        posterImage = view.findViewById(R.id.admin_event_poster);
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
                                    Glide.with(getContext())
                                            .load(uri)
                                            .into(posterImage);
                                    posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                });
                            } else {
                                posterUri = null;
                            }
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

        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (posterUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(posterUri.toString());
                    storageRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    eventsRef
                                            .document(firestoreEventId)
                                            .update("posterImage", "")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //     add success log
                                                }
                                            });
                                    posterUri = null;
                                    posterImage.setImageResource(R.drawable.ic_person_foreground);
                                }
                            });
                }
            }
        });


        return view;
    }
}
