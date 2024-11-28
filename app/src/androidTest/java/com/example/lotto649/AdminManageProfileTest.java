package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class AdminManageProfileTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Deletes a profile as an admin
     * Tests US 03.02.01
     */
    @Test
    public void testDeleteProfile() {
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
    //     create example profile
        HashMap<String, Object> data = new HashMap<>();
        data.put("admin", false);
        data.put("email", "test@test.com");
        data.put("entrant", true);
        data.put("name", "Testy Test");
        data.put("organizer", false);
        data.put("phone", "");
        data.put("profileImage", "");
        // document set so it is first in list
        DocumentReference testRef = db.collection("users").document("1111111111111111111111111uitest1");
        testRef.set(data, SetOptions.merge());

    //     run test
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.browseProfiles))
                .check(matches(isDisplayed()))
                .perform(click());

        // clicks on user to test on (will always be at top of list)
        onData(anything())
                .inAdapterView(withId(R.id.browse_profiles_list))
                .atPosition(0)
                .perform(click());

        // check that correct item was clicked
        onView(withId(R.id.admin_user_name))
                .check(matches(withText("Testy Test")));

        // check it was written to db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Testy Test", document.getString("name"));
                            assertEquals("test@test.com", document.getString("email"));
                            assertEquals("", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        // delete user
        onView(withId(R.id.admin_delete_user))
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
    }

    /**
     * Deletes a profile Image as an admin
     * Tests US 03.03.01
     */
    @Test
    public void testDeleteProfileImage() throws IOException, ExecutionException, InterruptedException {
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
        //     create example profile
        HashMap<String, Object> data = new HashMap<>();
        data.put("admin", false);
        data.put("email", "test@test.com");
        data.put("entrant", true);
        data.put("name", "Testy Test");
        data.put("organizer", false);
        data.put("phone", "");
        data.put("profileImage", "");
        // document set so it is first in list
        DocumentReference testRef = db.collection("users").document("1111111111111111111111111uitest1");
        testRef.set(data, SetOptions.merge());

        Uri newUri = copyFirebaseImage();
        FirebaseFirestore.getInstance().collection("users").document("1111111111111111111111111uitest1")
                .update("profileImage", newUri.toString());

        //     run test
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.browseProfiles))
                .check(matches(isDisplayed()))
                .perform(click());

        // clicks on user to test on (will always be at top of list)
        onData(anything())
                .inAdapterView(withId(R.id.browse_profiles_list))
                .atPosition(0)
                .perform(click());

        Thread.sleep(1000);

        // check that correct item was clicked
        onView(withId(R.id.admin_user_name))
                .check(matches(withText("Testy Test")));

        // check it was written to db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Testy Test", document.getString("name"));
                            assertEquals("test@test.com", document.getString("email"));
                            assertEquals("", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals(newUri.toString(), document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        // delete user
        onView(withId(R.id.admin_delete_user_image))
                .check(matches(isDisplayed()))
                .perform(click());

        // check it was deleted from db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Testy Test", document.getString("name"));
                            assertEquals("test@test.com", document.getString("email"));
                            assertEquals("", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});
        assertTrue(isImageDeleted());

        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    /**
     * Test browse profiles
     * Tests US 03.05.01
     */
    @Test
    public void testBrowseProfiles() throws InterruptedException {
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
        //     create example profile
        HashMap<String, Object> data = new HashMap<>();
        data.put("admin", false);
        data.put("email", "test@test.com");
        data.put("entrant", true);
        data.put("name", "Testy Test");
        data.put("organizer", false);
        data.put("phone", "");
        data.put("profileImage", "");
        // document set so it is first in list
        DocumentReference testRef = db.collection("users").document("1111111111111111111111111uitest1");
        testRef.set(data, SetOptions.merge());

        //     create example profile
        HashMap<String, Object> data2 = new HashMap<>();
        data2.put("admin", false);
        data2.put("email", "test2@test.com");
        data2.put("entrant", true);
        data2.put("name", "Secondy Testy");
        data2.put("organizer", false);
        data2.put("phone", "54321");
        data2.put("profileImage", "");
        // document set so it is first in list
        DocumentReference testRef2 = db.collection("users").document("1111111111111111111111111uitest2");
        testRef2.set(data2, SetOptions.merge());

        //     create example profile
        HashMap<String, Object> data3 = new HashMap<>();
        data3.put("admin", false);
        data3.put("email", "test3@test.com");
        data3.put("entrant", true);
        data3.put("name", "Thirdy Testy");
        data3.put("organizer", false);
        data3.put("phone", "");
        data3.put("profileImage", "");
        // document set so it is first in list
        DocumentReference testRef3 = db.collection("users").document("1111111111111111111111111uitest3");
        testRef3.set(data3, SetOptions.merge());

        //     run test
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.browseProfiles))
                .check(matches(isDisplayed()))
                .perform(click());

        // clicks on user to test on (will always be at top of list)
        onData(anything())
                .inAdapterView(withId(R.id.browse_profiles_list))
                .atPosition(0)
                .perform(click());

        Thread.sleep(1000);

        // check that correct item was clicked
        onView(withId(R.id.admin_user_name))
                .check(matches(withText("Testy Test")));
        onView(withId(R.id.admin_user_email))
                .check(matches(withText("test@test.com")));

        onView(withId(R.id.back_button))
                .check(matches(isDisplayed()))
                .perform(click());

        onData(anything())
                .inAdapterView(withId(R.id.browse_profiles_list))
                .atPosition(1)
                .perform(click());

        Thread.sleep(1000);

        // check that correct item was clicked
        onView(withId(R.id.admin_user_name))
                .check(matches(withText("Secondy Testy")));
        onView(withId(R.id.admin_user_email))
                .check(matches(withText("test2@test.com")));
        onView(withId(R.id.admin_user_phone))
                .check(matches(withText("54321")));

        onView(withId(R.id.back_button))
                .check(matches(isDisplayed()))
                .perform(click());

        onData(anything())
                .inAdapterView(withId(R.id.browse_profiles_list))
                .atPosition(2)
                .perform(click());

        Thread.sleep(1000);

        // check that correct item was clicked
        onView(withId(R.id.admin_user_name))
                .check(matches(withText("Thirdy Testy")));
        onView(withId(R.id.admin_user_email))
                .check(matches(withText("test3@test.com")));

        IdlingRegistry.getInstance().unregister(idlingResource);
    }


    private Uri copyFirebaseImage() throws IOException, ExecutionException, InterruptedException {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://shower-lotto649.firebasestorage.app");
        StorageReference sourceRef = storage.getReference().child("profileImages/test_profile_image.jpg");
        StorageReference destinationRef = storage.getReference().child("profileImages/new_test_profile_image.jpg");

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
        StorageReference imageRef = storage.getReference().child("profileImages/new_test_profile_image.jpg");

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
