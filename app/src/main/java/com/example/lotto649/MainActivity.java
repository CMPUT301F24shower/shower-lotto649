/**
 * MainActivity is the entry point of the application that implements a bottom navigation bar
 * for switching between different fragments such as Home, Camera, and Account.
 * <p>
 * Code adapted from the following source for implementing a bottom navigation bar:
 * <a href="https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/">GeeksforGeeks: Bottom Navigation Bar in Android</a>
 * </p>
 */
package com.example.lotto649;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lotto649.Models.FirestoreIsAdminCallback;
import com.example.lotto649.Models.FirestoreUserCallback;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.Views.Fragments.AccountFragment;
import com.example.lotto649.Views.Fragments.AdminAndUserFragment;
import com.example.lotto649.Views.Fragments.BrowseEventsFragment;
import com.example.lotto649.Views.Fragments.BrowseFacilitiesFragment;
import com.example.lotto649.Views.Fragments.BrowseProfilesFragment;
import com.example.lotto649.Views.Fragments.CameraFragment;
import com.example.lotto649.Views.Fragments.FacilityFragment;
import com.example.lotto649.Views.Fragments.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;

    /**
     * Initializes the activity, setting up the bottom navigation view and its listener.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO this code is incomplete, just here to fix build errors
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_CODE_POST_NOTIFICATIONS);
        } else {
            // Permission already granted, post notifications
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().clear();

        // Set the listener for navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        checkUserAdminStatus(new FirestoreIsAdminCallback() {
            @Override
            public void onCallback(boolean isAdmin) {
                if (isAdmin) {
                    checkUserEntrantStatus(new FirestoreIsAdminCallback() {
                        @Override
                        public void onCallback(boolean isEntrant) {
                            if (isEntrant) {
                                bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_user_and_admin);
                                // Set the default selected item to "browseProfiles"
                                bottomNavigationView.setSelectedItemId(R.id.home);
                            } else {
                                bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_admin);
                                // Set the default selected item to "home"
                                bottomNavigationView.setSelectedItemId(R.id.home);
                            }
                        }
                    });
                } else {
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
                    // Set the default selected item to "home"
                    bottomNavigationView.setSelectedItemId(R.id.home);
                }
            }
        });
    }

    // Create fragment instances
    HomeFragment homeFragment = new HomeFragment();
    CameraFragment cameraFragment = new CameraFragment();
    AccountFragment accountFragment = new AccountFragment();
    FacilityFragment facilityFragment = new FacilityFragment();
    BrowseEventsFragment browseEventsFragment = new BrowseEventsFragment();
    BrowseProfilesFragment browseProfilesFragment = new BrowseProfilesFragment();
    BrowseFacilitiesFragment browseFacilitiesFragment = new BrowseFacilitiesFragment();
    AdminAndUserFragment adminAndUserFragment = new AdminAndUserFragment();

    /**
     * Handles the selection of items from the BottomNavigationView.
     * Replaces the current fragment in the fragment container with the selected fragment.
     *
     * @param item The selected menu item.
     * @return true if the item was successfully handled, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation based on the selected item ID
        if (item.getItemId() == R.id.home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.camera) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, cameraFragment)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.account) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, accountFragment)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.facility) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, facilityFragment)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.browseEvents) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, browseEventsFragment)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.browseFacilities) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, browseFacilitiesFragment)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.browseProfiles) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, browseProfilesFragment)
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.admin) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, adminAndUserFragment)
                    .commit();
            return true;
        } else {
            return false;
        }
    }



    private void checkUserAdminStatus(FirestoreIsAdminCallback firestoreIsAdminCallback) {
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(deviceId);

        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Boolean isAdmin = document.getBoolean("admin");
                        if (isAdmin != null && isAdmin) {
                            firestoreIsAdminCallback.onCallback(true);
                        } else {
                            firestoreIsAdminCallback.onCallback(false);
                        }
                    } else {
                        firestoreIsAdminCallback.onCallback(false);
                    }
                } else {
                    firestoreIsAdminCallback.onCallback(false);
                }
            }
        });
    }

    private void checkUserEntrantStatus(FirestoreIsAdminCallback firestoreIsAdminCallback) {
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(deviceId);

        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Boolean isAdmin = document.getBoolean("entrant");
                        if (isAdmin != null && isAdmin) {
                            firestoreIsAdminCallback.onCallback(true);
                        } else {
                            firestoreIsAdminCallback.onCallback(false);
                        }
                    } else {
                        firestoreIsAdminCallback.onCallback(false);
                    }
                } else {
                    firestoreIsAdminCallback.onCallback(false);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, post notifications
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

}
