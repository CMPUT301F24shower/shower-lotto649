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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class QrFragment extends Fragment {
    private static final String ARG_QR_DATA = "qr_data";
    private ImageView qrCodeImageView;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);
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

    private void generateQRCode(String data) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            int size = 512;
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

            // Encode the data to generate the QR code bitmap
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }

            qrCodeImageView.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
