package com.example.lotto649.Views.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lotto649.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.ByteArrayOutputStream;

/**
 * QrFragment is a fragment class that displays a QR code image.
 * It also provides functionality for generating a QR code from a given string.
 */
public class QrFragment extends Fragment {

    /**
     * Creates a new instance of QrFragment with the given QR code bitmap.
     *
     * @param bitmap The bitmap of the QR code to display.
     * @return A new instance of QrFragment containing the QR code image.
     */
    public static QrFragment newInstance(Bitmap bitmap){
        QrFragment fragment = new QrFragment();
        Bundle args = new Bundle();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        args.putByteArray("qr_code_bitmap", byteArray);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Inflates the fragment's view, retrieves the QR code image from the arguments,
     * and displays it in an ImageView. Also sets up a back button to navigate to the HomeFragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        ImageView qrCodeImageView = view.findViewById(R.id.qrCodeImageView);
        ExtendedFloatingActionButton backButton = view.findViewById(R.id.qrCodeBackButton);

        if (getArguments() != null) {
            byte[] byteArray = getArguments().getByteArray("qr_code_bitmap");
            if (byteArray != null) {
                Bitmap qrCodeBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(getContext(), "No QR code image provided", Toast.LENGTH_SHORT).show();
            }
        }

        backButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}