package com.example.lotto649;

import android.util.ArraySet;

import java.util.Set;

public abstract class AbstractObservable {
    private final transient Set<AbstractObserver> observers;
    protected AbstractObservable() {
        observers = new ArraySet<>();
    }
    public void addObserver(AbstractObserver observer) {
        observers.add(observer);
        observer.update(this);
    }
    public void removeObserver(AbstractObserver observer) {
        observers.remove(observer);
    }
    public void notifyObservers() {
        // Call this from setters
        for (AbstractObserver observer : observers) {
            observer.update(this);
        }
    }
}
