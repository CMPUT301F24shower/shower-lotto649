package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Checks.checkNotNull;
import static androidx.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.provider.Settings;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AdminManageFacilityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException {
        PermissionHandler.handlePermissions();
    }
    /**
     * Deleting a facility
     * Tests US 03.07.01
     */
    @Test
    public void testDeleteFacility() throws InterruptedException {
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
        //     create example facility
        HashMap<String, Object> data = new HashMap<>();
        data.put("facility", "Test Facility");
        data.put("address", "111 Apple Street");
        // document set so it is first in list
        DocumentReference testRef = db.collection("facilities").document("000000000000000000000000uitest1");
        testRef.set(data, SetOptions.merge());

        //     run test
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.browseFacilities))
                .check(matches(isDisplayed()))
                .perform(click());

        // clicks on facility to test on (will always be at top of list)
        onData(anything())
                .inAdapterView(withId(R.id.browse_facilities_list))
                .atPosition(0)
                .perform(click());

        Thread.sleep(1000);

        // check that correct item was clicked
        onView(withId(R.id.admin_facility_name))
                .check(matches(withText("Test Facility")));

        // check it was written to db
        testRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Test Facility", document.getString("facility"));
                            assertEquals("111 Apple Street", document.getString("address"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        // delete facility
        onView(withId(R.id.admin_delete_facility))
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
}
