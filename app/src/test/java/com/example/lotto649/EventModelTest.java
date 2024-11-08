package com.example.lotto649;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class EventModelTest {

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
    @Mock
    MyApp mockApp;
    @Mock
    private UserModel mockUser;
    private String mockDeviceId = "mockDeviceId";
    private String mockFacilityId = "mockFacilityId";
    private String mockEventId = "eventId";

    private EventModel event;

    @Before
    public void testSetUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ANDROID_ID, mockDeviceId);
        
        
        // Mock FirebaseFirestore instance
        mockFirestore = mock(FirebaseFirestore.class);
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.set(any())).thenReturn(mockTask);
        when(mockDocumentReference.update(anyString(), any())).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);  // Simulate successful completion
            return mockTask;
        });
        when(mockTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            return mockTask;
        });

        // Create a new EventModel with mocked context
        mockUser = new UserModel(context, "John Doe", "john@example.com", "123456789", mockFirestore);

        MyApp mockAppInstance = spy(MyApp.class);
        MyApp.setInstance(mockAppInstance);

        mockApp = MyApp.getInstance();
        mockApp.setUserModel(mockUser);

        event = spy(new EventModel(context,
                "Event", mockFacilityId,
                9.99,
                "Description",
                5, 15,
                new Date(2024, 12, 25),
                new Date(2024, 12, 30),
                Object.class, false, Object.class,
                new ArrayList<UserModel>(Arrays.asList(
                        new UserModel[]{new UserModel(), new UserModel()}
                    )),
                mockFirestore)
                );
        doNothing().when(event).notifyViews();
    }

    @Test
    public void testSaveEventToFirestore() {
        assertFalse(event.getSavedToFirestore());

        Task<DocumentReference> mockAddTask = mock(Task.class);
        DocumentReference mockDocumentReference = mock(DocumentReference.class);

        when(mockAddTask.addOnSuccessListener(any(OnSuccessListener.class))).thenAnswer(invocation -> {
            OnSuccessListener<DocumentReference> listener = invocation.getArgument(0);
            listener.onSuccess(mockDocumentReference);
            return mockAddTask;
        });

        when(mockAddTask.addOnFailureListener(any(OnFailureListener.class))).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Mock failure"));
            return mockAddTask;
        });

        when(mockCollectionReference.add(any(HashMap.class))).thenReturn(mockAddTask);

        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);

        event.saveEventToFirestore();

        verify(mockFirestore).collection("events");
        verify(mockCollectionReference).add(any(HashMap.class));
        verify(mockAddTask).addOnSuccessListener(any(OnSuccessListener.class)); // This should now pass

        assertTrue(event.getSavedToFirestore());
    }

    @Test
    public void testRemoveEventFromFirestore() {
        event.setEventId(mockEventId);
        event.setSavedToFirestore(true);
        assertTrue(event.getSavedToFirestore());

        Task<Void> mockDeleteTask = mock(Task.class);

        DocumentReference mockDocumentReference = mock(DocumentReference.class);
        when(mockDocumentReference.delete()).thenReturn(mockDeleteTask);

        when(mockCollectionReference.document(mockEventId)).thenReturn(mockDocumentReference);
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);

        when(mockDeleteTask.addOnSuccessListener(any(OnSuccessListener.class))).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockDeleteTask;
        });

        when(mockDeleteTask.addOnFailureListener(any(OnFailureListener.class))).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Mock failure"));
            return mockDeleteTask;
        });

        event.removeEventFromFirestore();

        verify(mockFirestore).collection("events");
        verify(mockCollectionReference).document(mockEventId);
        verify(mockDocumentReference).delete();
        verify(mockDeleteTask).addOnSuccessListener(any(OnSuccessListener.class));

        assertFalse(event.getSavedToFirestore());
    }

    @Test
    public void testUpdateFirestore() {
        // Set the event ID to simulate an existing document in Firestore
        event.setEventId(mockEventId);
        event.setSavedToFirestore(true);
        assertTrue(event.getSavedToFirestore());

        // Mock Task<Void> for the update operation
        Task<Void> mockUpdateTask = mock(Task.class);

        // Mock DocumentReference and CollectionReference behavior
        DocumentReference mockDocumentReference = mock(DocumentReference.class);
        when(mockDocumentReference.update("title", "New Title")).thenReturn(mockUpdateTask);

        // Ensure CollectionReference returns the correct DocumentReference
        when(mockCollectionReference.document(mockEventId)).thenReturn(mockDocumentReference);
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);

        // Mock update task success behavior
        when(mockUpdateTask.addOnSuccessListener(any(OnSuccessListener.class))).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null); // Simulate successful update
            return mockUpdateTask;
        });

        when(mockUpdateTask.addOnFailureListener(any(OnFailureListener.class))).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Mock failure")); // Simulate failure
            return mockUpdateTask;
        });

        // Call the method to test
        event.updateFirestore("title", "New Title");

        // Verify that the correct calls were made with the specified event ID and field update
        verify(mockFirestore).collection("events");
        verify(mockCollectionReference).document(mockEventId);
        verify(mockDocumentReference).update("title", "New Title");
        verify(mockUpdateTask).addOnSuccessListener(any(OnSuccessListener.class));

        // Ensure the savedToFirestore status has not changed
        assertTrue(event.getSavedToFirestore());
    }

    @Test
    public void testSetTitle() {
        String newTitle = "New Event";
        assertEquals("Event", event.getTitle());

        event.setTitle(newTitle);

        assertEquals(newTitle, event.getTitle());
        verify(event).notifyViews();
    }

    @Test
    public void testSetFacilityId() {
        String newFacilityId = "FacTest";
        assertEquals(mockFacilityId, event.getFacilityId());

        event.setFacilityId(newFacilityId);

        assertEquals(newFacilityId, event.getFacilityId());
        verify(event).notifyViews();
    }

    @Test
    public void testSetDescription() {
        String newDescription = "New Description";
        assertEquals("Description", event.getDescription());

        event.setDescription(newDescription);

        assertEquals(newDescription, event.getDescription());
        verify(event).notifyViews();
    }

    @Test
    public void testSetCost() {
        double newCost = 8.88;
        assertEquals(9.99, event.getCost(), 0.005);

        event.setCost(newCost);

        assertEquals(newCost, event.getCost(),0.005);
        verify(event).notifyViews();
    }

    @Test
    public void testSetStartDate() {
        Date newStartDate = new Date(2024,12,26);
        assertEquals(new Date(2024,12,25), event.getStartDate());

        event.setStartDate(newStartDate);

        assertEquals(newStartDate, event.getStartDate());
        verify(event).notifyViews();
    }

    @Test
    public void testSetEndDate() {
        Date newEndDate = new Date(2024,12,29);
        assertEquals(new Date(2024,12,30), event.getEndDate());

        event.setEndDate(newEndDate);

        assertEquals(newEndDate, event.getEndDate());
        verify(event).notifyViews();
    }

    @Test
    public void testSetNumberOfSpots() {
        int newSpots = 10;
        assertEquals(5, event.getNumberOfSpots());

        event.setNumberOfSpots(newSpots);

        assertEquals(newSpots, event.getNumberOfSpots());
        verify(event).notifyViews();
    }

    @Test
    public void testSetNumberOfMaxEntrants() {
        int newNumberOfMaxEntrants = 10;
        assertEquals(15, event.getNumberOfMaxEntrants());

        event.setNumberOfMaxEntrants(newNumberOfMaxEntrants);

        assertEquals(newNumberOfMaxEntrants, event.getNumberOfMaxEntrants());
        verify(event).notifyViews();
    }

    @Test
    public void testSetPosterImage() {
        Object newPosterImage = Object.class;
        assertEquals(Object.class, event.getPosterImage());

        event.setPosterImage(newPosterImage);

        assertEquals(newPosterImage, event.getPosterImage());
        verify(event).notifyViews();
    }

    @Test
    public void testSetGeo() {
        boolean newGeo = true;
        assertFalse(event.getGeo());

        event.setGeo(newGeo);

        assertTrue(event.getGeo());
        verify(event).notifyViews();
    }

    @Test
    public void testSetQrCode() {
        Object newQrCode = Object.class;
        assertEquals(Object.class, event.getQrCode());

        event.setPosterImage(newQrCode);

        assertEquals(newQrCode, event.getQrCode());
        verify(event).notifyViews();
    }

    @Test
    public void testAddToWaitingList() {
        UserModel testMockUser = mock(UserModel.class);
        assertTrue(event.addToWaitingList(testMockUser));
    }

    @Test
    public void testAddToWaitingList_MaxReached() {
        UserModel testMockUser = mock(UserModel.class);
        event.setNumberOfMaxEntrants(2);
        assertFalse(event.addToWaitingList(testMockUser));
    }
/*
*/
}
