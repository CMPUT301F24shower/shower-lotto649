package com.example.lotto649;

import android.app.Application;
import com.example.lotto649.Models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyApp extends Application {
    private UserModel userModel;

    @Override
    public void onCreate() {
        super.onCreate();
        // Use getApplicationContext() instead of getContext()
        userModel = new UserModel(getApplicationContext(), FirebaseFirestore.getInstance());
    }

    public UserModel getUserModel() {
        return userModel;
    }
}
