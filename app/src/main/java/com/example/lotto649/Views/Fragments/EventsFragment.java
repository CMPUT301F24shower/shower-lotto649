package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Controllers.EventsController;
import com.example.lotto649.Models.EventsModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class EventsFragment extends Fragment {
    private EventsController eventsController;
    private ExtendedFloatingActionButton addButton;
    private EventsModel events;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        events = new EventsModel();
        eventsController = new EventsController(events);
        addButton = view.findViewById(R.id.addButton);



        addButton.setOnClickListener(v -> eventsController.addEvent());
        
        return view;
    }
}
