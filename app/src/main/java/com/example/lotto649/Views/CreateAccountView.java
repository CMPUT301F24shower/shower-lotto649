/**
 * AccountView is part of the Model-View-Controller (MVC) architecture.
 * It represents the view component in the application, observing the state of the `UserModel`
 * and updating the associated `AccountFragment` whenever the model changes.
 * <p>
 * This class extends `AbstractView` to implement the functionality for observing the `UserModel`
 * and updating the view with the latest user details.
 * </p>
 */
package com.example.lotto649.Views;

import com.example.lotto649.AbstractClasses.AbstractModel;
import com.example.lotto649.AbstractClasses.AbstractView;
import com.example.lotto649.Views.Fragments.AccountFragment;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.Views.Fragments.CreateAccountFragment;

public class CreateAccountView extends AbstractView {
    // Reference to the AccountFragment associated with this view
    private final CreateAccountFragment createAccountFragment;

    // The user model that this view is observing
    private UserModel user;

    /**
     * Constructor for the AccountView class.
     * Initializes the view with the specified user model and fragment,
     * and starts observing changes in the user model.
     *
     * @param user the user model to be observed by this view
     * @param createAccountFragment the fragment in which user details will be displayed
     */
    public CreateAccountView(UserModel user, CreateAccountFragment createAccountFragment) {
        this.createAccountFragment = createAccountFragment;
        this.user = user;

        // Start observing the user model
        startObserving(user);
    }

    /**
     * Retrieves the model (user) currently being observed by this view.
     * This method overrides the `getModel()` method in the `AbstractView` to
     * return the `UserModel` specifically.
     *
     * @return the user model being observed by this view
     */
    @Override
    public UserModel getModel() {
        return (UserModel) super.getModel();
    }

    /**
     * Updates the view with the latest details from the observed model (user).
     * This method is called whenever the `UserModel` changes, and it updates the
     * associated `AccountFragment` with the latest user details.
     *
     * @param whoUpdatedMe the model that triggered the update (expected to be a `UserModel`)
     */
    @Override
    public void update(AbstractModel whoUpdatedMe) {
        user = getModel();
        createAccountFragment.showUserDetails(user);
    }
}
