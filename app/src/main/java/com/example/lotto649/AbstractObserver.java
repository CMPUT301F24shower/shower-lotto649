package com.example.lotto649;

public abstract class AbstractObserver {
    public transient AbstractObservable observable;
    public void startObserving(AbstractObservable observable) {
        // call me from the constructor or when ready
        if (this.observable != null) {
            throw new RuntimeException("Can't view two models!");
        }
        this.observable = observable;
        observable.addObserver(this);
    }
    public void stopObserving() {
        // call me from delete() or close() etc.
        observable.removeObserver(this);
        this.observable = null;
    }
    public abstract void update(AbstractObservable whoUpdatedMe);
}