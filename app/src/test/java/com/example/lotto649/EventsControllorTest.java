package com.example.lotto649;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.example.lotto649.Controllers.EventsController;
import com.example.lotto649.Models.EventModel;
import com.example.lotto649.Models.EventsModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class EventsControllorTest {
    @Mock
    private EventsModel mockEventsModel;

    private EventsController eventsController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventsController = spy(new EventsController(mockEventsModel));

        MyApp mockAppInstance = spy(MyApp.class);
        MyApp.setInstance(mockAppInstance);
    }

    @Test
    public void testGetMyEvents() {
        EventsModel.MyEventsCallback callback = mock(EventsModel.MyEventsCallback.class);
        eventsController.getMyEvents(callback);
        verify(mockEventsModel).getMyEvents(callback);
    }

    @Test
    public void testAddEvent() {
        eventsController.addEvent();
        verify(eventsController).addEvent();
    }

    @Test
    public void testEditEvent() {
        EventModel mockEvent = mock(EventModel.class);
        eventsController.editEvent(mockEvent);
        verify(eventsController).editEvent(mockEvent);
    }
}
