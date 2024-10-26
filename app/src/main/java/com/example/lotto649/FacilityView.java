package com.example.lotto649;

public class FacilityView extends AbstractView {
    // Reference to the fragment associated with this view
    private final FacilityFragment facilityFragment;

    // The model that this view is observing
    private FacilityModel facility;

    /**
     * Constructor for the AccountView class.
     * Initializes the view with the specified facility model and fragment,
     * and starts observing changes in the facility model.
     *
     * @param facility the model to be observed by this view
     * @param facilityFragment the fragment in which user details will be displayed
     */
    public FacilityView(FacilityModel facility, FacilityFragment facilityFragment) {
        this.facilityFragment = facilityFragment;
        this.facility = facility;

        // Start observing the user model
        startObserving(facility);
    }

    /**
     * Retrieves the model currently being observed by this view.
     * This method overrides the `getModel()` method in the `AbstractView` to
     * return the `FacilityModel` specifically.
     *
     * @return the user model being observed by this view
     */
    @Override
    public FacilityModel getModel() {
        return (FacilityModel) super.getModel();
    }

    /**
     * Updates the view with the latest details from the observed model.
     * This method is called whenever the `FacilityModel` changes, and it updates the
     * associated `AccountFragment` with the latest user details.
     *
     * @param whoUpdatedMe the model that triggered the update (expected to be a `FacilityModel`)
     */
    @Override
    public void update(AbstractModel whoUpdatedMe) {
        facility = getModel();
        facilityFragment.showFacilityDetails(facility);
    }
}
