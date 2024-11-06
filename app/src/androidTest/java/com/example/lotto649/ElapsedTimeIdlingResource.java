/**
 * ElapsedTimeIdlingResource is an IdlingResource that pauses Espresso tests until a specified waiting time
 * has elapsed. This is useful when waiting for a specific duration, simulating delays in UI loading or background
 * processes.
 * Source for this code: https://blog.bananacoding.com/2016/08/08/testing-elapsed-time-with-expresso-idling-resource/
 */
package com.example.lotto649;

import androidx.test.espresso.IdlingResource;

public class ElapsedTimeIdlingResource implements IdlingResource {

    private final long startTime;
    private final long waitingTime;
    private ResourceCallback resourceCallback;

    /**
     * Initializes an instance of ElapsedTimeIdlingResource.
     *
     * @param waitingTime The amount of time, in milliseconds, that Espresso should wait before marking this resource as idle.
     */
    public ElapsedTimeIdlingResource(long waitingTime) {
        this.startTime = System.currentTimeMillis();
        this.waitingTime = waitingTime;
    }

    /**
     * Gets the name of this IdlingResource, which includes the class name and the specified waiting time.
     *
     * @return A unique name for this IdlingResource, combining the class name and waiting time.
     */
    @Override
    public String getName() {
        return ElapsedTimeIdlingResource.class.getName() + ":" + waitingTime;
    }

    /**
     * Determines whether this IdlingResource is idle.
     *
     * @return True if the elapsed time since initialization is greater than or equal to the specified waiting time; false otherwise.
     */
    @Override
    public boolean isIdleNow() {
        long elapsed = System.currentTimeMillis() - startTime;
        boolean isIdle = (elapsed >= waitingTime);
        if (isIdle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    /**
     * Registers a callback to be notified when this resource transitions from non-idle to idle.
     *
     * @param callback The callback to notify when the resource becomes idle.
     */
    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
