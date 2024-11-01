package com.example.lotto649;

import android.content.Context;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.FacilityModel;
import com.example.lotto649.Models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventModelTest {

    private Context context;
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockCollectionReference;
    @Mock
    private DocumentReference mockDocumentReference;
    @Mock
    private UserModel userMock;
    @Mock
    private FacilityModel locationMock;

    // Define facilityMock as a class-level field
    private FacilityModel facilityMock;
    private EventModel event;

    /**
     * Initializes the test environment with mock objects for Firestore and other dependencies.
     * Sets up an EventModel instance to be used in each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        // Mock FirebaseFirestore, CollectionReference, and DocumentReference
        mockFirestore = mock(FirebaseFirestore.class);
        mockCollectionReference = mock(CollectionReference.class);
        mockDocumentReference = mock(DocumentReference.class);

        // Set up mock Firestore behavior for the "events" and "facilities" collections
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);
        when(mockFirestore.collection("facilities")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        // Mock Task<DocumentSnapshot> for DocumentReference.get()
        Task<DocumentSnapshot> mockFetchTask = mock(Task.class);
        DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
        facilityMock = mock(FacilityModel.class);

        // Configure DocumentSnapshot to return the facilityMock when toObject is called
        when(mockDocumentSnapshot.toObject(FacilityModel.class)).thenReturn(facilityMock);

        // Configure the mock fetch task to handle success and failure listeners
        when(mockFetchTask.addOnSuccessListener(any(OnSuccessListener.class))).thenAnswer(invocation -> {
            OnSuccessListener<DocumentSnapshot> successListener = invocation.getArgument(0);
            successListener.onSuccess(mockDocumentSnapshot); // Trigger success with mockDocumentSnapshot
            return mockFetchTask;
        });
        when(mockFetchTask.addOnFailureListener(any(OnFailureListener.class))).thenReturn(mockFetchTask);

        // Ensure DocumentReference.get() returns the configured mock fetch task
        when(mockDocumentReference.get()).thenReturn(mockFetchTask);

        // Initialize EventModel with mocked FirebaseFirestore
        event = new EventModel(context, "Sample Event", "facilityId", 50.0, "Sample Description", 100, "Conference", mockFirestore);
    }

    /**
     * Tests the constructor without a poster image to ensure all fields are correctly set.
     */
    @Test
    public void testConstructorWithoutPosterImage() {
        assertEquals("Sample Event", event.getTitle());
        assertEquals("facilityId", event.getFacilityId());
        assertEquals(50.0, event.getCost(), 0.001);
        assertEquals("Sample Description", event.getDescription());
        assertEquals(100, event.getNumberOfSpots());
        assertEquals("Conference", event.getEventType());
        assertNull(event.getPosterImage()); // Poster image should be null by default
        assertNotNull(event.getQrCodePath()); // QR code path should be initialized
    }

    /**
     * Tests the constructor with a poster image to verify all fields, including poster image, are set correctly.
     */
    @Test
    public void testConstructorWithPosterImage() {
        Object posterImage = new Object();
        event = new EventModel(context, "Sample Event", "facilityId", 50.0, "Sample Description", 100, "Conference", mockFirestore);
        event.setPosterImage(posterImage);

        assertEquals("Sample Event", event.getTitle());
        assertEquals("facilityId", event.getFacilityId());
        assertEquals(50.0, event.getCost(), 0.001);
        assertEquals("Sample Description", event.getDescription());
        assertEquals(100, event.getNumberOfSpots());
        assertEquals("Conference", event.getEventType());
        assertEquals(posterImage, event.getPosterImage());
        assertNotNull(event.getQrCodePath());
    }

    /**
     * Tests that the event data is saved to Firestore only once.
     */
    @Test
    public void testSaveEventToFirestore() {
        event.saveEventToFirestore();
        verify(mockCollectionReference, times(1)).add(anyMap());
    }

    /**
     * Tests updating a field in Firestore through the updateFirestore method.
     */
    @Test
    public void testUpdateFirestore() {
        event.updateFirestore("title", "Updated Title");
        verifyFirestoreUpdate("title", "Updated Title");
    }

    /**
     * Tests adding a user to the waiting list and verifies the waiting list size.
     */
    @Test
    public void testAddToWaitingList() {
        event.addToWaitingList(userMock);
        ArrayList<UserModel> waitingList = event.getWaitingList();

        assertEquals(1, waitingList.size());
        assertTrue(waitingList.contains(userMock));
    }

    /**
     * Tests that the waiting list is serialized correctly using reflection to access the private method.
     */
    @Test
    public void testSerializeWaitingList() throws Exception {
        // Add a user to the waiting list
        when(userMock.getDeviceId()).thenReturn("mockUserId");
        event.addToWaitingList(userMock);

        // Access and invoke private serializeWaitingList method
        Method serializeMethod = EventModel.class.getDeclaredMethod("serializeWaitingList");
        serializeMethod.setAccessible(true);  // Make the method accessible

        @SuppressWarnings("unchecked")
        List<String> serializedList = (List<String>) serializeMethod.invoke(event);

        // Assertions
        assertEquals(1, serializedList.size());
        assertEquals("mockUserId", serializedList.get(0));
    }

    /**
     * Tests fetching a facility by ID and verifies the callback returns the correct FacilityModel.
     */
    @Test
    public void testFetchFacility() {
        event.fetchFacility(facility -> {
            System.out.println(facility);
            System.out.println(facilityMock);
            assertNotNull(facility); // Ensure facility is not null
            assertEquals(facilityMock, facility); // Check that the fetched facility matches the mock
        });
    }

    /**
     * Tests that a QR code is generated successfully when the event is created.
     */
    @Test
    public void testGenerateQrCode() {
        assertNotNull(event.getQrCodePath());
    }

    /**
     * Tests notifying all users on the waiting list and verifies that a notification is sent to each user.
    @Test
    public void testNotifyEntrants() {
        NotificationSys notificationSysMock = mock(NotificationSys.class);
        event.addToWaitingList(userMock);
        event.notifyEntrants(notificationSysMock);

        verify(notificationSysMock).sendNotification(userMock, "You are on the waiting list for Sample Event");
    }
     */

    // Getter and Setter tests for all attributes with Firestore verification

    /**
     * Tests updating the event title and verifies that the title is correctly updated in Firestore.
     */
    @Test
    public void testSetTitle() {
        String newTitle = "Updated Event Title";
        event.setTitle(newTitle);
        assertEquals(newTitle, event.getTitle());

        verifyFirestoreUpdate("title", newTitle);
    }

    /**
     * Tests setting a new facility ID for the event and verifies the update in Firestore.
     */
    @Test
    public void testSetFacilityId() {
        String newFacilityId = "newFacilityId";
        event.setFacilityId(newFacilityId);
        assertEquals(newFacilityId, event.getFacilityId());

        verifyFirestoreUpdate("facilityId", newFacilityId);
    }

    /**
     * Tests updating the event cost and verifies that the cost is correctly updated in Firestore.
     */
    @Test
    public void testSetCost() {
        double newCost = 75.0;
        event.setCost(newCost);
        assertEquals(newCost, event.getCost(), 0.001);

        verifyFirestoreUpdate("cost", newCost);
    }

    /**
     * Tests updating the event description and verifies that the description is correctly updated in Firestore.
     */
    @Test
    public void testSetDescription() {
        String newDescription = "Updated Description";
        event.setDescription(newDescription);
        assertEquals(newDescription, event.getDescription());

        verifyFirestoreUpdate("description", newDescription);
    }

    /**
     * Tests updating the number of spots for the event and verifies the update in Firestore.
     */
    @Test
    public void testSetNumberOfSpots() {
        int newSpots = 150;
        event.setNumberOfSpots(newSpots);
        assertEquals(newSpots, event.getNumberOfSpots());

        verifyFirestoreUpdate("numberOfSpots", newSpots);
    }

    /**
     * Tests updating the event type and verifies that the event type is correctly updated in Firestore.
     */
    @Test
    public void testSetEventType() {
        String newEventType = "Workshop";
        event.setEventType(newEventType);
        assertEquals(newEventType, event.getEventType());

        verifyFirestoreUpdate("eventType", newEventType);
    }

    /**
     * Tests setting a new poster image for the event and verifies the update in Firestore.
     */
    @Test
    public void testSetPosterImage() {
        Object newPosterImage = new Object();
        event.setPosterImage(newPosterImage);
        assertEquals(newPosterImage, event.getPosterImage());

        verifyFirestoreUpdate("posterImage", newPosterImage.toString());
    }

    /**
     * Verifies that a specific field and value were updated in Firestore.
     *
     * @param field the name of the field expected to be updated
     * @param value the value expected to be set for the field
     */
    private void verifyFirestoreUpdate(String field, Object value) {
        ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(mockDocumentReference).update(fieldCaptor.capture(), valueCaptor.capture());
        assertEquals(field, fieldCaptor.getValue());
        assertEquals(value, valueCaptor.getValue());
    }
}
