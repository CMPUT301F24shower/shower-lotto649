/**
 * AbstractModel is part of the Model-View-Controller (MVC) architecture.
 * It serves as the base class for models in the application and manages the interaction
 * with views. The model maintains a set of views and notifies them of any changes
 * in the data or state.
 * This implementation is used from the lecture slides:
 * https://ualberta-cmput301.github.io/general/slides/020mvc.pdf
 */
package com.example.lotto649;

import android.util.ArraySet;
import java.util.Set;

public abstract class AbstractModel {
    private final transient Set<AbstractView> views;

    /**
     * Constructor for the AbstractModel class.
     * Initializes the set of views that will observe this model.
     */
    protected AbstractModel() {
        views = new ArraySet<>();
    }

    /**
     * Adds a view to the set of views that observe the model.
     * After adding, the view is immediately updated with the current state of the model.
     *
     * @param view the view to be added and notified of model updates
     */
    public void addView(AbstractView view) {
        views.add(view);
        view.update(this);
    }

    /**
     * Removes a view from the set of views observing the model.
     *
     * @param view the view to be removed from the observer list
     */
    public void removeView(AbstractView view) {
        views.remove(view);
    }

    /**
     * Notifies all the views that are observing the model.
     * Typically called from setters to inform the views of state changes.
     */
    public void notifyViews() {
        for (AbstractView view : views) {
            view.update(this);
        }
    }
}
