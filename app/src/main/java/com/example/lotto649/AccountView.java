package com.example.lotto649;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AccountView {

    private TextView accountTextView;
    private TextInputLayout fullNameInputLayout, emailInputLayout, phoneNumberInputLayout;
    private TextInputEditText fullNameEditText, emailEditText, phoneNumberEditText;
    private ExtendedFloatingActionButton saveButton;

    public AccountView(View view) {
        // Initialize the views from the layout
        accountTextView = view.findViewById(R.id.accountFragment);
        fullNameInputLayout = view.findViewById(R.id.textFieldFullName);
        emailInputLayout = view.findViewById(R.id.textFieldEmail);
        phoneNumberInputLayout = view.findViewById(R.id.textFieldPhoneNumber);

        fullNameEditText = (TextInputEditText) fullNameInputLayout.getEditText();
        emailEditText = (TextInputEditText) emailInputLayout.getEditText();
        phoneNumberEditText = (TextInputEditText) phoneNumberInputLayout.getEditText();

        saveButton = view.findViewById(R.id.extended_fab);
    }

    // Getters for each view
    public TextView getAccountTextView() {
        return accountTextView;
    }

    public TextInputLayout getFullNameInputLayout() {
        return fullNameInputLayout;
    }

    public TextInputEditText getFullNameEditText() {
        return fullNameEditText;
    }

    public TextInputLayout getEmailInputLayout() {
        return emailInputLayout;
    }

    public TextInputEditText getEmailEditText() {
        return emailEditText;
    }

    public TextInputLayout getPhoneNumberInputLayout() {
        return phoneNumberInputLayout;
    }

    public TextInputEditText getPhoneNumberEditText() {
        return phoneNumberEditText;
    }

    public ExtendedFloatingActionButton getSaveButton() {
        return saveButton;
    }

    // Method to get the full name entered by the user
    public String getFullName() {
        return fullNameEditText != null ? fullNameEditText.getText().toString() : "";
    }

    // Method to get the email entered by the user
    public String getEmail() {
        return emailEditText != null ? emailEditText.getText().toString() : "";
    }

    // Method to get the phone number entered by the user
    public String getPhoneNumber() {
        return phoneNumberEditText != null ? phoneNumberEditText.getText().toString() : "";
    }

    // Method to set a listener on the Save button
    public void setSaveButtonClickListener(View.OnClickListener listener) {
        saveButton.setOnClickListener(listener);
    }
}
