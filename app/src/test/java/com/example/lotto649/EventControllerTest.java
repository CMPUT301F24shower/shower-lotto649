package com.example.lotto649;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.example.lotto649.Controllers.EventController;
import com.example.lotto649.Models.EventModel;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class EventControllerTest {
    @Mock
    private EventModel mockEventModel; // Mocking the UserModel

    private EventController eventController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = spy(new EventController(mockEventModel));
    }

    @Test
    public void testUpdateTitle() {
        String newTitle = "New Event";
        eventController.updateTitle(newTitle);
        verify(eventController).updateTitle(newTitle);
    }

    @Test
    public void testUpdateDescription() {
        String newDescription = "New Event";
        eventController.updateDescription(newDescription);
        verify(eventController).updateDescription(newDescription);
    }

    @Test
    public void testUpdateNumberOfSpots() {
        int newNumberOfSpots = 2;
        eventController.updateNumberOfSpots(newNumberOfSpots);
        verify(eventController).updateNumberOfSpots(newNumberOfSpots);
    }

    @Test
    public void testUpdateNumberOfMaxEntrants() {
        int newNumberOfMaxEntrants = 4;
        eventController.updateNumberOfMaxEntrants(newNumberOfMaxEntrants);
        verify(eventController).updateNumberOfMaxEntrants(newNumberOfMaxEntrants);
    }

    @Test
    public void testUpdateStartDate() {
        Date startDate = new Date();
        eventController.updateStartDate(startDate);
        verify(eventController).updateStartDate(startDate);
    }

    @Test
    public void testUpdateEndDate() {
        Date endDate = new Date();
        eventController.updateEndDate(endDate);
        verify(eventController).updateEndDate(endDate);
    }


    @Test
    public void testUpdateCost() {
        double cost = 9.99;
        eventController.updateCost(cost);
        verify(eventController).updateCost(cost);
    }

    @Test
    public void testUpdateGeo() {
        boolean geo = true;
        eventController.updateGeo(geo);
        verify(eventController).updateGeo(geo);
    }

    @Test
    public void testSaveEventToFirestore() {
        eventController.saveEventToFirestore();
        verify(eventController).saveEventToFirestore();
    }

    @Test
    public void testRemoveEventFromFirestore() {
        eventController.removeEventFromFirestore();
        verify(eventController).removeEventFromFirestore();
    }

    @Test
    public void testReturnToEvents() {
        eventController.returnToEvents();
        verify(eventController).returnToEvents();
    }
}

