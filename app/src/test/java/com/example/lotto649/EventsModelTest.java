package com.example.lotto649;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.provider.Settings;

import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.EventsModel;
import com.example.lotto649.Models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Ordering;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EventsModelTest {

    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockCollectionReference;
    @Mock
    private Query mockQuery;
    @Mock
    private Task<QuerySnapshot> mockTask;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private DocumentSnapshot mockDocumentSnapshot;
    @Mock
    FirebaseApp mockFirebaseApp;
    @Mock
    EventsModel.MyEventsCallback callback;

    private Context context;
    private EventsModel eventsModel;
    private UserModel mockUser;
    private MyApp mockApp;
    private QuerySnapshot mockDocSnap;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();

        // Set up FirebaseApp with dummy options to avoid "not initialized" error
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:1234567890:android:abcdef") // Required for Firebase
                .setApiKey("fakeApiKey") // Required for Firebase
                .setProjectId("fakeProjectId") // Required for Firebase
                .build();

        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context, options);
        }

        // Mock FirebaseFirestore and related Firestore objects
        mockFirestore = mock(FirebaseFirestore.class);
        mockCollectionReference = mock(CollectionReference.class);
        mockQuery = mock(Query.class);
        mockTask = mock(Task.class);
        mockDocSnap = mock(QuerySnapshot.class);

        // Set up behavior for Firestore query
        when(mockFirestore.collection("events")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.whereEqualTo(anyString(), anyString())).thenReturn(mockQuery);
        when(mockCollectionReference.whereEqualTo(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);

        // Set up Task to simulate success and return mock QuerySnapshot
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocSnap);


        // Mock the QuerySnapshot to return a list of DocumentSnapshots
        List<DocumentSnapshot> mockDocuments = new ArrayList<>();
        DocumentSnapshot mockDocument = mock(QueryDocumentSnapshot.class); // Using QueryDocumentSnapshot here
        mockDocuments.add(mockDocument);

        when(mockDocSnap.getDocuments()).thenReturn(mockDocuments);

        // Set up success and failure listeners for the task
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<QuerySnapshot> listener = invocation.getArgument(0);
            listener.onSuccess(mockDocSnap); // Use the mock QuerySnapshot
            return mockTask;
        });
        when(mockTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Simulated failure")); // Simulate failure
            return mockTask;
        });

        // Initialize the user model and application instance
        mockUser = new UserModel(context, "John Doe", "john@example.com", "123456789", mockFirestore);
        MyApp mockAppInstance = spy(MyApp.class);
        MyApp.setInstance(mockAppInstance);
        mockApp = MyApp.getInstance();
        mockApp.setUserModel(mockUser);

        // Initialize EventsModel with the mock Firestore instance
        eventsModel = spy(new EventsModel(mockFirestore));
    }



    @Test
    public void testFetchEventsByOrganizerId_Success() {
        // Mock successful retrieval of documents
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.getDocuments()).thenReturn(Arrays.asList(mockDocumentSnapshot));

        // Mock the callback to verify results
        EventsModel.EventFetchCallback callback = mock(EventsModel.EventFetchCallback.class);
        EventsModel.fetchEventsByOrganizerId(callback, mockFirestore);

        verify(mockFirestore.collection("events")).whereEqualTo(anyString(), any());
        verify(mockTask).addOnCompleteListener(any());
    }
}
