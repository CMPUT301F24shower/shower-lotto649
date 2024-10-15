/**
 * Code used from the following source for menu bar
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 */

package com.example.lotto649;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import java.io.*;

public class ThirdFragment extends Fragment {

    public ThirdFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_third, container, false);
    }
}
