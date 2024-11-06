package com.example.lotto649.Views.Fragments;


import android.os.Bundle;
import android.provider.Settings;
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
import com.example.lotto649.R;
import com.example.lotto649.Views.ArrayAdapters.BrowseFacilitiesArrayAdapter;
import com.example.lotto649.Views.ArrayAdapters.BrowseProfilesArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BrowseProfilesFragment extends Fragment {
    private ArrayList<UserModel> dataList;
    private ListView browseProfilesList;
    private BrowseProfilesArrayAdapter profilesAdapter;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    /**
     * Public empty constructor for BrowseProfilesFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public BrowseProfilesFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for this fragment
     */
    // TODO: BUG!! WHEN FIRST LOADED, IF LISTVIEW ITEM CLICKED, APP CRASHES
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_profiles, container, false);

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // fill dataList from Firestore
        dataList = new ArrayList<UserModel>();
        db.collection("users").whereEqualTo("entrant", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String nameText = doc.getString("name");
                                String emailText = doc.getString("email");
                                String phoneText = doc.getString("phone");
                                dataList.add(new UserModel(view.getContext(), nameText, emailText, phoneText, null));
                            }
                        } else {
                            Log.e("JASONERROR", "WHATTTT");
                        }
                    }
                });

        browseProfilesList = view.findViewById(R.id.browse_profiles_list);
        profilesAdapter = new BrowseProfilesArrayAdapter(view.getContext(), dataList);
        browseProfilesList.setAdapter(profilesAdapter);

        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                if (querySnapshots != null) {
                    dataList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String nameText = doc.getString("name");
                        String emailText = doc.getString("email");
                        String phoneText = doc.getString("phone");
                        dataList.add(new UserModel(view.getContext(), nameText, emailText, phoneText, null));
                        profilesAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        browseProfilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserModel chosenUser = (UserModel) browseProfilesList.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putString("userDeviceId", chosenUser.getDeviceId());
                AdminProfileFragment frag = new AdminProfileFragment();
                frag.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.browse_profiles_list, frag, null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}