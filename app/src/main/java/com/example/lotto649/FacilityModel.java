package com.example.lotto649;

public class FacilityModel extends AbstractModel {
    private String facilityName;
    private String address;
    private String deviceId;

    public FacilityModel(String deviceId) {
        this.facilityName = "";
        this.deviceId = deviceId;
        this.address = "";
    }

    public FacilityModel(String deviceId, String facilityName) {
        this.facilityName = facilityName;
        this.deviceId = deviceId;
        this.address = "";
    }

    public FacilityModel(String deviceId, String facilityName, String address) {
        this.facilityName = facilityName;
        this.deviceId = deviceId;
        this.address = address;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
        notifyViews();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyViews();
    }

    public String getDeviceId() {
        return deviceId;
    }

    // No setter for device ID, as it is immutable after being set
}
