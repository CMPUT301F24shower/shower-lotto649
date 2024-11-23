/**
 * Helper class for uploading images to Firebase Storage and updating user profile information in Firestore.
 *
 * <p>This class includes methods to upload a profile image to a specified location in Firebase Storage and
 * update the corresponding user's profile image URL in Firestore.</p>
 * This code was adapted from the firebase docs:
 * https://firebase.google.com/docs/storage/android/upload-files
 */
package com.example.lotto649;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseStorageHelper {


    /**
     * Uploads a profile image to Firebase Storage.
     *
     * <p>This method uploads an image file to Firebase Storage, stores it in the "profileImages"
     * directory with the specified file name, and updates the Firestore "users" collection with
     * the URL of the uploaded image. The Firestore document is identified by removing the ".jpg"
     * extension from the provided file name.</p>
     *
     * @param imageUri   The Uri of the image to upload.
     * @param fileName   The name to use for the image file in Firebase Storage (e.g., "user123.jpg").
     */
    public static void uploadProfileImageToFirebaseStorage(Uri imageUri, String fileName, AtomicReference<String> currentImageUriString, MutableLiveData<Boolean> imageAbleToBeDeleted) {
        Log.e("JASON TEST", "uploadProfileImageToFirebaseStorage");
        if (imageUri == null) {
            return;
        }
        imageAbleToBeDeleted.setValue(Boolean.FALSE);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference storageRef = storage.getReference().child("profileImages/" + fileName);
        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String uriString = uri.toString();
                currentImageUriString.set(uriString);
                imageAbleToBeDeleted.setValue(Boolean.TRUE);
                FirebaseFirestore.getInstance().collection("users").document(fileName.replace(".jpg", ""))
                        .update("profileImage", uriString);
            });
        });
    }



    /**
     * Uploads a poster image to Firebase Storage.
     *
     * <p>This method uploads an image file to Firebase Storage, stores it in the "posterImages"
     * directory with the specified file name, and updates the Firestore "events" collection with
     * the URL of the uploaded image. The Firestore document is identified by removing the ".jpg"
     * extension from the provided file name.</p>
     *
     * @param imageUri   The Uri of the image to upload.
     * @param fileName   The name to use for the image file in Firebase Storage (e.g., "user123.jpg").
     */
    public static void uploadPosterImageToFirebaseStorage(Uri imageUri, String fileName, AtomicReference<String> currentImageUriString, MutableLiveData<Boolean> imageAbleToBeDeleted) {
        if (imageUri == null) {
            return;
        }
        imageAbleToBeDeleted.setValue(Boolean.FALSE);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference storageRef = storage.getReference().child("posterImages/" + fileName);
        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String uriString = uri.toString();
                imageAbleToBeDeleted.setValue(Boolean.TRUE);
                currentImageUriString.set(uriString);
                FirebaseFirestore.getInstance().collection("events").document(fileName.replace(".jpg", ""))
                        .update("posterImage", uriString);
            });
        });
    }
}

