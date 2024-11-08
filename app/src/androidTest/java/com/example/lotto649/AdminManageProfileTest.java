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

import android.provider.Settings;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AdminManageProfileTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

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


    @Test
    public void testDeleteProfilePicture() {
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

        // delete user image
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
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        // delete test entry from db
        testRef.delete();

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
