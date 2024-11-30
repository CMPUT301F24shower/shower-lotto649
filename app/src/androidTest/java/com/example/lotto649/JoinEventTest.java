package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class JoinEventTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<MainActivity>(getCustomIntent());

    private Intent getCustomIntent() {
        // Create a custom Intent with a data URI
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("https://example.com?eventId=THISISATESTEVENT");
        intent.setData(uri);
        intent.setClass(ApplicationProvider.getApplicationContext(), MainActivity.class);
        return intent;
    }
    /**
     * This test joins an event, scanning the QR code will be simulated by sending a deeplink
     * This tests US 01.01.01 and US 01.06.01 and US 01.06.02 and US 01.01.02
     */
    @Test
    public void testJoinAndUnjoinEventAccountCreated() throws IOException, InterruptedException {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayFormatted = dateFormat.format(today);

        // Use Calendar to calculate tomorrow's date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        String tomorrowFormatted = dateFormat.format(tomorrow);
        // TODO make sure this matches how we are creating events
        DocumentReference eventRef = FirebaseFirestore.getInstance().collection("events").document("THISISATESTEVENT");
        eventRef.set(new HashMap<String, Object>() {{
            put("title", "TEST EVENT");
            put("facilityId", "TESTFACILITYID");
            put("organizerId", "TESTFACILITYID");
            put("description", "A silly little event");
            put("numberOfSpots", 5);
            put("numberOfMaxEntrants", 10);
            put("startDate", today);
            put("endDate", tomorrow);
            put("qrCode", null);
            put("posterImage", "");
            put("geo",true);
        }});

        Thread.sleep(2000);
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);

        onView(withId(R.id.admin_event_name)).check(matches(isDisplayed())).check(matches(withText("TEST EVENT")));
        onView(withId(R.id.admin_event_status)).check(matches(isDisplayed())).check(matches(withText("OPEN")));
        onView(withId(R.id.admin_event_location)).check(matches(isDisplayed())).check(matches(withText("LOCATION")));
        // TODO update this when it is fixed
        onView(withId(R.id.admin_event_spots)).check(matches(isDisplayed())).check(matches(withText("TODO")));
        onView(withId(R.id.admin_event_attendees)).check(matches(isDisplayed())).check(matches(withText("5 Attendees")));
        onView(withId(R.id.admin_event_dates)).check(matches(isDisplayed())).check(matches(withText("Enter between " + todayFormatted + " - " + tomorrowFormatted)));
        onView(withId(R.id.admin_event_geo)).check(matches(isDisplayed())).check(matches(withText("Requires GeoLocation Tracking")));
        onView(withId(R.id.admin_event_description)).check(matches(isDisplayed())).check(matches(withText("A silly little event")));
        onView(withId(R.id.admin_event_poster_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.join_event_wait_list)).check(matches(isDisplayed())).perform(click());

        Thread.sleep(100);
        onView(withId(R.id.join_event_wait_list)).check(matches(not(isDisplayed())));
        onView(withId(R.id.unjoin_event_wait_list)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_event_name)).check(matches(isDisplayed())).check(matches(withText("TEST EVENT")));
        onView(withId(R.id.admin_event_status)).check(matches(isDisplayed())).check(matches(withText("OPEN")));
        onView(withId(R.id.admin_event_location)).check(matches(isDisplayed())).check(matches(withText("LOCATION")));
        // TODO update this when it is fixed
        onView(withId(R.id.admin_event_spots)).check(matches(isDisplayed())).check(matches(withText("TODO")));
        onView(withId(R.id.admin_event_attendees)).check(matches(isDisplayed())).check(matches(withText("5 Attendees")));
        onView(withId(R.id.admin_event_dates)).check(matches(isDisplayed())).check(matches(withText("Enter between " + todayFormatted + " - " + tomorrowFormatted)));
        onView(withId(R.id.admin_event_geo)).check(matches(isDisplayed())).check(matches(withText("Requires GeoLocation Tracking")));
        onView(withId(R.id.admin_event_description)).check(matches(isDisplayed())).check(matches(withText("A silly little event")));
        onView(withId(R.id.admin_event_poster_layout)).check(matches(isDisplayed()));

        DocumentReference signUpRef = FirebaseFirestore.getInstance().collection("signUps").document("THISISATESTEVENT_" + deviceId);
        signUpRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assertNotNull("Document should not be null", document);
                if (document.exists()) {
                    assertEquals("THISISATESTEVENT", document.getString("eventId"));
                    assertEquals(deviceId, document.getString("userId"));
                }
            } else {
                throw new AssertionError("Failed to fetch document");
            }});


        onView(withId(R.id.unjoin_event_wait_list)).perform(click());


        onView(withId(R.id.unjoin_event_wait_list)).check(matches(not(isDisplayed())));
        onView(withId(R.id.join_event_wait_list)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_event_name)).check(matches(isDisplayed())).check(matches(withText("TEST EVENT")));
        onView(withId(R.id.admin_event_status)).check(matches(isDisplayed())).check(matches(withText("OPEN")));
        onView(withId(R.id.admin_event_location)).check(matches(isDisplayed())).check(matches(withText("LOCATION")));
        // TODO update this when it is fixed
        onView(withId(R.id.admin_event_spots)).check(matches(isDisplayed())).check(matches(withText("TODO")));
        onView(withId(R.id.admin_event_attendees)).check(matches(isDisplayed())).check(matches(withText("5 Attendees")));
        onView(withId(R.id.admin_event_dates)).check(matches(isDisplayed())).check(matches(withText("Enter between " + todayFormatted + " - " + tomorrowFormatted)));
        onView(withId(R.id.admin_event_geo)).check(matches(isDisplayed())).check(matches(withText("Requires GeoLocation Tracking")));
        onView(withId(R.id.admin_event_description)).check(matches(isDisplayed())).check(matches(withText("A silly little event")));
        onView(withId(R.id.admin_event_poster_layout)).check(matches(isDisplayed()));

        signUpRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    fail();
                }
            }});

        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
