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

import com.example.lotto649.Models.FacilityModel;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseFacilitiesArrayAdapter;
import com.example.lotto649.Views.ArrayAdapters.BrowseProfilesArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
public class BrowseProfilesFragment extends Fragment {
    private ArrayList<String> deviceIdList;
    private ArrayList<UserModel> dataList;
    private ListView browseProfilesList;
    private BrowseProfilesArrayAdapter profilesAdapter;
    private FirebaseFirestore db;
    private CollectionReference userRef;
    private Context mContext;
    private ExtendedFloatingActionButton backButton;

    /**
     * Public empty constructor for BrowseFacilitiesFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public BrowseProfilesFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_profiles, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("users");

        // fill dataList from Firestore
        dataList = new ArrayList<UserModel>();
        deviceIdList = new ArrayList<String>();

        backButton = view.findViewById(R.id.back_button);
        browseProfilesList = view.findViewById(R.id.browse_profiles_list);
        profilesAdapter = new BrowseProfilesArrayAdapter(view.getContext(), dataList);
        browseProfilesList.setAdapter(profilesAdapter);

        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String deviceIdText = doc.getId();
                        String nameText = doc.getString("name");
                        String emailText = doc.getString("email");
                        String phoneText = doc.getString("phone");
                        UserModel newUser = new UserModel(mContext, nameText, emailText, phoneText, null);
                        newUser.setProfileImage(doc.getString("profileImage"));
                        dataList.add(newUser);
                        deviceIdList.add(deviceIdText);
                    }
                    profilesAdapter.notifyDataSetChanged();
                }
            }
        });

        browseProfilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String chosenUserId = (String) deviceIdList.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("userDeviceId", chosenUserId);
                AdminProfileFragment frag = new AdminProfileFragment();
                frag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flFragment, frag, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        backButton.setOnClickListener(new ExtendedFloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

        return view;
    }
}