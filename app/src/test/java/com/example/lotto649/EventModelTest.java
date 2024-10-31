package com.example.lotto649;

import android.content.Context;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.FacilityModel;
import com.example.lotto649.Models.UserModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
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

    private EventModel event;
    private String mockDeviceId = "mockDeviceId";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        // Mock FirebaseFirestore setup
        mockFirestore = mock(FirebaseFirestore.class);
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);

        // Initialize EventModel with mocked FacilityModel
        event = new EventModel("Sample Event", locationMock, 50.0, "Sample Description", 100, "Conference");
    }

    @Test
    public void testConstructorWithoutPosterImage() {
        assertEquals("Sample Event", event.getTitle());
        assertEquals(locationMock, event.getLocation());
        assertEquals(50.0, event.getCost(), 0.001);
        assertEquals("Sample Description", event.getDescription());
        assertEquals(100, event.getNumberOfSpots());
        assertEquals("Conference", event.getEventType());
        assertNull(event.getPosterImage()); // Poster image should be null by default
        assertNotNull(event.getQrCodePath()); // QR code path should be initialized
    }

    @Test
    public void testConstructorWithPosterImage() {
        Object posterImage = new Object(); // Replace with actual image if available
        event = new EventModel("Sample Event", locationMock, 50.0, "Sample Description", 100, "Conference", posterImage);

        assertEquals("Sample Event", event.getTitle());
        assertEquals(locationMock, event.getLocation());
        assertEquals(50.0, event.getCost(), 0.001);
        assertEquals("Sample Description", event.getDescription());
        assertEquals(100, event.getNumberOfSpots());
        assertEquals("Conference", event.getEventType());
        assertEquals(posterImage, event.getPosterImage());
        assertNotNull(event.getQrCodePath());
    }

    @Test
    public void testAddToWaitingList() {
        event.addToWaitingList(userMock);
        ArrayList<UserModel> waitingList = event.getWaitingList();

        assertEquals(1, waitingList.size());
        assertTrue(waitingList.contains(userMock));
    }

    @Test
    public void testGenerateQrCode() {
        assertNotNull(event.getQrCodePath());
    }

    @Test
    public void testSetTitle() {
        String newTitle = "Updated Event Title";
        event.setTitle(newTitle);
        assertEquals(newTitle, event.getTitle());
    }

    @Test
    public void testSetLocation() {
        FacilityModel newLocation = mock(FacilityModel.class);
        event.setLocation(newLocation);
        assertEquals(newLocation, event.getLocation());
    }

    @Test
    public void testSetCost() {
        double newCost = 75.0;
        event.setCost(newCost);
        assertEquals(newCost, event.getCost(), 0.001);
    }

    @Test
    public void testSetDescription() {
        String newDescription = "Updated Description";
        event.setDescription(newDescription);
        assertEquals(newDescription, event.getDescription());
    }

    @Test
    public void testSetNumberOfSpots() {
        int newSpots = 150;
        event.setNumberOfSpots(newSpots);
        assertEquals(newSpots, event.getNumberOfSpots());
    }

    @Test
    public void testSetEventType() {
        String newEventType = "Workshop";
        event.setEventType(newEventType);
        assertEquals(newEventType, event.getEventType());
    }

    @Test
    public void testSetPosterImage() {
        Object newPosterImage = new Object();
        event.setPosterImage(newPosterImage);
        assertEquals(newPosterImage, event.getPosterImage());
    }

    @Test
    public void testNotifyEntrants() {
        NotificationSys notificationSysMock = mock(NotificationSys.class);
        event.addToWaitingList(userMock);
        event.notifyEntrants(notificationSysMock);

        verify(notificationSysMock).sendNotification(userMock, "You are on the waiting list for Sample Event");
    }

    private void verifyFirestoreUpdate(String field, String value) {
        ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(mockDocumentReference).update(fieldCaptor.capture(), valueCaptor.capture());
        assertEquals(field, fieldCaptor.getValue());
        assertEquals(value, valueCaptor.getValue());
    }
}
