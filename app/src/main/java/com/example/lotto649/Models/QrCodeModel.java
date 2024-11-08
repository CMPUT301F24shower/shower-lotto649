package com.example.lotto649.Models;

import android.graphics.Bitmap;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * QrCodeModel class provides methods to generate a QR code image and create a SHA-256 hash.
 */
public class QrCodeModel extends AbstractModel {
    private static Bitmap bitmap;

    /**
     * Generates a QR code bitmap image based on the provided data string.
     *
     * @param data The string of data to encode into the QR code.
     * @return A Bitmap image of the generated QR code.
     */
    public static Bitmap generateForEvent(String data) {
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

    /**
     * Generates a SHA-256 hash of the input string.
     *
     * @param input The input string to hash.
     * @return A hexadecimal string representation of the SHA-256 hash.
     * @throws RuntimeException if SHA-256 algorithm is not available.
     */
    public static String generateHash(String input) {
        //TODO: Unhash string function for when we scan QR Code.
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}