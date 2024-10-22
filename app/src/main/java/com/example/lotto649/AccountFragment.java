/**
 * Code used from the following source for menu bar
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 */

package com.example.lotto649;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {
    private AccountView accountView;
    private AccountUserController userController;
    private FirebaseFirestore db;
    private UserModel user;
    private TextInputLayout fullNameInputLayout, emailInputLayout, phoneNumberInputLayout;
    private TextInputEditText fullNameEditText, emailEditText, phoneNumberEditText;
    private ExtendedFloatingActionButton saveButton;

    public AccountFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        fullNameInputLayout = view.findViewById(R.id.textFieldFullName);
        emailInputLayout = view.findViewById(R.id.textFieldEmail);
        phoneNumberInputLayout = view.findViewById(R.id.textFieldPhoneNumber);

        fullNameEditText = (TextInputEditText) fullNameInputLayout.getEditText();
        emailEditText = (TextInputEditText) emailInputLayout.getEditText();
        phoneNumberEditText = (TextInputEditText) phoneNumberInputLayout.getEditText();
        saveButton = view.findViewById(R.id.extended_fab);

        db = FirebaseFirestore.getInstance();
        user = new UserModel();

        checkUserInFirestore(new FirestoreUserCallback() {
            @Override
            public void onCallback(UserModel userModel) {
                user.update(userModel);
                // Here you get either the existing user or a new UserModel object
                fullNameEditText.setText(user.getName());
                emailEditText.setText(user.getEmail());
                phoneNumberEditText.setText(user.getPhone());
            }
        });

        accountView = new AccountView(user, this);
        userController = new AccountUserController(user);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect the data from the AccountView

                // Create a new UserModel object
                userController.update(new UserModel(getContext(), fullNameEditText.getText().toString(), emailEditText.getText().toString(), phoneNumberEditText.getText().toString()));
            }
        });
        return view;
    }

    private void checkUserInFirestore(FirestoreUserCallback firestoreUserCallback) {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference doc = db.collection("users").document(deviceId);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Deserialize the document into the UserModel
                        UserModel user = document.toObject(UserModel.class);
                        if (user != null) {
                            // Pass the existing user object to the callback
                            firestoreUserCallback.onCallback(user);
                        }
                    } else {
                        // Create a new UserModel with default values
                        UserModel newUser = new UserModel();
                        // Pass the new user object to the callback
                        firestoreUserCallback.onCallback(newUser);
                    }
                } else {
                    // Handle the failure case
                    firestoreUserCallback.onCallback(new UserModel());  // Return a new UserModel in case of failure
                }
            }
        });
    }

    public void showUserDetails(UserModel user) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fullNameEditText.setText(user.getName());
                emailEditText.setText(user.getEmail());
                phoneNumberEditText.setText(user.getPhone());
            }
        });
    }

}
