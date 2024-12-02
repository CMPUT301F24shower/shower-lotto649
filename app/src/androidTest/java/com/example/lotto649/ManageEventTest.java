package com.example.lotto649;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.is;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class ManageEventTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException {
        PermissionHandler.handlePermissions();
    }

    @Test
    public void testCreateEventWithCustomDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String eventId = "testEvent" + System.currentTimeMillis();
        DocumentReference eventRef = db.collection("events").document(eventId);

        // Navigate to EventFragment
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        // Check if the Add Event button is displayed
        onView(withId(R.id.addButton)).check(matches(isDisplayed()));
        // Simulate button click
        onView(withId(R.id.addButton)).perform(click());

        // Fill out event details
        onView(allOf(isDescendantOfA(withId(R.id.eventTitle)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("Sample Event"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.eventDescription)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("This is a test event."), closeSoftKeyboard());

        // Open and set the start date using the custom dialog
        onView(withId(R.id.eventLotteryStartDate)).perform(click());
        onView(withClassName(is("android.widget.DatePicker")))
                .perform(PickerActions.setDate(2024, 12, 10));
        onView(withId(android.R.id.button1)).perform(click()); // Confirm date
        onView(withClassName(is("android.widget.TimePicker")))
                .perform(PickerActions.setTime(10, 0));
        onView(withId(android.R.id.button1)).perform(click()); // Confirm time

        // Open and set the end date using the custom dialog
        onView(withId(R.id.eventLotteryEndDate)).perform(click());
        onView(withClassName(is("android.widget.DatePicker")))
                .perform(PickerActions.setDate(2024, 12, 12));
        onView(withId(android.R.id.button1)).perform(click()); // Confirm date
        onView(withClassName(is("android.widget.TimePicker")))
                .perform(PickerActions.setTime(18, 0));
        onView(withId(android.R.id.button1)).perform(click()); // Confirm time

        // Set spots and max entrants
        onView(allOf(isDescendantOfA(withId(R.id.eventSpots)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("50"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.eventMaxEntrants)), isAssignableFrom(TextInputEditText.class)))
                .perform(typeText("100"), closeSoftKeyboard());

        // Save the event
        onView(withId(R.id.saveButton)).perform(click());

        // Verify event in Firestore
        eventRef.get().addOnCompleteListener(task -> {
            assertTrue(task.isSuccessful());
            assertNotNull(task.getResult());
            assertTrue(task.getResult().exists());
            assertEquals("Sample Event", task.getResult().getString("title"));
            assertEquals("This is a test event.", task.getResult().getString("description"));
        });
    }


    @Test
    public void testEditEvent() {
        String eventId = "testEvent" + System.currentTimeMillis();
        DocumentReference eventRef = FirebaseFirestore.getInstance().collection("events").document(eventId);

        // Prepopulate Firestore with event data
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put("title", "Initial Event");
        eventData.put("description", "Original description");
        eventData.put("spots", 25);
        eventData.put("maxEntrants", 50);
        eventRef.set(eventData);

        // Navigate to EventFragment
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        // Check if the Add Event button is displayed
        onView(withId(R.id.addButton)).check(matches(isDisplayed()));
        // Simulate button click
        onView(withId(R.id.addButton)).perform(click());

        // Verify initial data
        onView(withId(R.id.eventTitle)) // Directly target the TextInputEditText
                .perform(click()) // Focus if necessary
                .check(matches(withText("Initial Event")));
        onView(withId(R.id.eventDescription))
                .perform(click())
                .check(matches(withText("Original description")));

        // Edit data
        onView(withId(R.id.eventTitle))
                .perform(clearText(), typeText("Updated Event"), closeSoftKeyboard());
        onView(withId(R.id.eventDescription))
                .perform(clearText(), typeText("Updated description"), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());

        // Verify updated data in Firestore
        eventRef.get().addOnCompleteListener(task -> {
            assertTrue(task.isSuccessful());
            assertNotNull(task.getResult());
            assertTrue(task.getResult().exists());
            assertEquals("Updated Event", task.getResult().getString("title"));
            assertEquals("Updated description", task.getResult().getString("description"));
        });
    }
}
