package com.example.lotto649;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeTab extends Fragment {
    ViewPagerAdapter adapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewPager != null) {
            viewPager.setAdapter(null); // Clear adapter to avoid memory leaks
        }
    }
}
