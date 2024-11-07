package com.example.lotto649.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.example.lotto649.Models.EventModel;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<EventModel> {
    public interface EventArrayAdapterListener {
    }

    public EventArrayAdapter(Context context, ArrayList<EventModel> events) {
        super(context, 0, events);
    }
}
