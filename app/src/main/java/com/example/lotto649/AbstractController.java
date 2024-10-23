/**
 * Code used from the lecture slides
 * https://ualberta-cmput301.github.io/general/slides/020mvc.pdf
 */
package com.example.lotto649;

/**
 * General controller class used to manage
 * updates to the Model. This class will be
 * implemented by all other controllers
 */
public abstract class AbstractController {
    private final AbstractModel model;

    /**
     * Constructor
     * @param model Model class being controlled
     */
    public AbstractController(AbstractModel model) {
        this.model = model;
    }

    /**
     * Get the model for this Controller
     * Will be called by the implementing class whenever
     * the model needs to be updated
     *
     * @return Model
     */
    public AbstractModel getModel() {
        return model;
    }
}
