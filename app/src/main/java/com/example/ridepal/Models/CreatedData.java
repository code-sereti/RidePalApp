package com.example.ridepal.Models;

public class CreatedData {
    private String documentId;
    private String type; // "event" or "ride"
    private String name;
    private String locationOrSource;
    private String phoneNumberOrDestination;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationOrSource() {
        return locationOrSource;
    }

    public void setLocationOrSource(String locationOrSource) {
        this.locationOrSource = locationOrSource;
    }

    public String getPhoneNumberOrDestination() {
        return phoneNumberOrDestination;
    }

    public void setPhoneNumberOrDestination(String phoneNumberOrDestination) {
        this.phoneNumberOrDestination = phoneNumberOrDestination;
    }
}
