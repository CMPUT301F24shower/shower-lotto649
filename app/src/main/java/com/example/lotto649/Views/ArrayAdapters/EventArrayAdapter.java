package com.example.lotto649.Views.ArrayAdapters;

import android.app.Activity;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Custom ArrayAdapter for displaying EventModel items in a ListView.
 * This adapter handles the binding of event details to the appropriate views and loads images from Firebase.
 */
public class EventArrayAdapter extends ArrayAdapter<EventModel> {
    private final EventArrayAdapterListener listener;
    private Uri profileUri;

    /**
     * Interface to listen for waitlist changes in events.
     */
    public interface EventArrayAdapterListener {

        /**
         * Called when the event's waitlist has changed.
         */
        void onEventsWaitListChanged();
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return The View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate view if it does not already exist
        View view = (convertView == null)? LayoutInflater.from(getContext()).inflate(R.layout.event_content, parent, false) : convertView;

        // Get the EventModel object for the current position
        EventModel event = getItem(position);

        // Find views for event details
        ImageView posterPlaceholderImage = view.findViewById(R.id.list_event_poster_placeholder);
        ImageView posterImage = view.findViewById(R.id.list_event_poster);

        // Set event details in the respective views
        ((TextView) view.findViewById(R.id.eventTitle)).setText(event.getTitle());
        ((TextView) view.findViewById(R.id.eventStatus)).setText("OPEN/PENDING/CLOSED");
        ((TextView) view.findViewById(R.id.eventLocation)).setText(event.getFacilityId());

        // Calculate and display available spots
        ((TextView) view.findViewById(R.id.eventAvailableSpots)).setText(
                ((0 < event.getNumberOfMaxEntrants() &&
                (event.getWaitingList().isEmpty() ? 0 : event.getWaitingList().size()) <= event.getNumberOfMaxEntrants()) ?
                        String.valueOf(event.getNumberOfMaxEntrants() - event.getWaitingList().size()) + " Spots Available" : "OPEN")
        );

        // Calculate and display days left
        ((TextView) view.findViewById(R.id.eventDaysLeft)).setText(
            String.valueOf(TimeUnit.DAYS.convert(event.getEndDate().getTime() - new Date().getTime(),TimeUnit.MILLISECONDS)) + " Days Left to Enter"
        );

        // Show geolocation requirement, if any
        if (event.getGeo()) {
            ((TextView) view.findViewById(R.id.geolocationRequirement)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.geolocationRequirement)).setText("Requires GeoLocation Tracking");
        } else {
            ((TextView) view.findViewById(R.id.geolocationRequirement)).setVisibility(View.GONE);
        }

        // Set the event description
        ((TextView) view.findViewById(R.id.eventDescription)).setText(event.getDescription());

        // Load event image if available
        String profileUriString = event.getPosterImage();
        Log.e("JASON", profileUriString);

        if (!Objects.equals(profileUriString, "")) {
            profileUri = Uri.parse(profileUriString);
            StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(profileUriString);

            // Asynchronously load image using Firebase and Glide
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                profileUri = uri;
                Context context = parent.getContext();
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;

                    // Check if the Activity is valid before using it
                    if (!activity.isDestroyed() && !activity.isFinishing()) {
                        Glide.with(activity)
                                .load(uri)
                                .into(posterImage);
                    }
                }
//                Glide.with(getContext())
//                        .load(uri)
//                        .into(posterImage);
                // TODO: This is hardcoded, but works good on my phone, not sure if this is a good idea or not
                // Set layout parameters for image
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(750, 450);
                posterImage.setLayoutParams(layoutParams);
                posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                posterPlaceholderImage.setVisibility(View.GONE);
                posterImage.setVisibility(View.VISIBLE);
            });
        } else {
            // Show placeholder if image URI is empty
            posterPlaceholderImage.setVisibility(View.VISIBLE);
            posterImage.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Constructor for the EventArrayAdapter.
     *
     * @param context  The current context.
     * @param events   The list of EventModel objects to represent in the ListView.
     * @param listener Listener for handling waitlist changes.
     */
    public EventArrayAdapter(Context context, ArrayList<EventModel> events, EventArrayAdapterListener listener) {
        super(context, 0, events);
        this.listener =  listener;
    }
}
