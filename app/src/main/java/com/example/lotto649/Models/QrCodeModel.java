package com.example.lotto649.Models;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrCodeModel extends AbstractModel {
    private static Bitmap bitmap;

    public static Bitmap generateForEvent(String data){
        //TODO: Add actual code for QrCode gen
        QRCodeWriter writer = new QRCodeWriter();
        try {
            int size = 512;
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            // Encode the data to generate the QR code bitmap
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
