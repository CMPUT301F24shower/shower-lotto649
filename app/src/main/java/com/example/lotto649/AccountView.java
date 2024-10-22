package com.example.lotto649;
public class AccountView extends AbstractView {
    private final AccountFragment accountFragment;
    private UserModel user;

    public AccountView(UserModel user, AccountFragment accountFragment) {
        this.accountFragment = accountFragment;
        this.user = user;

        startObserving(user);
    }

    @Override
    public UserModel getModel() {
        return (UserModel) super.getModel();
    }

    @Override
    public void update(AbstractModel whoUpdatedMe) {
        user = getModel();
        accountFragment.showUserDetails(user);
    }
}
