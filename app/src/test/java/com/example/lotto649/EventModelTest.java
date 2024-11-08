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
    private EventModel mockEvent;
    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    CollectionReference mockCollectionRef;
    @Mock
    DocumentReference mockDocRef;
    @Mock
    Task<Void> mockTask;
    @Mock
    private String mockDeviceId = "mockDeviceId";
    @Mock
    private String mockFacilityId = "mockFacilityId";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        // Mock FirebaseFirestore instance and setup
        mockDb = mock(FirebaseFirestore.class);
        when(mockDb.collection("events")).thenReturn(mockCollectionRef);
        when(mockCollectionRef.document(anyString())).thenReturn(mockDocRef);
        when(mockDocRef.set(any())).thenReturn(mockTask);
        when(mockDocRef.update(anyString(), any(), any())).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockTask;
        });

        // Initialize the EventModel with a mocked context
        mockEvent = spy(new EventModel(context, "Event", mockFacilityId, 9.99, "Description", 10, 25, new Date(), new Date(), false, mockFirestore));
        doNothing().when(mockEvent).notifyViews();
    }

    @Test
    public void testSetTitle() {
        mockEvent.setTitle("New Event Title");
        assertEquals("New Event Title", mockEvent.getTitle());
    }

    @Test
    public void testSetFacilityId() {
        mockEvent.setFacilityId("newFacilityId");
        assertEquals("newFacilityId", mockEvent.getFacilityId());
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
}
