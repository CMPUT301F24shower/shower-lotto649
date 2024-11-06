package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.provider.Settings;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateUserTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();

    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Tests creating a user, the user is saved based on device ID
     * @throws InterruptedException
     */
    @Test
    public void testCreateUser() throws InterruptedException {
        // Delete the current devices user if it exists
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = db.collection("users").document(deviceId);
        userRef.delete();

        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.account))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.emailEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("john.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneEditText))
                .check(matches(isDisplayed()))
                .perform(typeText("123-456-7890"), closeSoftKeyboard());

        Espresso.onView(withId(R.id.account_save_button)).perform(click());

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("John Doe", document.getString("name"));
                            assertEquals("john.doe@example.com", document.getString("email"));
                            assertEquals("123-456-7890", document.getString("phone"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        onView(withId(R.id.nameEditText))
                .check(matches(withText("John Doe")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("123-456-7890")));
        IdlingRegistry.getInstance().unregister(idlingResource);

    }
}