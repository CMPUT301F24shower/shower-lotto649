/**
 * AbstractView is part of the Model-View-Controller (MVC) architecture.
 * It represents the base class for views in the application and defines methods for
 * observing and interacting with a model. Views receive updates from models they observe
 * and can stop observing when necessary.
 * This implementation is used from the lecture slides:
 * https://ualberta-cmput301.github.io/general/slides/020mvc.pdf
 */
package com.example.lotto649.AbstractClasses;

public abstract class AbstractView {
    private AbstractModel model;

    /**
     * Starts observing a specified model. Once the view starts observing a model,
     * it receives updates from the model whenever the model state changes.
     * This method should be called during the view's initialization or
     * when it's ready to receive updates from the model.
     *
     * @param model the model to be observed by this view
     * @throws RuntimeException if the view is already observing a different model
     */
    public void startObserving(AbstractModel model) {
        if (this.model != null) {
            throw new RuntimeException("Can't view two models!");
        }
        this.model = model;
        model.addView(this);
    }

    /**
     * Stops the view from observing the model. This method should be called
     * when the view is no longer needed or is being destroyed, to properly
     * clean up the model-view relationship.
     */
    public void closeView() {
        // Called when the view goes away
        model.removeView(this);
        this.model = null;
    }

    /**
     * An abstract method that must be implemented by concrete views to define
     * how they should be updated when the observed model changes.
     *
     * @param whoUpdatedMe the model that triggered the update
     */
    public abstract void update(AbstractModel whoUpdatedMe);

    /**
     * Retrieves the model currently being observed by the view.
     *
     * @return the model this view is observing, or null if no model is being observed
     */
    public AbstractModel getModel() {
        return model;
    }
}
