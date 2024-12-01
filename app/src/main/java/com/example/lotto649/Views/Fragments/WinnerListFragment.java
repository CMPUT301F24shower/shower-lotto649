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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseProfilesArrayAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
 * <p>
 * Code for creating context from:
 * https://stackoverflow.com/questions/47987649/why-getcontext-in-fragment-sometimes-returns-null
 * </p>
 */
public class WinnerListFragment extends Fragment {
    private ArrayList<String> winnerList, deviceIdList;
    private ArrayList<UserModel> dataList;
    private ListView browseProfilesList;
    private BrowseProfilesArrayAdapter profilesAdapter;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private Context mContext;
    ExtendedFloatingActionButton backButton;
    private String firestoreEventId;

    /**
     * Public empty constructor for BrowseFacilitiesFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public WinnerListFragment() {
        // Required empty public constructor
    }

    /**
     * Attaches the fragment to the app, and sets the context
     * @param context the given context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firestoreEventId = getArguments().getString("firestoreEventId");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_profiles, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        // fill dataList from Firestore
        dataList = new ArrayList<UserModel>();
        deviceIdList = new ArrayList<String>();

        browseProfilesList = view.findViewById(R.id.browse_profiles_list);
        profilesAdapter = new BrowseProfilesArrayAdapter(view.getContext(), dataList);
        browseProfilesList.setAdapter(profilesAdapter);

        backButton = view.findViewById(R.id.back_button);
        // TODO we want this
//        MutableLiveData<Boolean> noAccepts = new MutableLiveData<>(Boolean.TRUE);
//        noAccepts.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                if (Objects.equals(aBoolean, Boolean.TRUE)) {
//                    ConstraintLayout layout = view.findViewById(R.id.browse_profiles_layout);
//                    Context context = getContext();
//                    if (context != null) {
//                        TextView textView = new TextView(getContext());
//                        textView.setId(View.generateViewId()); // Generate an ID for the TextView
//                        textView.setText("No Users have been invited yet");
//                        textView.setTextSize(24);
//                        textView.setGravity(Gravity.CENTER);
//                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); // Update with your color
//
//                        // Set layout params for the TextView to match parent constraints
//                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
//                                ConstraintLayout.LayoutParams.MATCH_PARENT,
//                                ConstraintLayout.LayoutParams.WRAP_CONTENT
//                        );
//
//                        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
//                        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
//                        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
//                        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
//
//                        textView.setLayoutParams(params);
//
//                        // Add the TextView to the layout
//                        layout.addView(textView);
//                    } else {
//
//                    }
//                }
//            }
//        });
        db.collection("winners").whereEqualTo("eventId", firestoreEventId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();

                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String deviceId = doc.getString("userId");
                        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
//                                noAccepts.setValue(Boolean.FALSE);
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
                }
            }
        });

        browseProfilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userId = (String) deviceIdList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("eventId", firestoreEventId);
                bundle.putString("userId", userId);
                WinnerListProfileFragment frag = new WinnerListProfileFragment();
                frag.setArguments(bundle);
                MyApp.getInstance().addFragmentToStack(frag);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

        return view;
    }
}


