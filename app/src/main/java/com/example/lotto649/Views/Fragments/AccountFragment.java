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
 * TextWatcher code was adapted from this thread:
 * https://stackoverflow.com/questions/8543449/how-to-use-the-textwatcher-class-in-android
 * Code for accessing images, and setting image within the app was adapted from this thread:
 * https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
 * Code for accessing firebase was adapted from this thread:
 * https://stackoverflow.com/questions/61418716/how-to-load-data-quickly-into-app-that-is-fetched-from-firestore
 * Code for Glide image was adapted from this thread:
 * https://stackoverflow.com/questions/44761720/save-picture-to-storage-using-glide
 * </p>
 * KNOWN ISSUE: removing profile image from profile page after just uploading image fails
 */
package com.example.lotto649.Views.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.lotto649.Controllers.AccountUserController;
import com.example.lotto649.FirebaseStorageHelper;
import com.example.lotto649.Models.FirestoreUserCallback;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.R;
import com.example.lotto649.Views.AccountView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
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
    private TextInputLayout nameInputLayout, emailInputLayout, phoneInputLayout;
    private TextInputEditText nameEditText, emailEditText, phoneEditText;
    private ExtendedFloatingActionButton saveButton;
    private ExtendedFloatingActionButton deleteImageButton;
    private String initialNameInput, initialEmailInput, initialPhoneInput;
    private TextView imagePlaceholder;
    private static final int PICK_IMAGE_REQUEST = 1;
    private LinearLayout linearLayout;
    private boolean hasSetImage;
    ImageView profileImage;
    Uri currentImageUri;
    private AtomicReference<String> currentImageUriString;
    private MutableLiveData<Boolean> imageAbleToBeDeleted;
    private Context mContext;

    /**
     * Required empty public constructor for AccountFragment.
     */
    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Attaches the fragment to the app, and sets the context
     * @param context the given context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
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
        hasSetImage = false;
        currentImageUri = null;
        currentImageUriString = new AtomicReference<String>("");
        imageAbleToBeDeleted = new MutableLiveData<Boolean>(Boolean.FALSE);
        // https://stackoverflow.com/questions/14457711/android-listening-for-variable-changes
        imageAbleToBeDeleted.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean changedValue) {
                if (Objects.equals(changedValue, Boolean.TRUE)) {
                    deleteImageButton.setVisibility(View.VISIBLE);
                } else {
                    deleteImageButton.setVisibility(View.GONE);
                }
            }
        });


        // Initialize UI components
        nameInputLayout = view.findViewById(R.id.textFieldName);
        emailInputLayout = view.findViewById(R.id.textFieldEmail);
        phoneInputLayout = view.findViewById(R.id.textFieldPhone);

        nameEditText = (TextInputEditText) nameInputLayout.getEditText();
        emailEditText = (TextInputEditText) emailInputLayout.getEditText();
        phoneEditText = (TextInputEditText) phoneInputLayout.getEditText();
        saveButton = view.findViewById(R.id.account_save_button);
        deleteImageButton = view.findViewById(R.id.account_delete_image);
        linearLayout = view.findViewById(R.id.account_linear_layout);
        profileImage = new ImageView(getContext());
        profileImage.setId(View.generateViewId());
        // TODO: This is hardcoded, but works good on my phone, not sure if this is a good idea or not
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(450, 450);
        profileImage.setLayoutParams(layoutParams);
        profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        imagePlaceholder = view.findViewById(R.id.imagePlaceholder);
        imagePlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.removeView(imagePlaceholder);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                if (!hasSetImage) {
                    linearLayout.addView(profileImage, 2);
                }
            }
        });


        nameEditText.addTextChangedListener(nameWatcher);
        emailEditText.addTextChangedListener(emailWatcher);
        phoneEditText.addTextChangedListener(phoneWatcher);

        initialNameInput = nameEditText.getEditableText().toString();
        initialEmailInput = emailEditText.getEditableText().toString();
        initialPhoneInput = phoneEditText.getEditableText().toString();

        // Initialize Firestore and UserModel
        db = FirebaseFirestore.getInstance();
        user = new UserModel(mContext, FirebaseFirestore.getInstance());

        // Check if the user exists in Firestore, or create a new user
        checkUserInFirestore(new FirestoreUserCallback() {
            @Override
            public void onCallback(String name, String email, String phone, String profileImageUri) {
                userController.updateName(name);
                userController.updateEmail(email);
                userController.updatePhone(phone);
                user = userController.getModel();
                nameEditText.setText(user.getName());
                emailEditText.setText(user.getEmail());
                phoneEditText.setText(user.getPhone());

                initialNameInput = name;
                initialEmailInput = email;
                initialPhoneInput = phone;
                imagePlaceholder.setText(user.getInitials());
                SetSaveButtonVisibility(true);

                // Update profile Image
                if (!Objects.equals(profileImageUri, "")) {
                    StorageReference imageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(profileImageUri);
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        currentImageUri = uri;
                        currentImageUriString.set(profileImageUri);
                        imageAbleToBeDeleted.setValue(Boolean.TRUE);
                        Glide.with(mContext)
                                .load(uri)
                                .into(profileImage);
                        linearLayout.removeView(imagePlaceholder);
                        linearLayout.addView(profileImage, 2);
                        hasSetImage = true;
                    })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Unable to fetch profile image", Toast.LENGTH_SHORT).show();
                            });
                }

            }
        });

        // Initialize MVC components
        AccountView accountView = new AccountView(user, this);
        userController = new AccountUserController(user);

        // Set up the save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update user information via the controller
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                if (name.isEmpty()) {
                    nameInputLayout.setError("Please enter your name");
                    return;
                }
                final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
                Pattern pattern = Pattern.compile(EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(email);
                if (email.isEmpty() || !matcher.matches()) {
                    emailInputLayout.setError("Please enter a valid email address");
                    return;
                }
                nameInputLayout.setError(null);
                emailInputLayout.setError(null);
                nameInputLayout.setErrorEnabled(false);
                emailInputLayout.setErrorEnabled(false);
                initialNameInput = name;
                initialEmailInput = email;
                initialPhoneInput = phone;
                String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                String fileName = deviceId + ".jpg";
                // https://stackoverflow.com/questions/1068760/can-i-pass-parameters-by-reference-in-java
                FirebaseStorageHelper.uploadProfileImageToFirebaseStorage(currentImageUri, fileName, currentImageUriString, imageAbleToBeDeleted);
                SetSaveButtonVisibility(true);
                if (!userController.getSavedToFirebase()) {
                    userController.saveToFirestore(name, email, phone, currentImageUriString.get());
                } else {
                    userController.updateName(name);
                    userController.updateEmail(email);
                    userController.updatePhone(phone);
                    userController.updateImage(currentImageUriString.get());
                }
                user = userController.getModel();
                imagePlaceholder.setText(user.getInitials()); // TODO, this isnt right
                saveButton.setText("Save");
            }
        });


        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentImageUri != null && !currentImageUriString.get().isEmpty()) {
                    Log.e("JASON TEST", "IF 1: " + currentImageUriString.get());
                    StorageReference storageRef = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app").getReferenceFromUrl(currentImageUriString.get());
                    storageRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.e("JASON TEST", "ON SUCCESS 1");
                                    db.collection("users")
                                            .document(Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID))
                                            .update("profileImage", "")
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.e("JASON TEST", "ON SUCCESS 2");
                                                }
                                            });
                                    hasSetImage = false;
                                    currentImageUri = null;
                                    userController.updateImage("");
                                    currentImageUriString.set("");
                                    imageAbleToBeDeleted.setValue(Boolean.FALSE);
                                    linearLayout.removeView(profileImage);
                                    linearLayout.addView(imagePlaceholder, 2);
                                    imagePlaceholder.setText(new UserModel(getContext(), initialNameInput, "").getInitials());
                                }
                            });
                } else if (currentImageUri != null && currentImageUriString.get().isEmpty()) {
                    Log.e("JASON TEST", "IF 2: " + currentImageUriString.get());
                    hasSetImage = false;
                    currentImageUri = null;
                    userController.updateImage("");
                    currentImageUriString.set("");
                    imageAbleToBeDeleted.setValue(Boolean.FALSE);
                    linearLayout.removeView(profileImage);
                    linearLayout.addView(imagePlaceholder, 2);
                    imagePlaceholder.setText(new UserModel(getContext(), initialNameInput, "").getInitials());
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
                            saveButton.setText("Save");
                            firestoreUserCallback.onCallback(user.getName(), user.getEmail(), user.getPhone(), user.getProfileImage());
                        }
                    } else {
                        // No user found, create a new one
                        saveButton.setText("Create");
                        firestoreUserCallback.onCallback("", "", "", "");
                    }
                } else {
                    // Failure, return a new default user
                    firestoreUserCallback.onCallback("", "", "", "");
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
//         requireActivity().runOnUiThread(new Runnable() {
//             @Override
//             public void run() {
//                 nameEditText.setText(user.getName());
//                 emailEditText.setText(user.getEmail());
//                 phoneEditText.setText(user.getPhone());
//                 imagePlaceholder.setText(user.getInitials());
//             }
//         });
        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhone());
        imagePlaceholder.setText(user.getInitials());
    }

    /**
     * Finds if the information in the EditText components is the same as in Firestore or not.
     *
     * @return true if either of the facility name or address changed from the saved version
     */
    private boolean DidInfoRemainConstant() {
        return Objects.equals(nameEditText.getEditableText().toString(), initialNameInput) && Objects.equals(emailEditText.getEditableText().toString(), initialEmailInput) && Objects.equals(phoneEditText.getEditableText().toString(), initialPhoneInput);
    }

    /**
     * Watches the name EditText for changes, and calls to possibly change the save button colour
     */
    private final TextWatcher nameWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    /**
     * Watches the email EditText for changes, and calls to possibly change the save button colour
     */
    private final TextWatcher emailWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    /**
     * Watches the phone EditText for changes, and calls to possibly change the save button colour
     */
    private final TextWatcher phoneWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SetSaveButtonVisibility(DidInfoRemainConstant());
        }
    };

    /**
     * Sets the save button's colour depending on whether information on the page changed.
     *
     * @param isEqual if the facility information inputted is the same as in Firestore
     */
    private void SetSaveButtonVisibility(boolean isEqual) {
        if (isEqual) {
            saveButton.setVisibility(View.GONE);
            nameInputLayout.setError(null);
            emailInputLayout.setError(null);
        } else {
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Handles the result of an activity that was started for a result, specifically for picking an image.
     *
     * <p>This method is called when the user selects an image from the gallery or other image sources.
     * If the image selection is successful, the selected image's URI is set to the {@code profileImage}
     * view, and the save button's color is updated to indicate the image has been selected.</p>
     *
     * @param requestCode The request code that was passed to the activity when it was started.
     * @param resultCode  The result code returned by the activity, indicating whether the operation was successful.
     * @param data        The intent containing the result data, which includes the URI of the selected image.
     *                    If the operation was successful, this will not be null and will contain the image URI.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            currentImageUri = data.getData();
            profileImage.setImageURI(currentImageUri);
            imageAbleToBeDeleted.setValue(Boolean.TRUE);
            hasSetImage = true;
            SetSaveButtonVisibility(false);
        } else {
            if (!hasSetImage) {
                Log.e("JASON TEST", "REMOVING IMAGE");
                linearLayout.removeView(profileImage);
                linearLayout.addView(imagePlaceholder, 2);
                imagePlaceholder.setText(new UserModel(getContext(), initialNameInput, "").getInitials());
                imageAbleToBeDeleted.setValue(Boolean.FALSE);
            }
        }
    }
}
