package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
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
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(AndroidJUnit4.class)
public class ManageProfileTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Tests creating a user, the user is saved based on device ID to firestore
     * This tests US 01.02.01 and US 01.07.01
     */
    @Test
    public void testCreateUser() {
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
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));

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

    /**
     * Tests editing user, the edits should be reflected in both firebase and the UI
     * This tests US 01.02.02
     */
    @Test
    public void testEditUser() {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        userRef.set(new HashMap<String, Object>() {{
            put("name", "John Tester");
            put("email", "john@example.com");
            put("phone", "999-999-9999");
            put("entrant", true);
            put("organizer", false);
            put("admin", false);
            put("profileImage", "");
        }});

        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.account))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.nameEditText))
                .check(matches(withText("John Tester")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("999-999-9999")));
        onView(withId(R.id.account_save_button)).check(matches(isDisplayed()));

        // Edit name
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("Jane User"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Jane User", document.getString("name"));
                            assertEquals("john@example.com", document.getString("email"));
                            assertEquals("999-999-9999", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});
        onView(withId(R.id.nameEditText))
                .check(matches(withText("Jane User")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("999-999-9999")));

        // Edit email
        onView(withId(R.id.emailEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("jane.doe@example.com"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Jane User", document.getString("name"));
                            assertEquals("jane.doe@example.com", document.getString("email"));
                            assertEquals("999-999-9999", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});
        onView(withId(R.id.nameEditText))
                .check(matches(withText("Jane User")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("jane.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("999-999-9999")));

        // Edit phone
        onView(withId(R.id.phoneEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("123-456-7890"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("Jane User", document.getString("name"));
                            assertEquals("jane.doe@example.com", document.getString("email"));
                            assertEquals("123-456-7890", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        onView(withId(R.id.nameEditText))
                .check(matches(withText("Jane User")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("jane.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("123-456-7890")));
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    /**
     * Tests creating a user without a phone number, the user is saved based on device ID to firestore
     * This tests US 01.02.01
     */
    @Test
    public void testCreateUserNoPhone() {
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
                .check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.account_save_button)).perform(click());

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assertNotNull("Document should not be null", document);
                        if (document.exists()) {
                            assertEquals("John Doe", document.getString("name"));
                            assertEquals("john.doe@example.com", document.getString("email"));
                            assertEquals("", document.getString("phone"));
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));
                        }
                    } else {
                        throw new AssertionError("Failed to fetch document");
                    }});

        onView(withId(R.id.nameEditText))
                .check(matches(withText("John Doe")));
        onView(withId(R.id.emailEditText))
                .check(matches(withText("john.doe@example.com")));
        onView(withId(R.id.phoneEditText))
                .check(matches(withText("")));
        IdlingRegistry.getInstance().unregister(idlingResource);

    }

    // There are no automated tests for uploading images, this requires a human eye to test, and a specific device to select different images, therefore no point in automating

    /**
     * Tests creating that the profile image is created from the users initials
     * This tests US 01.03.03
     */
    @Test
    public void testGeneratedProfileImage() {
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
        onView(withId(R.id.imagePlaceholder))
            .check(matches(withText("")));
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
                            assertEquals(false, document.getBoolean("admin"));
                            assertEquals(false, document.getBoolean("organizer"));
                            assertEquals(true, document.getBoolean("entrant"));
                            assertEquals("", document.getString("profileImage"));

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
        onView(withId(R.id.imagePlaceholder))
                .check(matches(withText("JD")));

        // Edit name
        onView(withId(R.id.nameEditText))
                .check(matches(isDisplayed()))
                .perform(clearText(), typeText("Billy Jim"), closeSoftKeyboard());
        onView(withId(R.id.account_save_button)).perform(click());
        onView(withId(R.id.imagePlaceholder))
                .check(matches(withText("BJ")));
        IdlingRegistry.getInstance().unregister(idlingResource);

    }
}
