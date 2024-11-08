package com.example.lotto649;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.example.lotto649.Controllers.EventController;
import com.example.lotto649.Models.EventModel;

@RunWith(RobolectricTestRunner.class)
public class EventControllerTest {
    @Mock
    private EventModel mockEventModel; // Mocking the UserModel

    private EventController eventController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController(mockEventModel);
    }

    @Test
    public void testUpdateTitle() {
        String newName = "New Event";

        eventController.updateTitle(newName);

        verify(eventController).setName(newName); // Verify that setName was called with newName
    }
}
