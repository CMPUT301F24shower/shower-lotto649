package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.openLinkWithUri;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsAnything.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.widget.ImageView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.example.lotto649.Models.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class AdminManageEventTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException {
        PermissionHandler.handlePermissions();
    }

    /**
     * Tests deleting an event
     * Tests US 03.01.01
     */
    @Test
    public void testDeleteEvent() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //     create admin profile to test with
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", false);
            put("organizer", false);
            put("admin", true);
            put("profileImage", "");
        }});
        //     create example event
        HashMap<String, Object> data = new HashMap<>();
        Date testDate = new Date();
        data.put("description", "");
        data.put("endDate", testDate);
        data.put("facilityId", "");
        data.put("geo", false);
        data.put("numberOfMaxEntrants", 5);
        data.put("numberOfSpots", 3);
        data.put("organizerId", "");
        data.put("posterImage", "");
        data.put("qrCode", "");
        data.put("startDate", testDate);
        data.put("title", "UI Test Event");
//        data.put("waitingList", (new ArrayList<UserModel>()).stream()
//                .map(UserModel::getDeviceId) // Assuming UserModel has a getDeviceId() method
//                .collect(Collectors.toList()));
        // document set so it is first in list
        DocumentReference testRef = db.collection("events").document("1111111111111111111111111uitest1");
        testRef.set(data, SetOptions.merge());

        //     run test
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.browseEvents))
                .check(matches(isDisplayed()))
                .perform(click());

        // clicks on event to test on (will always be at top of list)
        onData(anything())
                .inAdapterView(withId(R.id.browse_events_list))
                .atPosition(0)
                .perform(click());

        Thread.sleep(1000);

        // check that correct item was clicked
        onView(withId(R.id.admin_event_name))
                .check(matches(isDisplayed()))
                .check(matches(withText("UI Test Event")));

        // check it was written to db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("", document.getString("description"));
                            assertEquals(testDate, document.getDate("endDate"));
                            assertEquals("", document.getString("facilityId"));
                            assertEquals(false, document.getBoolean("geo"));
                            assertEquals(5, ((Long) document.get("numberOfMaxEntrants")).intValue());
                            assertEquals(3, ((Long) document.get("numberOfSpots")).intValue());
                            assertEquals("", document.getString("organizerId"));
                            assertEquals("", document.getString("posterImage"));
                            assertEquals("", document.getString("qrCode"));
                            assertEquals(testDate, document.getDate("startDate"));
                            assertEquals("UI Test Event", document.getString("title"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        // delete event
        onView(withId(R.id.admin_delete_event))
                .check(matches(isDisplayed()))
                .perform(click());

        // check it was deleted from db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            throw new AssertionError("Failed to delete document");
                        }
                    }});

        IdlingRegistry.getInstance().unregister(idlingResource);
        userRef.delete();
        testRef.delete();
    }

    /**
     * Deleting poster image for an event
     * Tests US 03.03.01
     */
    @Test
    public void testAdminDeleteEventImage() throws InterruptedException, IOException, ExecutionException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //     create admin profile to test with
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", false);
            put("organizer", false);
            put("admin", true);
            put("profileImage", "");
        }});
        //     create example event
        HashMap<String, Object> data = new HashMap<>();
        Date testDate = new Date();
        data.put("description", "");
        data.put("endDate", testDate);
        data.put("facilityId", "");
        data.put("geo", false);
        data.put("numberOfMaxEntrants", 5);
        data.put("numberOfSpots", 3);
        data.put("organizerId", "");
        data.put("posterImage", "");
        data.put("qrCode", "");
        data.put("startDate", testDate);
        data.put("title", "UI Test Event");
        // document set so it is first in list
        DocumentReference testRef = db.collection("events").document("000000000000000000000000uitest1");
        testRef.set(data, SetOptions.merge());

        Uri newUri = copyFirebaseImage();
        FirebaseFirestore.getInstance().collection("events").document("000000000000000000000000uitest1")
                .update("posterImage", newUri.toString());

        //     run test
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        Thread.sleep(1000);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.browseEvents))
                .check(matches(isDisplayed()))
                .perform(click());

        // clicks on event to test on (will always be at top of list)
        onData(anything())
                .inAdapterView(withId(R.id.browse_events_list))
                .atPosition(0)
                .perform(click());


        Thread.sleep(1000);

        // check that correct item was clicked
        onView(withId(R.id.admin_event_name))
                .check(matches(isDisplayed()))
                .check(matches(withText("UI Test Event")));

        // check it was written to db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("", document.getString("description"));
                            assertEquals(testDate, document.getDate("endDate"));
                            assertEquals("", document.getString("facilityId"));
                            assertEquals(false, document.getBoolean("geo"));
                            assertEquals(5, ((Long) document.get("numberOfMaxEntrants")).intValue());
                            assertEquals(3, ((Long) document.get("numberOfSpots")).intValue());
                            assertEquals("", document.getString("organizerId"));
                            assertEquals(newUri.toString(), document.getString("posterImage"));
                            assertEquals("", document.getString("qrCode"));
                            assertEquals(testDate, document.getDate("startDate"));
                            assertEquals("UI Test Event", document.getString("title"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        // delete event image
        onView(withId(R.id.admin_delete_event_image))
                .check(matches(isDisplayed()))
                .perform(click());

        // check it was deleted from db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("", document.getString("description"));
                            assertEquals(testDate, document.getDate("endDate"));
                            assertEquals("", document.getString("facilityId"));
                            assertEquals(false, document.getBoolean("geo"));
                            assertEquals(5, ((Long) document.get("numberOfMaxEntrants")).intValue());
                            assertEquals(3, ((Long) document.get("numberOfSpots")).intValue());
                            assertEquals("", document.getString("organizerId"));
                            assertEquals("", document.getString("posterImage"));
                            assertEquals("", document.getString("qrCode"));
                            assertEquals(testDate, document.getDate("startDate"));
                            assertEquals("UI Test Event", document.getString("title"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});
        assertTrue(isImageDeleted());
        onView(withId(R.id.admin_event_name))
                .check(matches(isDisplayed()))
                .check(matches(withText("UI Test Event")));
        onView(withId(R.id.admin_delete_event_image))
                .check(matches(not(isDisplayed())));
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    private Uri copyFirebaseImage() throws IOException, ExecutionException, InterruptedException {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference sourceRef = storage.getReference().child("posterImages/test_profile_image.jpg");
        StorageReference destinationRef = storage.getReference().child("posterImages/new_test_profile_image.jpg");

        Context context = ApplicationProvider.getApplicationContext();
        File tempFile = new File(context.getCacheDir(), "temp_image.webp");

        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }

        Task<byte[]> downloadTask = sourceRef.getBytes(Long.MAX_VALUE);
        byte[] imageData = Tasks.await(downloadTask);

        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(imageData);
        }

        Uri tempFileUri = Uri.fromFile(tempFile);
        UploadTask uploadTask = destinationRef.putFile(tempFileUri);
        Tasks.await(uploadTask);

        Task<Uri> getDownloadUriTask = destinationRef.getDownloadUrl();
        Uri downloadUri = Tasks.await(getDownloadUriTask);

        return downloadUri;
    }

    private boolean isImageDeleted() {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference imageRef = storage.getReference().child("posterImages/new_test_profile_image.jpg");

        try {
            Tasks.await(imageRef.getMetadata());
            return false;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof StorageException) {
                StorageException storageException = (StorageException) e.getCause();
                if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    return true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
