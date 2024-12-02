/**
 * This is an array adapter for the admin view
 * for browsing events. It maps an ArrayList of EventModels
 * to a ListView to display the needed information
 */
package com.example.lotto649.Views.ArrayAdapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.lotto649.FirestoreHelper;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This is an array adapter for the admin view
 * for browsing events. It maps an ArrayList of EventModels
 * to a ListView to display the needed information
 */
public class BrowseEventsArrayAdapter extends ArrayAdapter<EventModel> {
    private Uri posterUri;
    private final LifecycleOwner lifecycleOwner;

    /**
     * Constructor for the array adapter
     * @param context the context of the adapter
     * @param events the user models to be adapted
     */
    public BrowseEventsArrayAdapter(Context context, ArrayList<EventModel> events, LifecycleOwner lifecycleOwner) {
        super(context, 0, events);
        this.lifecycleOwner = lifecycleOwner;
    }

    /**
     * Gets the list view item and displays information
     * @param position the position in the ListView that the adapter is for
     * @param convertView gives the ability to reuse an old view
     * @param parent the parent of the adapter
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        EventModel event = getItem(position);
        assert event != null;
        TextView eventName = view.findViewById(R.id.admin_event_name);
        TextView eventStatus = view.findViewById(R.id.admin_event_status);
        TextView eventLocation = view.findViewById(R.id.admin_event_location);
        TextView eventSpotsAvail = view.findViewById(R.id.admin_event_spots);
        TextView eventNumAttendees = view.findViewById(R.id.admin_event_attendees);
        TextView eventDates = view.findViewById(R.id.admin_event_dates);
        TextView eventGeo = view.findViewById(R.id.admin_event_geo);
        TextView eventDescription = view.findViewById(R.id.admin_event_description);
        ImageView posterImage = view.findViewById(R.id.event_poster);

        eventName.setText(event.getTitle());
        eventStatus.setText(event.getState().toString());
        eventSpotsAvail.setText("No max waitlist size");
        MutableLiveData<Integer> waitListSize = new MutableLiveData<>(-1);
        FirestoreHelper.getInstance().getWaitlistSize(event.getEventId(), waitListSize);
        if (event.getNumberOfMaxEntrants() != -1) {
            waitListSize.observe(lifecycleOwner, size -> {
                if (size != null && size != -1) {
                    Log.d("Waitlist browseeventsarrayadapter", "Current waitlist size: " + size);
                    // Perform actions with the waitlist size
                    if (event.getNumberOfMaxEntrants() <= size) {
                        eventSpotsAvail.setText("FULL");
                    } else {
                        String newText = size + "/" + event.getNumberOfMaxEntrants() + " Spots Full";
                        eventSpotsAvail.setText(newText);
                    }
                }
            });
        }

        eventNumAttendees.setText(event.getNumberOfSpots() + " Lottery Winners");
        eventLocation.setText("LOCATION");
        FirebaseFirestore.getInstance().collection("events").document(event.getEventId()).get().addOnCompleteListener(eventTask -> {
            if (eventTask.isSuccessful()) {
                DocumentSnapshot eventDoc = eventTask.getResult();
                if (eventDoc != null) {
                    String facilityId = eventDoc.getString("organizerId");
                    if (facilityId != null) {
                        FirebaseFirestore.getInstance().collection("facilities").document(facilityId).get().addOnCompleteListener(facilityTask -> {
                            if (facilityTask.isSuccessful()) {
                                DocumentSnapshot facilityDoc = facilityTask.getResult();
                                String facilityName = facilityDoc.getString("facility");
                                String facilityAddress = facilityDoc.getString("address");
                                if (facilityName != null && facilityAddress != null) {
                                    eventLocation.setText(facilityName + " - " + facilityAddress);
                                }
                            }
                        });
                    }
                }
            }
        });


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (event.getStartDate() != null && event.getEndDate() != null)
            eventDates.setText("Enter between " + df.format(event.getStartDate()) + " - " + df.format(event.getEndDate()));
        if (event.getGeo()) {
            eventGeo.setVisibility(View.VISIBLE);
            eventGeo.setText("Requires GeoLocation Tracking");
        } else {
            eventGeo.setVisibility(View.GONE);
        }
        eventDescription.setText(event.getDescription());


        String posterUriString = event.getPosterImage();
        Log.e("JASON TEST", "Poster image: " + posterUriString);
        if (posterUriString != null && !Objects.equals(posterUriString, "")) {
            posterUri = Uri.parse(posterUriString);
            StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(posterUriString);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                posterUri = uri;
                Glide.with(getContext())
                        .load(uri)
                        .into(posterImage);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(900, 450);
                posterImage.setLayoutParams(layoutParams);
                posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            });
        }
        return view;
    }
}
