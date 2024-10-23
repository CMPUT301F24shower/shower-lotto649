/**
 * AccountUserController is part of the Model-View-Controller (MVC) architecture.
 * It serves as the controller for managing interactions between the user model (`UserModel`)
 * and the views or other components in the application.
 * <p>
 * This controller allows for updates to the `UserModel` and ensures the model's state is
 * synchronized with any changes made by the user.
 * </p>
 */
package com.example.lotto649;

public class AccountUserController extends AbstractController {

    /**
     * Constructor for the AccountUserController class.
     * Initializes the controller with the specified user model.
     *
     * @param user the user model to be managed by this controller
     */
    public AccountUserController(UserModel user) {
        super(user);
    }

    /**
     * Retrieves the model (user) being managed by this controller.
     * This method overrides the `getModel()` method in `AbstractController`
     * to specifically return a `UserModel`.
     *
     * @return the user model being managed by this controller
     */
    @Override
    public UserModel getModel() {
        return (UserModel) super.getModel();
    }

    /**
     * Updates the user model with new data. This method is used to modify the
     * state of the `UserModel` and trigger any necessary updates.
     *
     * @param name the updated user name
     */
    public void updateName(String name) {
        getModel().setName(name);
    }

    /**
     * Updates the user model with new data. This method is used to modify the
     * state of the `UserModel` and trigger any necessary updates.
     *
     * @param email the updated user email
     */
    public void updateEmail(String email) {
        getModel().setEmail(email);
    }

    /**
     * Updates the user model with new data. This method is used to modify the
     * state of the `UserModel` and trigger any necessary updates.
     *
     * @param phone the updated user phone
     */
    public void updatePhone(String phone) {
        getModel().setPhone(phone);
    }

    /**
     * Gets if the user has been saved to firebase
     *
     * @return a boolean representing if the user has been saved to firebase
     */
    public boolean getSavedToFirebase() {
        return getModel().getSavedToFirestore();
    }

    /**
     * Save the user to firestore for data retention
     */
    public void saveToFirestore(String name, String email, String phone) {
        getModel().saveUserToFirestore(name, email, phone);
    }
}
