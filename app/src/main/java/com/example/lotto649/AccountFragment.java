/**
 * Code used from the following source for menu bar
 * https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/
 */

package com.example.lotto649;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {
    private AccountView accountView;
    private FirebaseFirestore db;

    public AccountFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        db = FirebaseFirestore.getInstance();
        checkUserInFirestore();


        accountView = new AccountView(view);

        accountView.setSaveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect the data from the AccountView
                String fullName = accountView.getFullName();
                String email = accountView.getEmail();
                String phoneNumber = accountView.getPhoneNumber();

                // Create a new UserModel object
                UserModel userModel = new UserModel(requireContext(), fullName, email, phoneNumber);

                // Here, you can now save this userModel to Firestore or perform other actions

                // Log or handle the UserModel
                System.out.println(userModel.toString());  // Example of logging the model
            }
        });
        return view;
    }

    /**
     * Method to query Firestore for a user with the matching device ID.
     */
    private void checkUserInFirestore() {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference doc = db.collection("users").document(deviceId);
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Deserialize the document into the User model
                        UserModel user = document.toObject(UserModel.class);
                        if (user != null) {
                            accountView.getFullNameEditText().setText(user.getName());
                            accountView.getEmailEditText().setText(user.getEmail());
                            accountView.getPhoneNumberEditText().setText(user.getPhone());
                        }
                    } else {
//                        Log.d(TAG, "No such document!");
                    }
                } else {
//                    Log.d(TAG, "Get failed with ", task.getException());
                }
            }
        });
    }
}
