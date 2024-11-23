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

import com.bumptech.glide.Glide;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;
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

    /**
     * Constructor for the array adapter
     * @param context the context of the adapter
     * @param events the user models to be adapted
     */
    public BrowseEventsArrayAdapter(Context context, ArrayList<EventModel> events) {
        super(context, 0, events);
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
        // TODO: update with actual status
        eventStatus.setText("OPEN");
        // TODO: update with actual location
        eventLocation.setText("Location");
        if (event.getNumberOfMaxEntrants() == -1) {
            eventSpotsAvail.setText("OPEN");
        } else {
            eventSpotsAvail.setText(Integer.toString(event.getNumberOfMaxEntrants() - event.getWaitingList().size()) + " Spots Available");
        }
        eventNumAttendees.setText(Integer.toString(event.getNumberOfSpots()) + " Attendees");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
                // TODO: This is hardcoded, but works good on my phone, not sure if this is a good idea or not
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(900, 450);
                posterImage.setLayoutParams(layoutParams);
                posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            });
        }
        return view;
    }
}
