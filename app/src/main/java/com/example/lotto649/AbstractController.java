package com.example.lotto649;

public abstract class AbstractController {
    private final AbstractModel model;
    public AbstractController(AbstractModel model) {
        this.model = model;
    }
    public AbstractModel getModel() {
        return model;
    }
}
