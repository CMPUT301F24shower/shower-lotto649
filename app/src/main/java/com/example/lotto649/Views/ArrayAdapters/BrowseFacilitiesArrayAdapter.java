package com.example.lotto649.Views.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lotto649.Models.FacilityModel;
import com.example.lotto649.R;

import java.util.ArrayList;

public class BrowseFacilitiesArrayAdapter extends ArrayAdapter<FacilityModel> {
        public BrowseFacilitiesArrayAdapter(Context context, ArrayList<FacilityModel> facilities) {
            super(context, 0, facilities);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.facility_list_item, parent, false);
            FacilityModel facility = getItem(position);
            assert facility != null;
            TextView facilityName = view.findViewById(R.id.facility_name);
            TextView address = view.findViewById(R.id.facility_address);
            TextView numOpenEvents = view.findViewById(R.id.facility_open_events);
            facilityName.setText(facility.getFacilityName());
            if (facility.getAddress().isEmpty()) {
                address.setVisibility(View.GONE);
            } else {
                address.setVisibility(View.VISIBLE);
                address.setText(facility.getAddress());
            }
            // TODO: update with actual number of open events at facility
            numOpenEvents.setText("Number of Open Events: 0");
            return view;
        }
}