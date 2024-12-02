package com.example.lotto649;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * This creates the tab at the top of the home page for switching between created events
 * and joined events
 */
public class HomeTab extends Fragment {
    ViewPagerAdapter adapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;

    /**
     * Called to create the view hierarchy for this fragment.
     *
     * @param inflater           LayoutInflater object used to inflate any views in the fragment
     * @param container          The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.home_tab, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        if (viewPager.getAdapter() == null) {
            adapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(adapter);
            viewPager.setSaveEnabled(false);


            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) {
                    tab.setText("Created Events");
                } else if (position == 1) {
                    tab.setText("Joined Events");
                }
            }).attach();
        }
        return view;
    }

    /**
     * When the view is destroyed, clear the adapter to avoid memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewPager != null) {
            viewPager.setAdapter(null); // Clear adapter to avoid memory leaks
        }
    }
}
