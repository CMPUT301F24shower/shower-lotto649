package com.example.lotto649.Views.Fragments;

import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.example.lotto649.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

/**
 * A fragment that displays the profile details of a user who is waiting for an event, including their
 * name, email, phone, roles, and profile image. It provides an option to remove the user from the event
 * and navigate back to the previous screen.
 */
public class WaitingListProfileFragment extends Fragment {
    TextView name;
    TextView email;
    TextView phone;
    TextView roles;
    Button removeUser;
    ExtendedFloatingActionButton backButton;
    String userDeviceId;
    String firestoreEventId;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private TextView imagePlaceholder;
    private LinearLayout linearLayout;
    private ImageView profileImage;
    private Uri profileUri;
    private String nameText;

    /**
     * Public empty constructor for BrowseEventsFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public WaitingListProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create and initialize the fragment's view. It retrieves the user details from Firestore,
     * sets up the profile UI elements, and handles user actions like removing the user or navigating back.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState A bundle containing the state of the fragment if it was previously saved.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get info from bundle
        userDeviceId = getArguments().getString("userDeviceId");
        firestoreEventId = getArguments().getString("firestoreEventId");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waiting_list_profile, container, false);


        // initialize Firestore
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("signUps");

        name = view.findViewById(R.id.admin_user_name);
        email = view.findViewById(R.id.admin_user_email);
        phone = view.findViewById(R.id.admin_user_phone);
        roles = view.findViewById(R.id.admin_user_roles);
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
                            if (isAdmin) {
                                rolesBuilder.append("Admin, ");
                            }
                            if (isOrganizer) {
                                rolesBuilder.append("Organizer, ");
                            }
                            if (isEntrant) {
                                rolesBuilder.append("Entrant, ");
                            }
                            String rolesText = rolesBuilder.toString().substring(0, rolesBuilder.toString().length() - 2);
                            name.setText(nameText);
                            email.setText(emailText);
                            if (phoneText.isEmpty()) {
                                phone.setVisibility(View.GONE);
                            } else {
                                phone.setVisibility(View.VISIBLE);
                                phone.setText(phoneText);
                            }
                            roles.setText(rolesText);

                            //     getting profile image
                            imagePlaceholder.setText(new UserModel(getContext(), nameText, emailText).getInitials());
                            String profileUriString = doc.getString("profileImage");
                            if (!Objects.equals(profileUriString, "")) {
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
                db.collection("signUps")
                        .document(firestoreEventId + "_" + userDeviceId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot doc = task.getResult();
                                Map<String, Object> data = doc.getData();
                                if (data != null) {
                                    data.put("hasSeenNoti", false);
                                    db.collection("cancelled").document(firestoreEventId + "_" + userDeviceId).set(data);
                                    db.collection("signUps")
                                            .document(firestoreEventId + "_" + userDeviceId)
                                            .delete();
                                }
                            }
                        });
                MyApp.getInstance().popFragment();
            }
        });


        return view;
    }
}
