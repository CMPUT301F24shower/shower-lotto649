package com.example.lotto649.Views.Fragments;

import android.graphics.Bitmap;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrFragment extends Fragment {
    private static final String ARG_QR_DATA = "qr_data";
    private ImageView qrCodeImageView;

    public static QrFragment newInstance(String qrData){
        QrFragment fragment = new QrFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QR_DATA, qrData);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);

        if (getArguments() != null) {
            String qrData = getArguments().getString(ARG_QR_DATA);
            if (qrData != null) {
                generateQRCode(qrData);
            } else {
                Toast.makeText(getContext(), "No data provided", Toast.LENGTH_SHORT).show();
            }
        }
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
