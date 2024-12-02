package com.example.lotto649;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.example.lotto649.Models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;


@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class UserModelTest {

    private Context context;
    @Mock
    private ContentResolver mockContentResolver;
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    CollectionReference mockCollectionReference;
    @Mock
    DocumentReference mockDocumentReference;
    @Mock
    Task<Void> mockTask;


    private UserModel user;
    private String mockDeviceId = "mockDeviceId";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
//        MockitoAnnotations.initMocks(this);
        context = RuntimeEnvironment.getApplication();

        Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ANDROID_ID, mockDeviceId);

        // Mock FirebaseFirestore instance
        mockFirestore = mock(FirebaseFirestore.class);
        when(mockFirestore.collection("users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(any())).thenReturn(mockTask);
        when(mockDocumentReference.set(any(), any())).thenReturn(mockTask);
        when(mockDocumentReference.update(anyString(), any(), any())).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);  // Simulate successful completion
            return mockTask;
        });

        // Create a new UserModel with mocked context
        user = spy(new UserModel(context, "John Doe", "john@example.com", "123456789", mockFirestore));
        doNothing().when(user).notifyViews();
    }

    @Test
    public void testSetName() {
        String newName = "Jane Doe";
        assertEquals("John Doe", user.getName());

        user.setName(newName);

        assertEquals(newName, user.getName());
        verifyFirestoreUpdate("name", newName);
        verify(user).notifyViews();
    }

    @Test
    public void testSetEmail() {
        String newEmail = "jane@example.com";
        assertEquals("john@example.com", user.getEmail());

        user.setEmail(newEmail);

        assertEquals(newEmail, user.getEmail());
        verifyFirestoreUpdate("email", newEmail);
        verify(user).notifyViews();

    }

    @Test
    public void testSetPhone() {
        String newPhone = "987654321";

         user.setPhone(newPhone);

        assertEquals(newPhone, user.getPhone());
        verifyFirestoreUpdate("phone", newPhone);
        verify(user).notifyViews();
    }

    @Test
    public void testSaveUserToFirestoreSuccessful() {
        assertFalse(user.getSavedToFirestore());
        user.saveUserToFirestore();
        verify(mockFirestore).collection("users");
        verify(mockCollectionReference).document(mockDeviceId);
        verify(mockDocumentReference).set(new HashMap<String, Object>() {{
            put("name", user.getName());
            put("email", user.getEmail());
            put("phone", user.getPhone());
            put("entrant", user.getEntrant());
            put("organizer", user.getOrganizer());
            put("profileImage", user.getProfileImage());
        }}, SetOptions.merge());
        assertTrue(user.getSavedToFirestore());
    }

    @Test
    public void testSaveUserToFirestoreFailure() {
        // Simulate failure
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            // Not invoking this listener to simulate a failure
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any());
        doAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Mock failure")); // Trigger the failure listener
            return mockTask;
        }).when(mockTask).addOnFailureListener(any());

        assertFalse(user.getSavedToFirestore());
        user.saveUserToFirestore();
        verify(mockFirestore).collection("users");
        verify(mockCollectionReference).document(mockDeviceId);
        verify(mockDocumentReference).set(new HashMap<String, Object>() {{
            put("name", user.getName());
            put("email", user.getEmail());
            put("phone", user.getPhone());
            put("entrant", user.getEntrant());
            put("organizer", user.getOrganizer());
            put("profileImage", user.getProfileImage());
        }}, SetOptions.merge());
        assertFalse(user.getSavedToFirestore());
    }

    @Test
    public void testUpdateFirestoreSuccessful() {
        user.updateFirestore("field", "value");
        verify(mockFirestore).collection("users");
        verify(mockCollectionReference).document(mockDeviceId);
        verify(mockDocumentReference).update("field", "value");
        // TODO: Add error handling to test for failure

    }

    @Test
    public void testUpdateFirestoreFailure() {
        // Simulate failure
        doAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            // Not invoking this listener to simulate a failure
            return mockTask;
        }).when(mockTask).addOnSuccessListener(any());
        doAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Mock failure")); // Trigger the failure listener
            return mockTask;
        }).when(mockTask).addOnFailureListener(any());

        user.updateFirestore("field", "value");
        verify(mockFirestore).collection("users");
        verify(mockCollectionReference).document(mockDeviceId);
        verify(mockDocumentReference).update("field", "value");
        // TODO: Add error handling to test for failure
    }


    private void verifyFirestoreUpdate(String field, String value) {
        // Capture the Firestore document updates
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        // Verify the call to Firestore update
        verify(mockFirestore.collection("users").document(mockDeviceId)).update(
                eq(field), eq(value));
    }

    @Test
    public void testSetSavedToFirestore() {
        assertFalse(user.getSavedToFirestore());

        user.setSavedToFirestore();

        assertTrue(user.getSavedToFirestore());
    }

    @Test
    public void testGetInitials_withName() {
        user.setName("John Doe");
        assertEquals("JD", user.getInitials());
    }

    @Test
    public void testGetInitials_withSingleName() {
        user.setName("Cher");
        assertEquals("C", user.getInitials());
    }

    @Test
    public void testGetInitials_withEmptyName() {
        user.setName("");
        assertEquals("", user.getInitials());
    }

    @Test
    public void testGetInitials_withNullName() {
        user.setName(null);
        assertEquals("", user.getInitials());
    }

    @Test
    public void testGetAndSetProfileImage() {
        String profileImageUrl = "https://example.com/image.jpg";
        user.setProfileImage(profileImageUrl);
        assertEquals(profileImageUrl, user.getProfileImage());
    }
}
