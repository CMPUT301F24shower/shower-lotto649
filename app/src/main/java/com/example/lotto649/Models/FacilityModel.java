/**
 * A model of the Facility class.
 */
package com.example.lotto649.Models;

import com.example.lotto649.AbstractClasses.AbstractModel;

/**
 * A model class that stores information about a given Facility.
 * A Facility can be managed by one Organizer, and each Organizer can have one Facility.
 * Holds information about the Facility, and the Organizer that created it.
 */
public class FacilityModel extends AbstractModel {
    private String facilityName;
    private String address;
    private String deviceId;

    /**
     * Constructor to create a new facility with only a deviceId.
     * All other properties will be set to "".
     *
     * @param deviceId the deviceId of the organizer that created the facility
     */
    public FacilityModel(String deviceId) {
        this.facilityName = "";
        this.deviceId = deviceId;
        this.address = "";
    }

    /**
     * Constructor to create a new facility with a deviceId, facility name, and optional address.
     *
     * @param deviceId the deviceId of the organizer that created the facility
     * @param facilityName the name of the facility being created
     * @param address the address of the facility being created
     */
    public FacilityModel(String deviceId, String facilityName, String address) {
        this.facilityName = facilityName;
        this.deviceId = deviceId;
        this.address = address;
    }

    /**
     * Gets the name of the facility.
     *
     * @return facilityName
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * Sets the name of the facility.
     * Notifies the views with this model that the model changed.
     *
     * @param facilityName The new facility name.
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
        notifyViews();
    }

    /**
     * Gets the address of the facility.
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the facility.
     * Notifies the views with this model that the model changed.
     *
     * @param address The new facility name.
     */
    public void setAddress(String address) {
        this.address = address;
        notifyViews();
    }

    /**
     * Gets the deviceId of the organizer related to the facility
     *
     * @return deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    // No setter for device ID, as it is immutable after being set
}
