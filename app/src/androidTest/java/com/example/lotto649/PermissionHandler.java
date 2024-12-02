package com.example.lotto649;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.runner.RunWith;

public class PermissionHandler {

    public static void handlePermissions() throws UiObjectNotFoundException {
        // Get the UiDevice instance
        UiDevice device = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation());

        // Wait for the permission dialog to appear
        device.waitForIdle();

        // Find the "Allow" button and click it
        UiObject allowButton = device.findObject(new UiSelector().text("Allow"));
        if (allowButton.exists() && allowButton.isEnabled()) {
            allowButton.click();
        }

        // Handle Location Permission
        UiObject allowLocation = device.findObject(new UiSelector().text("While using the app"));
        if (allowLocation.exists()) {
            allowLocation.click();
        }
    }
}
