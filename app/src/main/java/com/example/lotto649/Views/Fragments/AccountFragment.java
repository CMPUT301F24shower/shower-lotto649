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
package com.example.lotto649.Views.Fragments;

import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lotto649.Controllers.AccountUserController;
import com.example.lotto649.Models.FirestoreUserCallback;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.R;
import com.example.lotto649.Views.AccountView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String initialFullNameInput, initialEmailInput, initialPhoneInput;

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
        saveButton = view.findViewById(R.id.account_save_button);

        fullNameEditText.addTextChangedListener(nameWatcher);
        emailEditText.addTextChangedListener(emailWatcher);
        phoneNumberEditText.addTextChangedListener(phoneWatcher);

        initialFullNameInput = fullNameEditText.getEditableText().toString();
        initialEmailInput = emailEditText.getEditableText().toString();
        initialPhoneInput = phoneNumberEditText.getEditableText().toString();

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

                initialFullNameInput = name;
                initialEmailInput = email;
                initialPhoneInput = phone;
                SetSaveButtonColor(true);
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
                if (name.isEmpty()) {
                    fullNameInputLayout.setError("Please enter your name");
                    return;
                }
                final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
                Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(email);
                if (email.isEmpty() || !matcher.matches()) {
                    emailInputLayout.setError("Please enter an email address");
                    return;
                }
                fullNameInputLayout.setError(null);
                emailInputLayout.setError(null);
                fullNameInputLayout.setErrorEnabled(false);
                emailInputLayout.setErrorEnabled(false);
                initialFullNameInput = name;
                initialEmailInput = email;
                initialPhoneInput = phone;
                SetSaveButtonColor(true);
                if (!userController.getSavedToFirebase()) {
                    userController.saveToFirestore(name, email, phone);
                } else {
                    userController.updateName(name);
                    userController.updateEmail(email);
                    userController.updatePhone(phone);
                }
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
//         getActivity().runOnUiThread(new Runnable() {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fullNameEditText.setText(user.getName());
                emailEditText.setText(user.getEmail());
                phoneNumberEditText.setText(user.getPhone());
            }
        });
    }

    /**
     * Finds if the information in the EditText components is the same as in Firestore or not.
     *
     * @return true if either of the facility name or address changed from the saved version
     */
    private boolean DidInfoRemainConstant() {
        return Objects.equals(fullNameEditText.getEditableText().toString(), initialFullNameInput) && Objects.equals(emailEditText.getEditableText().toString(), initialEmailInput) && Objects.equals(phoneNumberEditText.getEditableText().toString(), initialPhoneInput);
    }

    /**
     * Watches the name EditText for changes, and calls to possibly change the save button colour
     */
    private TextWatcher nameWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonColor(DidInfoRemainConstant());
        }
    };

    /**
     * Watches the email EditText for changes, and calls to possibly change the save button colour
     */
    private TextWatcher emailWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonColor(DidInfoRemainConstant());
        }
    };

    /**
     * Watches the phone EditText for changes, and calls to possibly change the save button colour
     */
    private TextWatcher phoneWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonColor(DidInfoRemainConstant());
        }
    };

    /**
     * Sets the save button's colour depending on whether information on the page changed.
     *
     * @param isEqual if the facility information inputted is the same as in Firestore
     */
    private void SetSaveButtonColor(boolean isEqual) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            RippleDrawable saveBackgroundColor = (RippleDrawable) saveButton.getBackground();
            if (isEqual) {
                if (saveBackgroundColor.getEffectColor().getDefaultColor() != getResources().getColor(R.color.lightSurfaceContainerHigh, null)) {
                    saveButton.setBackgroundColor(getResources().getColor(R.color.lightSurfaceContainerHigh, null));
                    saveButton.setTextColor(getResources().getColor(R.color.lightPrimary, null));
                }
            }
            else {
                if (saveBackgroundColor.getEffectColor().getDefaultColor() != getResources().getColor(R.color.lightOnSurface, null)) {
                    saveButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    saveButton.setTextColor(getResources().getColor(R.color.black, null));
                }
            }
        }
    }
}
