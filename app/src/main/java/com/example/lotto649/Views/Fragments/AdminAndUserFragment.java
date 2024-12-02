/**
 * AdminAndUserFragment class represents a fragment for a user who is an admin and some other user type.
 * <p>
 * This fragment inflates the AdminAndUser layout when the user loads the app.
 * It is part of the bottom navigation bar implementation used in the application.
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 */
package com.example.lotto649.Views.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * AdminAndUserFragment class represents a fragment for a user who is an admin and some other user type.
 * <p>
 * This fragment inflates the AdminAndUser layout when the user loads the app.
 * It is part of the bottom navigation bar implementation used in the application.
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 */
public class AdminAndUserFragment extends Fragment {
    private ExtendedFloatingActionButton profiles, facilities, events;

    /**
     * Public empty constructor for AdminAndUserFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public AdminAndUserFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_and_user, container, false);

        profiles = view.findViewById(R.id.admin_button_profiles);
        facilities = view.findViewById(R.id.admin_button_facilities);
        events = view.findViewById(R.id.admin_button_events);

        profiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().addFragmentToStack(new BrowseProfilesFragment());
            }
        });

        facilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().addFragmentToStack(new BrowseFacilitiesFragment());
            }
        });

        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().addFragmentToStack(new BrowseEventsFragment());
            }
        });

        return view;
    }
}
