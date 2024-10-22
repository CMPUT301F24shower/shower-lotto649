/**
 * AbstractObservable is part of the Observer design pattern.
 * It represents the base class for observable objects, which are responsible for
 * maintaining a list of observers and notifying them whenever the state of the
 * observable changes.
 * <p>
 * This implementation is used from the lecture slides:
 * https://ualberta-cmput301.github.io/general/slides/020mvc.pdf
 * </p>
 */
package com.example.lotto649;

import android.util.ArraySet;
import java.util.Set;

public abstract class AbstractObservable {
    // A set of observers observing this observable
    private final transient Set<AbstractObserver> observers;

    /**
     * Constructor for AbstractObservable class.
     * Initializes the set of observers that will observe this observable.
     */
    protected AbstractObservable() {
        observers = new ArraySet<>();
    }

    /**
     * Adds an observer to the set of observers that observe this observable.
     * Immediately updates the observer with the current state of the observable.
     *
     * @param observer the observer to be added and notified of updates
     */
    public void addObserver(AbstractObserver observer) {
        observers.add(observer);
        observer.update(this);
    }

    /**
     * Removes an observer from the set of observers observing this observable.
     *
     * @param observer the observer to be removed from the observer list
     */
    public void removeObserver(AbstractObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all the observers that are observing the observable.
     * Typically called from setters when the state of the observable changes.
     */
    public void notifyObservers() {
        // Call this from setters to update all observers
        for (AbstractObserver observer : observers) {
            observer.update(this);
        }
    }
}
