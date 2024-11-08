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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EventArrayAdapter extends ArrayAdapter<EventModel> {
    private final EventArrayAdapterListener listener;
    private Uri profileUri;

    public interface EventArrayAdapterListener {
        void onEventsWaitListChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)? LayoutInflater.from(getContext()).inflate(R.layout.event_content, parent, false) : convertView;

        EventModel event = getItem(position);

        ImageView posterPlaceholderImage = view.findViewById(R.id.list_event_poster_placeholder);
        ImageView posterImage = view.findViewById(R.id.list_event_poster);

        ((TextView) view.findViewById(R.id.eventTitle)).setText(event.getTitle());
        ((TextView) view.findViewById(R.id.eventStatus)).setText("OPEN/PENDING/CLOSED");
        ((TextView) view.findViewById(R.id.eventLocation)).setText(event.getFacilityId());
        ((TextView) view.findViewById(R.id.eventCost)).setText("$" + new DecimalFormat("0.00").format(event.getCost()));
        ((TextView) view.findViewById(R.id.eventAvailableSpots)).setText(
                ((0 < event.getNumberOfMaxEntrants() &&
                (event.getWaitingList().isEmpty() ? 0 : event.getWaitingList().size()) <= event.getNumberOfMaxEntrants()) ?
                        String.valueOf(event.getNumberOfMaxEntrants() - event.getWaitingList().size()) + " Spots Available" : "OPEN")
        );
        ((TextView) view.findViewById(R.id.eventDaysLeft)).setText(
            String.valueOf(TimeUnit.DAYS.convert(event.getEndDate().getTime() - new Date().getTime(),TimeUnit.MILLISECONDS)) + " Days Left to Enter"
        );
        ((TextView) view.findViewById(R.id.eventDescription)).setText(event.getDescription());

        String profileUriString = event.getPosterImage();

        Log.e("JASON", profileUriString);

        if (!Objects.equals(profileUriString, "")) {
            profileUri = Uri.parse(profileUriString);
            StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(profileUriString);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                profileUri = uri;
                Glide.with(getContext())
                        .load(uri)
                        .into(posterImage);
                // TODO: This is hardcoded, but works good on my phone, not sure if this is a good idea or not
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(750, 450);
                posterImage.setLayoutParams(layoutParams);
                posterImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                posterPlaceholderImage.setVisibility(View.GONE);
                posterImage.setVisibility(View.VISIBLE);
            });
        } else {
            posterPlaceholderImage.setVisibility(View.VISIBLE);
            posterImage.setVisibility(View.GONE);
        }

        return view;
    }

    public EventArrayAdapter(Context context, ArrayList<EventModel> events, EventArrayAdapterListener listener) {
        super(context, 0, events);
        this.listener =  listener;
    }
}
