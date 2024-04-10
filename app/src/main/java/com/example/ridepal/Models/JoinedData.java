package com.example.ridepal.Models;

public class JoinedData {
    private String documentId;
    private String pickupDocumentId;

    public String getPickupDocumentId() {
        return pickupDocumentId;
    }

    public void setPickupDocumentId(String pickupDocumentId) {
        this.pickupDocumentId = pickupDocumentId;
    }

    private String type; // "event" or "ride"
    private String name;
    private String locationOrSource;
    private String phoneNumberOrDestination;
    private String bookedSeats;

    public String getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(String bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

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
