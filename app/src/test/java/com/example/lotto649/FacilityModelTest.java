package com.example.lotto649;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class FacilityModelTest {
    private FacilityModel mockFacility;

    @Before
    public void setUp() {
        mockFacility = spy(new FacilityModel("deviceId"));
        doNothing().when(mockFacility).notifyViews();
    }

    @Test
    public void testSetFacilityName() {
        String newName = "Van Vliet Center";
        mockFacility.setFacilityName(newName);

        assertEquals(newName, mockFacility.getFacilityName());
        verify(mockFacility).notifyViews();
    }

    @Test
    public void testSetAddress() {
        String newAddress = "87 Ave & 114 St";
        mockFacility.setAddress(newAddress);

        assertEquals(newAddress, mockFacility.getAddress());
        verify(mockFacility).notifyViews();
    }
}
