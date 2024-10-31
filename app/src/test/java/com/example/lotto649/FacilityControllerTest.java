package com.example.lotto649;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.content.Context;

import com.example.lotto649.Controllers.FacilityController;
import com.example.lotto649.Models.FacilityModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class FacilityControllerTest {

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
    FacilityModel mockFacility;

    private FacilityController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        // Mock Firebase Firestore instance
        mockFirestore = mock(FirebaseFirestore.class);
        when(mockFirestore.collection("facilities")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.update(anyString(), any(), any())).thenReturn(mockTask);
        when(mockDocumentReference.set(any())).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);  // Simulate successful completion
            return mockTask;
        });

        mockFacility = spy(new FacilityModel("deviceId"));
        doNothing().when(mockFacility).notifyViews();
        controller = new FacilityController(mockFacility, mockFirestore);
    }

    @Test
    public void testUpdateFacilityName() {
        String newName = "North Pole";

        controller.updateFacilityName(newName);

        verify(mockFacility).setFacilityName(newName);
    }

    @Test
    public void testUpdateAddress() {
        String newAddress = "H0H 0H0";

        controller.updateAddress(newAddress);

        verify(mockFacility).setAddress(newAddress);
    }

    @Test
    public void testSaveToFirestore() {
        mockFacility.setAddress("H0H 0H0");
        mockFacility.setFacilityName("North Pole");
        controller.saveToFirestore();
        verify(mockFirestore).collection("facilities");
        verify(mockCollectionReference).document("deviceId");
        verify(mockDocumentReference).set(new HashMap<String, String>() {{
            put("facility", mockFacility.getFacilityName());
            put("address", mockFacility.getAddress());
        }}, SetOptions.merge());
    }
}
