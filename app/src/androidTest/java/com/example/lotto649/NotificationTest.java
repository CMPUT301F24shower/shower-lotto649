package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationTest {

    private NotificationManager notificationManager;
    private Map<String, List<String>> documentsToDelete; // Map of collection names to document IDs

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() {
        // Initialize NotificationManager
        Context context = ApplicationProvider.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        documentsToDelete = new HashMap<>();
    }

    /**
     * This test creates a generic notification to all and checks if the notification is displayed
     * This tests US 02.07.01
     */
    @Test
    public void testCustomNotificationisReceived() throws InterruptedException {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", true);
            put("organizer", true);
            put("admin", false);
            put("profileImage", "");
        }});
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        DocumentReference notiRef = FirebaseFirestore.getInstance()
                .collection("custom")
                .document("TESTNOTIFICATION");
        notiRef.set(new HashMap<String, Object>() {{
            put("eventId", "THISISATESTEVENT");
            put("title", "THISISATESTNOTI");
            put("message", "Testing Message");
            put("hasSeenNoti", false);
            put("userId",deviceId);
        }});
        addDocumentToCleanup("custom", notiRef.getId());

        Thread.sleep(2000);
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);

        Thread.sleep(2000);
        boolean isNotificationDisplayed = isNotificationPresent("THISISATESTNOTI");
        assertTrue("Notification should be displayed", isNotificationDisplayed);
    }

    /**
     * This test creates a winning notification and checks if the notification is displayed
     * This tests US 02.05.01
     */
    @Test
    public void testWinningNotificationisReceived() throws InterruptedException {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", true);
            put("organizer", true);
            put("admin", false);
            put("profileImage", "");
        }});
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        DocumentReference notiRef = FirebaseFirestore.getInstance()
                .collection("winners")
                .document("TESTNOTIFICATION");
        notiRef.set(new HashMap<String, Object>() {{
            put("eventDeleted", false);
            put("eventId", "THISISATESTEVENT");
            put("hasSeenNoti", false);
            put("latitude", null);
            put("longitude", null);
            put("lottoStatus", "Waiting");
            put("timestamp", today);
            put("userId",deviceId);
        }});
        addDocumentToCleanup("winners", notiRef.getId());

        Thread.sleep(2000);
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);

        Thread.sleep(2000);
        boolean isNotificationDisplayed = isNotificationPresent("Event Lottery System");
        assertTrue("Notification should be displayed", isNotificationDisplayed);
    }

    /**
     * This test creates a cancelled notification and checks if the notification is displayed
     * This tests US 02.07.03
     */
    @Test
    public void testCancelNotificationisReceived() throws InterruptedException {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", true);
            put("organizer", true);
            put("admin", false);
            put("profileImage", "");
        }});
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        DocumentReference notiRef = FirebaseFirestore.getInstance()
                .collection("cancelled")
                .document("TESTNOTIFICATION");
        notiRef.set(new HashMap<String, Object>() {{
            put("eventDeleted", false);
            put("eventId", "THISISATESTEVENT");
            put("hasSeenNoti", false);
            put("latitude", null);
            put("longitude", null);
            put("lottoStatus", "Waiting");
            put("timestamp", today);
            put("userId",deviceId);
        }});
        addDocumentToCleanup("cancelled", notiRef.getId());

        Thread.sleep(2000);
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);

        Thread.sleep(2000);
        boolean isNotificationDisplayed = isNotificationPresent("Event Lottery System");
        assertTrue("Notification should be displayed", isNotificationDisplayed);
    }

    /**
     * This test creates a losing notification and checks if the notification is displayed
     * This tests US 02.07.02
     */
    @Test
    public void testNotSelectedNotificationisReceived() throws InterruptedException {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", true);
            put("organizer", true);
            put("admin", false);
            put("profileImage", "");
        }});
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        DocumentReference notiRef = FirebaseFirestore.getInstance()
                .collection("notSelected")
                .document("TESTNOTIFICATION");
        notiRef.set(new HashMap<String, Object>() {{
            put("eventDeleted", false);
            put("eventId", "THISISATESTEVENT");
            put("hasSeenNoti", false);
            put("latitude", null);
            put("longitude", null);
            put("lottoStatus", "Waiting");
            put("timestamp", today);
            put("userId",deviceId);
        }});
        addDocumentToCleanup("notSelected", notiRef.getId());

        Thread.sleep(2000);
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);

        Thread.sleep(2000);
        boolean isNotificationDisplayed = isNotificationPresent("Event Lottery System");
        assertTrue("Notification should be displayed", isNotificationDisplayed);
    }

    @After
    public void tearDown() {
        for (Map.Entry<String, List<String>> entry : documentsToDelete.entrySet()) {
            String collectionName = entry.getKey();
            for (String documentId : entry.getValue()) {
                FirebaseFirestore.getInstance().collection(collectionName)
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("Deleted document from " + collectionName + ": " + documentId);
                        })
                        .addOnFailureListener(e -> {
                            System.err.println("Failed to delete document from " + collectionName + ": " + e.getMessage());
                        });
            }
        }
    }


    private boolean isNotificationPresent(String expectedTitle) {
        // Check if a notification with the specified title is present
        StatusBarNotification[] activeNotifications = notificationManager.getActiveNotifications();
        for (StatusBarNotification notification : activeNotifications) {
            if (notification.getNotification().extras.getString("android.title").equals(expectedTitle)) {
                return true;
            }
        }
        return false;
    }

    private void addDocumentToCleanup(String collectionName, String documentId) {
        documentsToDelete
                .computeIfAbsent(collectionName, k -> new ArrayList<>())
                .add(documentId);
    }
}

