/**
 * WaitingListFragment class represents a fragment for the organizer to browse all profiles
 * associated with a specific event in the application.
 * <p>
 * This fragment displays a list of user profiles that have signed up for the event.
 * Users with 'organizer' status can view and interact with the profiles. Selecting a profile
 * will show its full details in another fragment. If no users have signed up, a message is displayed.
 * </p>
 * <p>
 * This fragment features:
 * <ul>
 *   <li>A dynamic list view of user profiles using {@link BrowseProfilesArrayAdapter}</li>
 *   <li>Firestore database integration for fetching user data</li>
 *   <li>A navigation mechanism to move to individual user profiles</li>
 * </ul>
 * </p>
 * <p>
 * External code references:
 * <ul>
 *   <li>Code for the bottom navigation bar was adapted from:
 *       <a href="https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/">
 *           Bottom Navigation Bar in Android
 *       </a>
 *   </li>
 *   <li>Code for creating context was adapted from:
 *       <a href="https://stackoverflow.com/questions/47987649/why-getcontext-in-fragment-sometimes-returns-null">
 *           Why getContext() in Fragment sometimes returns null
 *       </a>
 *   </li>
 * </ul>
 * </p>
 *
 * <p><strong>Note:</strong> This page is only accessible to users with 'organizer' status.</p>
 */
package com.example.lotto649.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseProfilesArrayAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WaitingListFragment class represents a fragment for the organizer to browse all profiles associated
 * with a specific event in the application. It displays a list of user profiles that signed up for the event.
 * <p>
 * Selecting a profile will show its full details in another fragment.
 * If no users have signed up, a message will be displayed instead.
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 * <p>
 * Code for creating context was adapted from:
 * https://stackoverflow.com/questions/47987649/why-getcontext-in-fragment-sometimes-returns-null
 * </p>
 */
public class WaitingListFragment extends Fragment {
    private ArrayList<String> deviceIdList;
    private ArrayList<UserModel> dataList;
    private ListView browseProfilesList;
    private BrowseProfilesArrayAdapter profilesAdapter;
    private FirebaseFirestore db;
    ExtendedFloatingActionButton backButton;
    private String firestoreEventId;

    /**
     * Called to create the view hierarchy associated with this fragment.
     *
     * @param inflater  LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firestoreEventId = getArguments().getString("firestoreEventId");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_profiles, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // fill dataList from Firestore
        dataList = new ArrayList<UserModel>();
        deviceIdList = new ArrayList<String>();

        browseProfilesList = view.findViewById(R.id.browse_profiles_list);
        profilesAdapter = new BrowseProfilesArrayAdapter(view.getContext(), dataList);
        browseProfilesList.setAdapter(profilesAdapter);

        backButton = view.findViewById(R.id.back_button);

        db.collection("signUps").whereEqualTo("eventId", firestoreEventId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();
                    AtomicBoolean noSignUps = new AtomicBoolean(true);
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String deviceId = doc.getString("userId");
                        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                noSignUps.set(false);
                                DocumentSnapshot userDoc = task.getResult();
                                String deviceIdText = userDoc.getId();
                                String nameText = userDoc.getString("name");
                                String emailText = userDoc.getString("email");
                                String phoneText = userDoc.getString("phone");
                                Context context = getContext();
                                UserModel newUser;
                                if (context != null) {
                                    newUser = new UserModel(context, nameText, emailText, phoneText, null);
                                    newUser.setProfileImage(userDoc.getString("profileImage"));
                                    dataList.add(newUser);
                                    deviceIdList.add(deviceIdText);
                                    profilesAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    if (noSignUps.get()) {
                        ConstraintLayout layout = view.findViewById(R.id.browse_profiles_layout);
                        Context context = getContext();
                        if (context != null) {
                            TextView textView = new TextView(getContext());
                            textView.setId(View.generateViewId()); // Generate an ID for the TextView
                            textView.setText("No Users have signed up yet");
                            textView.setTextSize(24);
                            textView.setGravity(Gravity.CENTER);
                            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); // Update with your color

                            // Set layout params for the TextView to match parent constraints
                            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                            );

                            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

                            textView.setLayoutParams(params);

                            // Add the TextView to the layout
                            layout.addView(textView);
                        }
                    }
                }
            }
        });

        /**
         * Sets up the click listener for the list view items.
         * Navigates to the WaitingListProfileFragment to display profile details.
         */
        browseProfilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenUserId = (String) deviceIdList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("userDeviceId", chosenUserId);
                bundle.putString("firestoreEventId", firestoreEventId);
                WaitingListProfileFragment frag = new WaitingListProfileFragment();
                frag.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(frag);
            }
        });

        /**
         * Sets up the click listener for the back button.
         * Navigates to the previous fragment in the stack.
         */
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

        return view;
    }
}
