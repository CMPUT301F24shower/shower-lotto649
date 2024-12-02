package com.example.lotto649;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lotto649.Views.Fragments.HomeFragment;
import com.example.lotto649.Views.Fragments.JoinedEventsFragment;

/**
 * Adapter class for managing fragments in a {@link androidx.viewpager2.widget.ViewPager2}.
 * <p>This class is responsible for providing fragments to be displayed in the ViewPager2 and
 * dynamically managing the fragments based on the position of the page.</p>
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor for the ViewPagerAdapter.
     *
     * @param fragmentActivity The {@link HomeTab} activity that holds the ViewPager2.
     */
    public ViewPagerAdapter(@NonNull HomeTab fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates a new fragment for the given position.
     *
     * @param position The position of the fragment to create (0 for HomeFragment, 1 for JoinedEventsFragment).
     * @return The corresponding fragment to display.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else {
            return new JoinedEventsFragment();
        }
    }

    /**
     * Returns the number of fragments/pages to be displayed in the ViewPager2.
     *
     * @return The number of pages (2 in this case: HomeFragment and JoinedEventsFragment).
     */
    @Override
    public int getItemCount() {
        return 2; // Number of pages
    }

    /**
     * Returns a unique ID for each fragment, based on the position.
     *
     * @param position The position of the fragment.
     * @return The unique ID for the fragment at the given position.
     */
    @Override
    public long getItemId(int position) {
        return position; // Unique ID for each fragment
    }

    /**
     * Determines if the item with the given ID exists.
     *
     * @param itemId The ID of the item to check.
     * @return True if the item exists, false otherwise.
     */
    @Override
    public boolean containsItem(long itemId) {
        return itemId < getItemCount();
    }
}
