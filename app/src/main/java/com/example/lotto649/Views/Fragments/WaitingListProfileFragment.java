/**
 * A fragment to display a given profile's information.
 * This is used by an admin user to manage a profile.
 * This fragment is reached through a list of profiles in the admin view.
 */
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * A fragment to display and manage a user's profile information.
 * <p>
 * This fragment is used by an organizer user to view and manage a profile.
 * It allows the organizer to see details such as name, email, phone, roles,
 * and profile picture of the user. The organizer can also remove the user
 * from the event. This fragment is reached through a list of profiles
 * in the organizer view.
 * </p>
 */
public class WaitingListProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private TextView imagePlaceholder;
    private LinearLayout linearLayout;
    private ImageView profileImage;
    private Uri profileUri;
    private String nameText;
    TextView name;
    TextView email;
    TextView phone;
    TextView roles;
    Button removeUser;
    ExtendedFloatingActionButton backButton;
    String userDeviceId;
    String firestoreEventId;

    /**
     * Public empty constructor for WaitingListProfileFragment.
     * <p>
     * Required for proper instantiation of the fragment by the Android system.
     * </p>
     */
    public WaitingListProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with this fragment.
     * <p>
     * This method inflates the layout defined in `fragment_waiting_list_profile.xml`,
     * fetches the user profile details from Firestore, and initializes the UI components.
     * </p>
     *
     * @param inflater  LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the fragment's UI
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
        // TODO: This is hardcoded, but works good on my phone, not sure if this is a good idea or not
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
                usersRef.document(firestoreEventId + "_" + userDeviceId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MyApp.getInstance().popFragment();
                            }
                        });
            }
        });

        return view;
    }
}
