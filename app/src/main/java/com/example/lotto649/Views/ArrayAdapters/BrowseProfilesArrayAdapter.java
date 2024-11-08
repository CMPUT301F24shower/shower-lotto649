package com.example.lotto649.Views.ArrayAdapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class BrowseProfilesArrayAdapter extends ArrayAdapter<UserModel> {
    private Uri profileUri;

    public BrowseProfilesArrayAdapter(Context context, ArrayList<UserModel> users) {
        super(context, 0, users);
    }

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
        if (!Objects.equals(profileUriString, "")) {
            profileUri = Uri.parse(profileUriString);
            StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(profileUriString);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                profileUri = uri;
                Glide.with(getContext())
                        .load(uri)
                        .into(profileImage);
                // TODO: This is hardcoded, but works good on my phone, not sure if this is a good idea or not
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                profileImage.setLayoutParams(layoutParams);
                profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                profileImage.setVisibility(View.VISIBLE);
                imagePlaceholder.setVisibility(View.GONE);
            });
        } else {
            profileImage.setVisibility(View.GONE);
            imagePlaceholder.setVisibility(View.VISIBLE);
        }
        userName.setText(user.getName());
        userEmail.setText(user.getEmail());
        if (user.getPhone().isEmpty()) {
            userPhone.setVisibility(View.GONE);
        } else {
            userPhone.setVisibility(View.VISIBLE);
            userPhone.setText(user.getPhone());
        }


        return view;
    }
}
