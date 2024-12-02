/**
 * Custom Application class for managing the global state of the app, including user data and current activity.
 *
 * <p>This class provides access to the global {@link UserModel} instance, manages the
 * current {@link FragmentActivity} reference, and allows fragment replacement within
 * the current activity's layout.</p>
 */
package com.example.lotto649;

import static android.app.PendingIntent.getActivity;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.lotto649.Models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;

public class MyApp extends Application {
    private UserModel user;
    private static MyApp instance;
    private WeakReference<FragmentActivity> currentActivity;

    /**
     * Retrieves the singleton instance of MyApp.
     *
     * @return The MyApp instance.
     */
    public static MyApp getInstance() {
        return instance;
    }

    /**
     * Initializes the MyApp instance and sets up the global {@link UserModel} object.
     * Called when the application is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Use getApplicationContext() instead of getContext()
        user = new UserModel(getApplicationContext(), FirebaseFirestore.getInstance());
    }

    /**
     * Retrieves the global {@link UserModel} instance associated with the application.
     *
     * @return The current UserModel instance.
     */
    public UserModel getUserModel() {
        return user;
    }

    /**
     * Sets the singleton instance of MyApp.
     *
     * @param instance The MyApp instance to set.
     */
    public static void setInstance(MyApp instance) {
        MyApp.instance = instance;
    }

    /**
     * Updates the global {@link UserModel} instance.
     *
     * @param user The UserModel to set as the global user.
     */
    public void setUserModel(UserModel user) {
        this.user = user;
    }

    /**
     * Sets the current activity reference for managing fragment transactions.
     *
     * @param activity The FragmentActivity to set as the current activity.
     */
    // Method to set the current activity
    public void setCurrentActivity(FragmentActivity activity) {
        this.currentActivity = new WeakReference<>(activity);
    }

    /**
     * Replaces the current fragment in the activity with the specified fragment.
     *
     * <p>This method replaces the fragment within the current activity's container,
     * defined by the R.id.flFragment ID, if the current activity is set.</p>
     *
     * @param fragment The fragment to display in the current activity.
     */
    // Fragment replacement method
    public void replaceFragment(Fragment fragment) {
        if (currentActivity != null) {
            FragmentManager fragmentManager = currentActivity.get().getSupportFragmentManager();

            // Clear the back stack
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // Replace the fragment
            fragmentManager.beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .commit();
        }
    }

    /**
     * Replaces the current fragment in the activity with the specified fragment.
     *
     * <p>This method replaces the fragment within the current activity's container,
     * defined by the R.id.flFragment ID, if the current activity is set.</p>
     *
     * @param fragment The fragment to display in the current activity.
     */
    public void addFragmentToStack(Fragment fragment) {
        if (currentActivity != null) {
            currentActivity.get().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Pops the top fragment from the back stack.
     *
     * <p>This method allows the app to navigate back to the previous fragment in the stack.</p>
     */
    public void popFragment() {
        if (currentActivity != null) {
            currentActivity.get().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    /**
     * Enum representing the state of an event.
     * <p>This enum is used to track the state of an event in the application.</p>
     */
    public enum EventState {
        OPEN,
        WAITING,
        CLOSED
    }
}
