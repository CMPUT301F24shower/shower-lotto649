package com.example.lotto649;

public class AccountUserController extends AbstractController {
    public AccountUserController(UserModel user) {
        super(user);
    }

    @Override
    public UserModel getModel() {
        return (UserModel) super.getModel();
    }

    public void update(UserModel user) {
        getModel().update(user);
    }
}
