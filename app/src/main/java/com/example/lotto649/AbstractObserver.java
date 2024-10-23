/**
 * AbstractObserver is part of the Observer design pattern.
 * It represents an abstract base class for observers that observe and respond to changes
 * in an observable object. This class defines methods for starting and stopping
 * the observation of an observable object.
 * This implementation is used from the lecture slides:
 * https://ualberta-cmput301.github.io/general/slides/020mvc.pdf
 */

package com.example.lotto649;

public abstract class AbstractObserver {
    public transient AbstractObservable observable;

    /**
     * Starts observing a specified observable object. Once the observer starts observing,
     * it will receive updates whenever the observable object changes.
     * <p>
     * This method should be called during the observer's initialization or when it's ready
     * to start receiving updates from the observable object.
     * </p>
     *
     * @param observable the observable object to be observed by this observer
     * @throws RuntimeException if the observer is already observing a different observable object
     */
    public void startObserving(AbstractObservable observable) {
        // call me from the constructor or when ready
        if (this.observable != null) {
            throw new RuntimeException("Can't view two models!");
        }
        this.observable = observable;
        observable.addObserver(this);
    }

    /**
     * Stops the observer from observing the observable object. This should be called when
     * the observer is no longer needed or being destroyed to clean up the observer-observable relationship.
     */
    public void stopObserving() {
        // call me from delete() or close() etc.
        observable.removeObserver(this);
        this.observable = null;
    }

    /**
     * An abstract method that must be implemented by concrete observers to define
     * how they should be updated when the observed observable object changes.
     *
     * @param whoUpdatedMe the observable object that triggered the update
     */
    public abstract void update(AbstractObservable whoUpdatedMe);
}