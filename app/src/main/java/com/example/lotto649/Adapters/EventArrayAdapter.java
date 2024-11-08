package com.example.lotto649.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventArrayAdapter extends ArrayAdapter<EventModel> {
    private final EventArrayAdapterListener listener;

    public interface EventArrayAdapterListener {
        void onEventsWaitListChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)? LayoutInflater.from(getContext()).inflate(R.layout.event_content, parent, false) : convertView;

        EventModel event = getItem(position);

        //((ImageView) view.findViewById(R.id.eventPoster));

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

        return view;
    }

    public EventArrayAdapter(Context context, ArrayList<EventModel> events, EventArrayAdapterListener listener) {
        super(context, 0, events);
        this.listener =  listener;
    }
}
