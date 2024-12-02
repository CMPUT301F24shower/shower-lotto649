package com.example.lotto649;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

import android.provider.Settings;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class QrFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * This test joins an event, scanning the QR code will be simulated by sending a deeplink
     * This tests US 01.01.01 and US 01.06.01 and US 01.06.02 and US 01.01.02
     */
    @Test
    public void testQrCodeisDisplayed() throws IOException, InterruptedException {
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayFormatted = dateFormat.format(today);

        // Use Calendar to calculate tomorrow's date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        // TODO make sure this matches how we are creating events
        DocumentReference eventRef = FirebaseFirestore.getInstance().collection("events").document("THISISATESTEVENT");
        eventRef.set(new HashMap<String, Object>() {{
            put("title", "TEST EVENT");
            put("facilityId", "TESTFACILITYID");
            put("organizerId", deviceId);
            put("description", "A silly little event");
            put("numberOfSpots", 5);
            put("numberOfMaxEntrants", 10);
            put("startDate", today);
            put("endDate", tomorrow);
            put("qrCode", "THISISATESTEVENT");
            put("posterImage", "");
            put("geo",true);
        }});

        Thread.sleep(2000);
        ElapsedTimeIdlingResource idlingResource = new ElapsedTimeIdlingResource(5000);
        IdlingRegistry.getInstance().register(idlingResource);

        onData(anything())
                .inAdapterView(withId(R.id.event_contents))
                .atPosition(0)
                .perform(click());

        Thread.sleep(100);
        onView(withId(R.id.organizer_event_options)).perform(click());

        Thread.sleep(100);
        onView(withId(R.id.org_dialog_view_qr)).perform(click());

        Thread.sleep(100);
        onView(withId(R.id.qrCodeImageView)).check(matches(isDisplayed()));
    }
}