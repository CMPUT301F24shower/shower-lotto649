package com.example.lotto649.Views.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class BrowseEventsArrayAdapter extends ArrayAdapter<EventModel> {
    public BrowseEventsArrayAdapter(Context context, ArrayList<EventModel> events) {
        super(context, 0, events);
    }

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
        return view;
    }
}
