package com.example.lotto649.Views.ArrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lotto649.Models.UserModel;
import com.example.lotto649.R;

import java.util.ArrayList;

public class BrowseProfilesArrayAdapter extends ArrayAdapter<UserModel> {
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
        ImageView userImage = view.findViewById(R.id.user_image);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userEmail = view.findViewById(R.id.user_email);
        TextView userPhone = view.findViewById(R.id.user_phone);
        // TODO: set image from user when it exists
        userImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_default_image, null));
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
