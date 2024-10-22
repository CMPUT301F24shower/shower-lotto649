package com.example.lotto649;

import android.content.Context;

public class AccountUserController {

    private UserModel userModel;

    // Controller constructor
    public AccountUserController() {
    }

    // Method to create a new user and save it to Firestore
    public void createUser(Context context, String name, String email) {
        userModel = new UserModel(context, name, email);
    }

    public void createUser(Context context, String name, String email, String phone) {
        userModel = new UserModel(context, name, email, phone);
    }

    // Other controller methods that interact with the model can go here
}
