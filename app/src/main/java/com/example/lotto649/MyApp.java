package com.example.lotto649;

import android.app.Application;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.lotto649.Models.UserModel;
import com.example.lotto649.Views.Fragments.EventFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;

public class MyApp extends Application {
    private UserModel user;
    private static MyApp instance;
    private WeakReference<FragmentActivity> currentActivity;


    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Use getApplicationContext() instead of getContext()
        user = new UserModel(getApplicationContext(), FirebaseFirestore.getInstance());
    }

    public UserModel getUserModel() {
        return user;
    }

    public static void setInstance(MyApp instance) {
        MyApp.instance = instance;
    }

    public void setUserModel(UserModel user) {
        this.user = user;
    }

    // Method to set the current activity
    public void setCurrentActivity(FragmentActivity activity) {
        this.currentActivity = new WeakReference<>(activity);
    }
    
    // Fragment replacement method
    public void replaceFragment(Fragment fragment) {
        if (currentActivity != null) {
            currentActivity.get().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .commit();
        }
    }
}
