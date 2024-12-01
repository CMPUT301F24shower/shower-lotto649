package com.example.lotto649;

import android.content.Context;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.UserModel;
import com.example.lotto649.MyApp;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventModelTest {
    private Context context;
    private EventModel mockEvent;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private MyApp mockApp;

    @Mock
    private UserModel mockUserModel;
    private CollectionReference mockCollectionRef;
    private DocumentReference mockDocRef;
    private QuerySnapshot mockQuerySnapshot;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        // Mock the application and user model
        when(mockApp.getUserModel()).thenReturn(mockUserModel);
        when(mockUserModel.getDeviceId()).thenReturn("mockDeviceId");

        // Set the mocked MyApp as the singleton instance
        MyApp.setInstance(mockApp);

        // Initialize mockDb and mockCollectionRef
        mockDb = mock(FirebaseFirestore.class);
        mockCollectionRef = mock(CollectionReference.class);

        // Link mockCollectionRef to mockDb
        when(mockDb.collection("facilities")).thenReturn(mockCollectionRef);
        when(mockCollectionRef.document(anyString())).thenReturn(mockDocRef);

        // Initialize the EventModel with mocked dependencies
        mockEvent = spy(new EventModel(context, "Event", "Description", 10,
                10, new Date(), new Date(), "posterImage", true, "qrCode",
                0, false, mockDb));
        doNothing().when(mockEvent).notifyViews();
    }


    @Test
    public void testSetTitle() {
        mockEvent.setTitle("New Event Title");
        assertEquals("New Event Title", mockEvent.getTitle());
    }

    @Test
    public void testSetStartDate() {
        Date now = new Date();
        mockEvent.setStartDate(now);
        assertEquals(now, mockEvent.getStartDate());
    }

    @Test
    public void testSetEndDate() {
        Date now = new Date();
        mockEvent.setEndDate(now);
        assertEquals(now, mockEvent.getEndDate());
    }

    @Test
    public void testGetNumberOfSpots() {
        mockEvent.setNumberOfSpots(5);
        assertEquals(5, mockEvent.getNumberOfSpots());
    }

    @Test
    public void testGetDescription() {
        mockEvent.setDescription("Event Description");
        assertEquals("Event Description", mockEvent.getDescription());
    }

    @Test
    public void testIsDrawn() {
        // Act & Assert
        assertFalse(mockEvent.isDrawn()); // Default state should be false
    }
}


