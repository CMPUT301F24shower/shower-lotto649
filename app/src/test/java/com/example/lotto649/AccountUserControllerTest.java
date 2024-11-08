package com.example.lotto649;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.example.lotto649.Controllers.AccountUserController;
import com.example.lotto649.Models.UserModel;

@RunWith(RobolectricTestRunner.class)
public class AccountUserControllerTest {

    @Mock
    private UserModel mockUserModel; // Mocking the UserModel

    private AccountUserController accountUserController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountUserController = new AccountUserController(mockUserModel);
    }

    @Test
    public void testUpdateName() {
        String newName = "Jane Doe";

        accountUserController.updateName(newName);

        verify(mockUserModel).setName(newName); // Verify that setName was called with newName
    }

    @Test
    public void testUpdateEmail() {
        String newEmail = "jane@example.com";

        accountUserController.updateEmail(newEmail);

        verify(mockUserModel).setEmail(newEmail); // Verify that setEmail was called with newEmail
    }

    @Test
    public void testUpdatePhone() {
        String newPhone = "987654321";

        accountUserController.updatePhone(newPhone);

        verify(mockUserModel).setPhone(newPhone); // Verify that setPhone was called with newPhone
    }

    @Test
    public void testGetSavedToFirebase_WhenUserNotSaved() {
        when(mockUserModel.getSavedToFirestore()).thenReturn(false);

        boolean saved = accountUserController.getSavedToFirebase();

        assertFalse(saved); // Assert that the saved status is false
    }

    @Test
    public void testGetSavedToFirebase_WhenUserSaved() {
        when(mockUserModel.getSavedToFirestore()).thenReturn(true);

        boolean saved = accountUserController.getSavedToFirebase();

        assertTrue(saved); // Assert that the saved status is true
    }

    @Test
    public void testSaveToFirestore() {
        String name = "Tester";
        String email = "test@example.com";
        String phone = "1234567";
        accountUserController.saveToFirestore(name, email, phone);

        verify(mockUserModel).setName(name);
        verify(mockUserModel).setEmail(email);
        verify(mockUserModel).setPhone(phone);

        verify(mockUserModel).saveUserToFirestore(); // Verify that saveUserToFirestore was called
    }
}

