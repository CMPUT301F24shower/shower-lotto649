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
     * @param user the updated user model containing new data
     */
    public void update(UserModel user) {
        getModel().update(user);
    }
}
