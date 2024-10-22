package com.example.lotto649;

public abstract class AbstractView {
    private AbstractModel model;
    public void startObserving(AbstractModel model) {
        // called during the constructor ...
        // ... or when its ready to start getting updates
        if (this.model != null) {
            throw new RuntimeException("Can't view two models!");
        }
        this.model = model;
        model.addView(this);
    }
    public void closeView() {
        // when the view goes away
        model.removeView(this);
        this.model = null;
    }
    public abstract void update(AbstractModel whoUpdatedMe);
    public AbstractModel getModel() {
        return model;
    }
}
