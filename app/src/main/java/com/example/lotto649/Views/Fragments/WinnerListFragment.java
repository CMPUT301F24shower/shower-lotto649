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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseProfilesArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

/**
 * WinnerListFragment class represents a fragment for the admin to view and manage
 * the winners of an event in the application.
 * <p>
 * This fragment displays a list of winners selected for an event. The admin can view detailed
 * information about each winner by selecting an item from the list. The page also ensures that
 * the correct number of winners is maintained based on the event configuration.
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 */
public class WinnerListFragment extends Fragment {
    private ArrayList<String> winnerList, deviceIdList;
    private ArrayList<UserModel> dataList;
    private ListView browseProfilesList;
    private BrowseProfilesArrayAdapter profilesAdapter;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private String firestoreEventId;
    private Context mContext;
    ExtendedFloatingActionButton backButton;

    private int numberOfSpots;

    /**
     * Attaches the fragment to the app, and sets the context.
     *
     * @param context the given context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     * <p>
     * This method inflates the layout for the winner list fragment and initializes all required
     * components, such as the list view, Firestore database references, and adapter for displaying winners.
     * </p>
     *
     * @param inflater  LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firestoreEventId = getArguments().getString("firestoreEventId");
        View view = inflater.inflate(R.layout.fragment_browse_profiles, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        // fill dataList from Firestore
        dataList = new ArrayList<UserModel>();
        deviceIdList = new ArrayList<String>();
        winnerList = new ArrayList<String>();

        browseProfilesList = view.findViewById(R.id.browse_profiles_list);
        profilesAdapter = new BrowseProfilesArrayAdapter(view.getContext(), dataList);
        browseProfilesList.setAdapter(profilesAdapter);

        backButton = view.findViewById(R.id.back_button);

//        db.collection("winners")
//                .whereEqualTo("firestoreEventId", firestoreEventId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
//                        Collections.shuffle(docs);
//                        for (DocumentSnapshot doc : docs) {
//                            if (winnerList.size() < numberOfSpots) {
//                                db.collection("winners").document(doc.getId()).delete();
//                            }
//                        }
//                    }
//                });

//        db.collection("signUps")
//                .whereEqualTo("firestoreEventId", firestoreEventId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
//                        Collections.shuffle(docs);
//                        for (DocumentSnapshot doc : docs) {
//                            if (winnerList.size() < numberOfSpots) {
//                                Log.e("Ohm", "Doc Id " + doc.getString("userId"));
//                                winnerList.add(doc.getString("userId"));
//
//                                db.collection("winners").add(doc.getData());
//                            }
//                        }
//                    }
//                });

        db.collection("events").document(firestoreEventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        numberOfSpots = ((Long) task.getResult().get("numberOfSpots")).intValue();
                    }
                });

        db.collection("winners")
                .whereEqualTo("firestoreEventId", firestoreEventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() <= numberOfSpots) {
                        db.collection("events").document(firestoreEventId).update("drawn",false);
                    } else if (task.isSuccessful() && task.getResult() != null) {
                        winnerList.clear();
                        deviceIdList.clear();

                        ArrayList<String> tempDeviceIdList  = new ArrayList<String>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            winnerList.add(doc.getId());
                            tempDeviceIdList.add(doc.getString("userId"));
                        }

                        if (!tempDeviceIdList.isEmpty()) {
                            userRef.whereIn(FieldPath.documentId(), tempDeviceIdList) // Use whereIn with document IDs
                                    .orderBy("name")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                return;
                                            }
                                            if (querySnapshots != null) {
                                                dataList.clear();
                                                for (QueryDocumentSnapshot doc : querySnapshots) {
                                                    String deviceIdText = doc.getId();
                                                    String nameText = doc.getString("name");
                                                    String emailText = doc.getString("email");
                                                    String phoneText = doc.getString("phone");

                                                    Log.e("Ohm",doc.getId() + " : " + doc.getString("name") + " | " + doc.getString("email") + " | " + doc.getString("phone"));
                                                    UserModel newUser = new UserModel(mContext, nameText, emailText, phoneText, null);
                                                    newUser.setProfileImage(doc.getString("profileImage"));
                                                    dataList.add(newUser);
                                                    deviceIdList.add(deviceIdText);
                                                }
                                                profilesAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });

        browseProfilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String signUpId = (String) winnerList.get(i);
                String userId = (String) deviceIdList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("signUpId", signUpId);
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


