/**
 * This is an array adapter for the admin view
 * for browsing user profiles. It maps an ArrayList of UserModels
 * to a ListView to display the needed information
 */
package com.example.lotto649.Views.ArrayAdapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This is an array adapter for the admin view
 * for browsing user profiles. It maps an ArrayList of UserModels
 * to a ListView to display the needed information
 */
public class BrowseProfilesArrayAdapter extends ArrayAdapter<UserModel> {
    private Uri profileUri;

    /**
     * Constructor for the array adapter
     *
     * @param context the context of the adapter
     * @param users   the user models to be adapted
     */
    public BrowseProfilesArrayAdapter(Context context, ArrayList<UserModel> users) {
        super(context, 0, users);
    }

    /**
     * Gets the list view item and displays information
     *
     * @param position    the position in the ListView that the adapter is for
     * @param convertView gives the ability to reuse an old view
     * @param parent      the parent of the adapter
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        } else {
            view = convertView;
        }
        UserModel user = getItem(position);
        assert user != null;
        TextView userName = view.findViewById(R.id.user_name);
        TextView userEmail = view.findViewById(R.id.user_email);
        TextView userPhone = view.findViewById(R.id.user_phone);
        TextView imagePlaceholder = view.findViewById(R.id.profile_image_placeholder);
        ImageView profileImage = view.findViewById(R.id.profile_image);
        String profileUriString = user.getProfileImage();
        String initials = user.getInitials();
        imagePlaceholder.setText(initials);
        if (!Objects.equals(profileUriString, "") && !(profileUriString == null)) {
            profileImage.setVisibility(View.VISIBLE);
            imagePlaceholder.setVisibility(View.GONE);
            profileUri = Uri.parse(profileUriString);
            StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(profileUriString);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                profileUri = uri;
                Glide.with(getContext())
                        .load(uri)
                        .into(profileImage);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                profileImage.setLayoutParams(layoutParams);
                profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            });
        } else {
            profileImage.setVisibility(View.GONE);
            imagePlaceholder.setVisibility(View.VISIBLE);
        }
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            userPhone.setVisibility(View.GONE);
        } else {
            userPhone.setVisibility(View.VISIBLE);
            userPhone.setText(user.getPhone());
        }


        return view;
    }
}
