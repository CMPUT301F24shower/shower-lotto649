/**
 * AccountFragment class manages the account details of the user in the application.
 * <p>
 * This fragment is responsible for displaying and updating the user's account information
 * (name, email, and phone number) using a form. The user information is retrieved from and
 * saved to Firebase Firestore. The class also manages the interactions between the view,
 * model, and controller in a Model-View-Controller (MVC) pattern.
 * </p>
 * <p>
 * Code for the bottom navigation bar was adapted from:
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 * </p>
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

/**
 * The AccountFragment class handles the user interface for viewing and editing account details.
 */
public class AccountFragment extends Fragment {
    private AccountView accountView;
    private AccountUserController userController;
    private FirebaseFirestore db;
    private UserModel user;
    private TextInputLayout fullNameInputLayout, emailInputLayout, phoneNumberInputLayout;
    private TextInputEditText fullNameEditText, emailEditText, phoneNumberEditText;
    private ExtendedFloatingActionButton saveButton;

    /**
     * Required empty public constructor for AccountFragment.
     */
    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy for this fragment.
     * This method inflates the layout for the account fragment and initializes the UI components.
     *
     * @param inflater  LayoutInflater object used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState Bundle containing data about the previous state (if any)
     * @return View for the account fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize UI components
        fullNameInputLayout = view.findViewById(R.id.textFieldFullName);
        emailInputLayout = view.findViewById(R.id.textFieldEmail);
        phoneNumberInputLayout = view.findViewById(R.id.textFieldPhoneNumber);

        fullNameEditText = (TextInputEditText) fullNameInputLayout.getEditText();
        emailEditText = (TextInputEditText) emailInputLayout.getEditText();
        phoneNumberEditText = (TextInputEditText) phoneNumberInputLayout.getEditText();
        saveButton = view.findViewById(R.id.extended_fab);

        // Initialize Firestore and UserModel
        db = FirebaseFirestore.getInstance();
        user = new UserModel(getContext(), FirebaseFirestore.getInstance());

        // Check if the user exists in Firestore, or create a new user
        checkUserInFirestore(new FirestoreUserCallback() {
            @Override
            public void onCallback(String name, String email, String phone) {
                userController.updateName(name);
                userController.updateEmail(email);
                userController.updatePhone(phone);
//                userController.update(userModel);
                fullNameEditText.setText(user.getName());
                emailEditText.setText(user.getEmail());
                phoneNumberEditText.setText(user.getPhone());
            }
        });

        // Initialize MVC components
        accountView = new AccountView(user, this);
        userController = new AccountUserController(user);

        // Set up the save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update user information via the controller
                String name = fullNameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneNumberEditText.getText().toString();
                if (!userController.getSavedToFirebase()) {
                    userController.saveToFirestore();
                }
                userController.updateName(name);
                userController.updateEmail(email);
                userController.updatePhone(phone);

            }
        });

        return view;
    }

    /**
     * Checks if the user exists in Firestore based on the device ID and retrieves the user data.
     * If no user is found, a new UserModel with default values is created.
     *
     * @param firestoreUserCallback The callback used to return the user model
     */
    private void checkUserInFirestore(FirestoreUserCallback firestoreUserCallback) {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference doc = db.collection("users").document(deviceId);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // User exists, deserialize to UserModel
                        UserModel user = document.toObject(UserModel.class);
                        if (user != null) {
                            firestoreUserCallback.onCallback(user.getName(), user.getEmail(), user.getPhone());
                        }
                    } else {
                        // No user found, create a new one
                        firestoreUserCallback.onCallback("", "", "");
                    }
                } else {
                    // Failure, return a new default user
                    firestoreUserCallback.onCallback("", "", "");
                }
            }
        });
    }

    /**
     * Updates the UI with the user's account details.
     *
     * @param user The user model containing the details to be displayed
     */
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
