/**
 * CameraFragment class represents a fragment for camera-related functionality in the application.
 * <p>
 * This fragment inflates the camera layout when the user navigates to the camera section of the app.
 * It is part of the bottom navigation bar implementation used in the application.
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
 */
package com.example.lotto649.Views.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lotto649.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

// TODO this class can likely be deleted, along with the entire page
public class CameraFragment extends Fragment {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};
    private PreviewView previewView;

    /**
     * Public empty constructor for CameraFragment.
     * <p>
     * Required for prope   r instantiation of the fragment by the Android system.
     * </p>
     */
    public CameraFragment() {
        // Required empty public constructor
    }
    /**
     * Called to create the view hierarchy associated with this fragment.
     * This method inflates the layout defined in `fragment_camera.xml`.
     *
     * @param inflater LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the camera fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        previewView = view.findViewById(R.id.previewView);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        return view;
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                PreviewView previewView = requireView().findViewById(R.id.previewView);


                Preview preview = new Preview.Builder()
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                Camera camera = cameraProvider.bindToLifecycle(this, androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA, preview);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraFragment", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Log.e("CameraFragment", "Permissions not granted by the user.");
            }
        }
    }
}