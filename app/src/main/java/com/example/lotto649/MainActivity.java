/**
 * MainActivity is the entry point of the application that implements a bottom navigation bar
 * for switching between different fragments such as Home, Camera, and Account.
 * <p>
 * Code adapted from the following source for implementing a bottom navigation bar:
 * <a href="https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/">GeeksforGeeks: Bottom Navigation Bar in Android</a>
 * Notification permission code was adapted from this thread:
 * https://stackoverflow.com/questions/44305206/ask-permission-for-push-notification
 * </p>
 */
package com.example.lotto649;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.lotto649.Models.UserModel;
import com.example.lotto649.Views.Fragments.AccountFragment;
import com.example.lotto649.Views.Fragments.AdminAndUserFragment;
import com.example.lotto649.Views.Fragments.BrowseEventsFragment;
import com.example.lotto649.Views.Fragments.BrowseFacilitiesFragment;
import com.example.lotto649.Views.Fragments.BrowseProfilesFragment;
import com.example.lotto649.Views.Fragments.CameraFragment;
import com.example.lotto649.Views.Fragments.FacilityFragment;
import com.example.lotto649.Views.Fragments.HomeFragment;
import com.example.lotto649.Views.Fragments.JoinEventFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    private MutableLiveData<Integer> whichMenuToShow;
    DocumentReference userRef;
    Boolean newEventSeen;

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
        newEventSeen = false;
        // TODO this code is incomplete, just here to fix build errors
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().clear();

        // Set the listener for navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set the default selected item to "account"
        MyApp.getInstance().setCurrentActivity(this);
        MyApp.getInstance().replaceFragment(accountFragment);
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        bottomNavigationView.setSelectedItemId(R.id.home);

        whichMenuToShow = new MutableLiveData<Integer>(1);
        whichMenuToShow.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer changedValue) {
                if (changedValue.intValue() == 1) {
                    int id = bottomNavigationView.getSelectedItemId();
                    removeMenuItems();
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
                    if (id == R.id.home || id == R.id.camera || id == R.id.facility || id == R.id.account) {
                        bottomNavigationView.setSelectedItemId(id);
                    } else {
                        bottomNavigationView.setSelectedItemId(R.id.home);
                    }
                    handleDeeplink();
                } else if (changedValue.intValue() == 2) {
                    removeMenuItems();
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_admin);
                    bottomNavigationView.setSelectedItemId(R.id.browseProfiles);
                    handleDeeplink();
                } else {
                    int id = bottomNavigationView.getSelectedItemId();
                    removeMenuItems();
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_user_and_admin);
                    if (id == R.id.home || id == R.id.camera || id == R.id.facility || id == R.id.account) {
                        bottomNavigationView.setSelectedItemId(id);
                    } else {
                        bottomNavigationView.setSelectedItemId(R.id.home);
                    }
                    handleDeeplink();
                }
            }
        });

        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Boolean isAdmin = documentSnapshot.getBoolean("admin");
                    Boolean isEntrant = documentSnapshot.getBoolean("entrant");
                    if (isAdmin != null && isAdmin) {
                        if (isEntrant != null && isEntrant) {
                            whichMenuToShow.setValue(3);
                        } else {
                            whichMenuToShow.setValue(2);
                        }
                    } else {
                        whichMenuToShow.setValue(1);
                    }
                } else {
                    whichMenuToShow.setValue(1);
                }
            }
        });

        createMenuByUserType();

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
        if (isFinishing() || isDestroyed()) {
            return false; // Don't perform the transaction if the activity is finishing or destroyed
        }
        // Handle navigation based on the selected item ID
        if (item.getItemId() == R.id.home) {
            MyApp.getInstance().replaceFragment(homeFragment);
            return true;
        } else if (item.getItemId() == R.id.camera) {
            MyApp.getInstance().replaceFragment(cameraFragment);
            return true;
        } else if (item.getItemId() == R.id.account) {
            MyApp.getInstance().replaceFragment(accountFragment);
            return true;
        } else if (item.getItemId() == R.id.facility) {
            MyApp.getInstance().replaceFragment(facilityFragment);
            return true;
        } else if (item.getItemId() == R.id.browseEvents) {
            MyApp.getInstance().replaceFragment(browseEventsFragment);

            return true;
        } else if (item.getItemId() == R.id.browseFacilities) {
            MyApp.getInstance().replaceFragment(browseFacilitiesFragment);

            return true;
        } else if (item.getItemId() == R.id.browseProfiles) {
            MyApp.getInstance().replaceFragment(browseProfilesFragment);

            return true;
        } else if (item.getItemId() == R.id.browseProfiles) {
            MyApp.getInstance().replaceFragment(browseProfilesFragment);

            return true;
        } else if (item.getItemId() == R.id.admin) {
            MyApp.getInstance().replaceFragment(adminAndUserFragment);

            return true;
        } else if (item.getItemId() == R.id.browseEvents) {
            MyApp.getInstance().replaceFragment(browseEventsFragment);
            return true;
        } else if (item.getItemId() == R.id.browseFacilities) {
            MyApp.getInstance().replaceFragment(browseFacilitiesFragment);
            return true;
        } else if (item.getItemId() == R.id.browseProfiles) {
            MyApp.getInstance().replaceFragment(browseProfilesFragment);
            return true;
        } else if (item.getItemId() == R.id.admin) {
            MyApp.getInstance().replaceFragment(adminAndUserFragment);
            return true;
        } else {
            return false;
        }
    }

    private void removeMenuItems() {
        bottomNavigationView.getMenu().removeItem(R.id.home);
        bottomNavigationView.getMenu().removeItem(R.id.camera);
        bottomNavigationView.getMenu().removeItem(R.id.facility);
        bottomNavigationView.getMenu().removeItem(R.id.account);
        bottomNavigationView.getMenu().removeItem(R.id.browseProfiles);
        bottomNavigationView.getMenu().removeItem(R.id.browseFacilities);
        bottomNavigationView.getMenu().removeItem(R.id.browseEvents);
        bottomNavigationView.getMenu().removeItem(R.id.admin);
    }

    private void handleDeeplink() {
        // TODO this should not happen if the user is not an entrant
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String url = getIntent().getData().toString();
            Uri uri = Uri.parse(url);
            String eventId = uri.getQueryParameter("eventId");
            // TODO because of async of getting user details, the first time this will get overwritten and replaced with home fragment, not sure how to fix this yet
            Bundle bundle = new Bundle();
            bundle.putString("firestoreEventId", eventId);
            JoinEventFragment frag = new JoinEventFragment();
            frag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, frag, null)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // Go back to the previous fragment
        } else {
            super.onBackPressed(); // Exit the activity
        }
    }

    /**
     * Checks if the current user has an admin status in the Firestore database.
     * This method retrieves the device ID, then queries the "users" collection in Firestore
     * to determine if the user has admin privileges.
     *
     */
    private void createMenuByUserType() {
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference doc = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        doc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Boolean isAdmin = document.getBoolean("admin");
                    Boolean isEntrant = document.getBoolean("entrant");
                    if (isAdmin != null && isAdmin) {
                        if (isEntrant != null && isEntrant) {
                            whichMenuToShow.setValue(3);
                        } else {
                            whichMenuToShow.setValue(2);
                        }
                    } else {
                        whichMenuToShow.setValue(1);
                    }
                } else {
                    whichMenuToShow.setValue(1);
                }
            } else {
                whichMenuToShow.setValue(1);
            }
        });
    }

    /**
     * Callback method that is invoked when the user responds to a permission request.
     * This method handles the result of the {@link android.Manifest.permission#POST_NOTIFICATIONS} permission
     * request, specifically checking if the permission was granted or denied.
     *
     * @param requestCode  The request code passed in {@link ActivityCompat#requestPermissions(Activity, String[], int)}.
     *                     It identifies which permission request is being handled.
     * @param permissions  An array of permissions that were requested.
     * @param grantResults An array of permission grant results corresponding to the permissions in the `permissions` array.
     *                     Each element in the array is either {@link PackageManager#PERMISSION_GRANTED} or
     *                     {@link PackageManager#PERMISSION_DENIED}.
     *
     * If the requestCode matches {@link #REQUEST_CODE_POST_NOTIFICATIONS}, the method checks if the user has granted
     * or denied the {@link andorid.Manifest.permission#POST_NOTIFICATIONS} permission:
     * <ul>
     *     <li>If the permission is granted, proceed with posting notifications.</li>
     *     <li>If the permission is denied, handle accordingly (e.g., show a message to the user).</li>
     * </ul>
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted
            } else {
                // NOT granted
            }
        }
    }

}
