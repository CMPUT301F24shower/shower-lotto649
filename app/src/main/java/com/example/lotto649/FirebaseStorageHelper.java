package com.example.lotto649;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageHelper {

    private static final String TAG = "FirebaseStorageHelper";

    /**
     * Uploads a profile image to Firebase Storage.
     *
     * @param imageUri   The Uri of the image to upload.
     * @param fileName   The name for the image file in Firebase Storage.
     * @param callback   A callback to handle success or failure.
     */
    public static void uploadProfileImageToFirebaseStorage(Uri imageUri, String fileName, final UploadCallback callback) {
        if (imageUri == null) {
            Log.e(TAG, "Image Uri is null");
            callback.onFailure("Image Uri is null");
            return;
        }

        // Reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference storageRef = storage.getReference().child("profileImages/" + fileName);
        // Start upload task
        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL after successful upload
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrlString = uri.toString();
                FirebaseFirestore.getInstance().collection("users").document(fileName.replace(".jpg", ""))
                        .update("profileImage", imageUrlString);
            });
        });
    }

    // Callback interface for upload results
    public interface UploadCallback {
        void onSuccess(Uri downloadUri);
        void onFailure(String errorMessage);
    }
}

