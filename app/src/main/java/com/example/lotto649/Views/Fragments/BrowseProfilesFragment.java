/**
 * BrowseProfilesFragment class represents a fragment for the admin to browse all profiles in the application.
 * <p>
 * This fragment shows a list view of every profile, selecting the event will show its full details and allow for it to be deleted.
 * This page is only accessible to users with 'admin' status
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

import com.example.lotto649.R;

public class BrowseProfilesFragment extends Fragment {

    /**
     * Public empty constructor for BrowseProfilesFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public BrowseProfilesFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     * This method inflates the layout defined in `fragment_browse_profiles.xml`.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the camera fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_profiles, container, false);
    }
}