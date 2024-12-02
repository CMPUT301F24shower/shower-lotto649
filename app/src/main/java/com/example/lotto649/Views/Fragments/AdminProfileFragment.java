/**
 * A fragment to display a given profile's information.
 * This is used by an admin user to manage a profile.
 * This fragment is reached through a list of profiles in the admin view.
 */
package com.example.lotto649.Views.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.lotto649.FirestoreHelper;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A fragment to display a given profile's information.
 * This is used by an admin user to manage a profile.
 * This fragment is reached through a list of profiles in the admin view.
 */
public class AdminProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private TextView imagePlaceholder;
    private LinearLayout linearLayout;
    private ImageView profileImage;
    private Uri profileUri;
    private String nameText;
    private MutableLiveData<Boolean> imageAbleToBeDeleted;
    TextView name;
    TextView email;
    TextView phone;
    TextView roles;
    Button removeImage;
    Button removeUser;
    ExtendedFloatingActionButton backButton;

    /**
     * Public empty constructor for BrowseEventsFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public AdminProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     * This method inflates the layout defined in `fragment_browse_events.xml`.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the camera fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get info from bundle
        String userDeviceId = getArguments().getString("userDeviceId");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_view_profile, container, false);

        imageAbleToBeDeleted = new MutableLiveData<Boolean>(Boolean.FALSE);
        // https://stackoverflow.com/questions/14457711/android-listening-for-variable-changes
        imageAbleToBeDeleted.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (Objects.equals(changedValue, Boolean.TRUE)) {
                    removeImage.setVisibility(View.VISIBLE);
                } else {
                    removeImage.setVisibility(View.GONE);
                }
            }
        });

        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        name = view.findViewById(R.id.admin_user_name);
        email = view.findViewById(R.id.admin_user_email);
        phone = view.findViewById(R.id.admin_user_phone);
        roles = view.findViewById(R.id.admin_user_roles);
        removeImage = view.findViewById(R.id.admin_delete_user_image);
        removeUser = view.findViewById(R.id.admin_delete_user);
        profileImage = new ImageView(getContext());
        profileImage.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(450, 450);
        profileImage.setLayoutParams(layoutParams);
        profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        linearLayout = view.findViewById(R.id.admin_profile_picture_layout);
        imagePlaceholder = view.findViewById(R.id.admin_profile_image_placeholder);
        backButton = view.findViewById(R.id.back_button);


        db.collection("users")
                .document(userDeviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            nameText = doc.getString("name");
                            String emailText = doc.getString("email");
                            String phoneText = doc.getString("phone");
                            Boolean isAdmin = doc.getBoolean("admin");
                            Boolean isOrganizer = doc.getBoolean("organizer");
                            Boolean isEntrant = doc.getBoolean("entrant");
                            StringBuilder rolesBuilder = new StringBuilder();
                            rolesBuilder.append("Roles: ");
                            if (Boolean.TRUE.equals(isAdmin)) {
                                // Admins should not be deleted in the app
                                removeUser.setVisibility(View.GONE);
                                rolesBuilder.append("Admin, ");
                            }
                            if (Boolean.TRUE.equals(isOrganizer)) {
                                rolesBuilder.append("Organizer, ");
                            }
                            if (Boolean.TRUE.equals(isEntrant)) {
                                rolesBuilder.append("Entrant, ");
                            }
                            String rolesText = rolesBuilder.toString().substring(0, rolesBuilder.toString().length() - 2);
                            name.setText(nameText);
                            email.setText(emailText);
                            if (phoneText == null || phoneText.isEmpty()) {
                                phone.setVisibility(View.GONE);
                            } else {
                                phone.setVisibility(View.VISIBLE);
                                phone.setText(phoneText);
                            }
                            roles.setText(rolesText);

                        //     getting profile image
                            imagePlaceholder.setText(new UserModel(getContext(), nameText, emailText).getInitials());
                            String profileUriString = doc.getString("profileImage");
                            if (profileUriString != null && !Objects.equals(profileUriString, "")) {
                                imageAbleToBeDeleted.setValue(Boolean.TRUE);
                                profileUri = Uri.parse(profileUriString);
                                StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(profileUriString);
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            profileUri = uri;
                                            Glide.with(getContext())
                                                    .load(uri)
                                                    .into(profileImage);
                                            linearLayout.removeView(imagePlaceholder);
                                            linearLayout.addView(profileImage, 2);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getActivity(), "Unable to fetch profile image", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                profileUri = null;
                                imageAbleToBeDeleted.setValue(Boolean.FALSE);
                            }
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.getInstance().popFragment();
            }
        });

        removeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirestoreHelper.getInstance().deleteFacility(userDeviceId);
                db.collection("signUps")
                        .whereEqualTo("userId", userDeviceId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Loop through the documents in the query result
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Delete each document
                                    db.collection("signUps").document(document.getId()).delete();
                                }
                            }
                        });
                db.collection("winners")
                        .whereEqualTo("userId", userDeviceId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Loop through the documents in the query result
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Delete each document
                                    db.collection("signUps").document(document.getId()).delete();
                                }
                            }
                        });
                db.collection("noSelected")
                        .whereEqualTo("userId", userDeviceId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Loop through the documents in the query result
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Delete each document
                                    db.collection("signUps").document(document.getId()).delete();
                                }
                            }
                        });
                db.collection("enrolled")
                        .whereEqualTo("userId", userDeviceId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Loop through the documents in the query result
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Delete each document
                                    db.collection("signUps").document(document.getId()).delete();
                                }
                            }
                        });
                db.collection("cancelled")
                        .whereEqualTo("userId", userDeviceId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    db.collection("signUps").document(document.getId()).delete();
                                }
                            }
                        });
                usersRef
                        .document(userDeviceId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                BrowseProfilesFragment frag = new BrowseProfilesFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.flFragment, frag, null)
                                        .addToBackStack(null)
                                        .commit();
                                //     add success log
                            }
                        });
            }
        });

        removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(profileUri.toString());
                    storageRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    usersRef
                                            .document(userDeviceId)
                                            .update("profileImage", "")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //     add success log
                                                }
                                            });
                                    profileUri = null;
                                    linearLayout.removeView(profileImage);
                                    linearLayout.addView(imagePlaceholder, 2);
                                    linearLayout.removeView(profileImage);
                                    imageAbleToBeDeleted.setValue(Boolean.FALSE);
                                    imagePlaceholder.setText(new UserModel(getContext(), nameText, "").getInitials());
                                }
                            });
                }
            }
        });


        return view;
    }
}
