package com.example.lotto649;

import android.util.ArraySet;

import java.util.Set;

public abstract class AbstractModel {
    private final transient Set<AbstractView> views;
    protected AbstractModel() {
        views = new ArraySet<>();
    }
    public void addView(AbstractView view) {
        views.add(view);
        view.update(this);
    }
    public void removeView(AbstractView view) {
        views.remove(view);
    }
    public void notifyViews() {
        // Call this from setters
        for (AbstractView view : views) {
            view.update(this);
        }
    }
}
