package com.example.lotto649;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lotto649.Views.Fragments.HomeFragment;
import com.example.lotto649.Views.Fragments.JoinedEventsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull HomeTab fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else {
            return new JoinedEventsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of pages
    }

    @Override
    public long getItemId(int position) {
        return position; // Unique ID for each fragment
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId < getItemCount();
    }
}
