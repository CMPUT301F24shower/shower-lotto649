package com.example.lotto649;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.provider.Settings;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import com.example.lotto649.Controllers.EventsController;

public class HomeFragmentAddEventTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private String deviceId;

    @Before
    public void setUp() {
        // Setup Firestore mock data
        deviceId = Settings.Secure.getString(
                androidx.test.InstrumentationRegistry.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Add user document
        DocumentReference userRef = firestore.collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "Test User");
            put("email", "testuser@example.com");
        }});

        // Add facility document
        DocumentReference facilityRef = firestore.collection("facilities").document(deviceId);
        facilityRef.set(new HashMap<String, Object>() {{
            put("facilityName", "Test Facility");
            put("address", "123 Test Lane");
        }});
    }

    @Test
    public void testAddEventButtonVisibilityAndClick() throws InterruptedException {
        ActivityScenario.launch(MainActivity.class);
        // Wait for Firestore setup
        Thread.sleep(2000);

        // Check if the Add Event button is displayed
        onView(withId(R.id.addButton)).check(matches(isDisplayed()));

        // Simulate button click
        onView(withId(R.id.addButton)).perform(click());

        // Verify that the button click triggers the addEvent action in the EventsController
        // (Since this relies on an actual method call, you may need a mock or log output check)
        onView(withId(R.id.eventFragment))
                .check(matches(isDisplayed()));
    }
}
