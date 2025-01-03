package com.example.lotto649.Views.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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
import java.util.Objects;

/**
 * A fragment that displays a list of users who are waiting for an event. The list is populated
 * by querying Firestore for users who have signed up for the event. It also provides a back button
 * to navigate back to the previous screen.
 */
public class WaitingListFragment extends Fragment {
    ExtendedFloatingActionButton backButton;
    private ArrayList<String> deviceIdList;
    private ArrayList<UserModel> dataList;
    private ListView browseProfilesList;
    private BrowseProfilesArrayAdapter profilesAdapter;
    private FirebaseFirestore db;
    private String firestoreEventId;

    /**
     * Called to create and initialize the fragment's view. It sets up the Firestore database,
     * fetches the users' data, binds the data to the list view, and sets up event listeners
     * for user interactions.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState A bundle containing the state of the fragment if it was previously saved.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firestoreEventId = getArguments().getString("firestoreEventId");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_profiles, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();

        // fill dataList from Firestore
        dataList = new ArrayList<UserModel>();
        deviceIdList = new ArrayList<String>();

        browseProfilesList = view.findViewById(R.id.browse_profiles_list);
        profilesAdapter = new BrowseProfilesArrayAdapter(view.getContext(), dataList);
        browseProfilesList.setAdapter(profilesAdapter);

        backButton = view.findViewById(R.id.back_button);

        ConstraintLayout layout = view.findViewById(R.id.browse_profiles_layout);
        Context context = getContext();
        MutableLiveData<Boolean> noWaitingUsers = new MutableLiveData<>(Boolean.TRUE);

        TextView textView;
        if (context != null) {
            textView = new TextView(context);
        } else {
            textView = null;
        }

        db.collection("signUps").whereEqualTo("eventId", firestoreEventId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String deviceId = doc.getString("userId");
                        db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                noWaitingUsers.setValue(Boolean.FALSE);
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

        noWaitingUsers.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (Objects.equals(aBoolean, Boolean.TRUE)) {
                    if (textView != null) {
                        if (textView.getParent() != null) {
                            ((ViewGroup) textView.getParent()).removeView(textView);
                        }
                        textView.setId(View.generateViewId()); // Generate an ID for the TextView
                        textView.setText("not waiting for any users");
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
                } else {
                    if (textView != null) {
                        textView.setVisibility(View.GONE);
                    }
                }
            }
        });

        browseProfilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenUserId = deviceIdList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("userDeviceId", chosenUserId);
                bundle.putString("firestoreEventId", firestoreEventId);
                WaitingListProfileFragment frag = new WaitingListProfileFragment();
                frag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, frag, null)
                        .addToBackStack(null)
                        .commit();
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


