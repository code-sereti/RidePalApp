package com.example.ridepal.Models;

public class EventPickupDetail {
    private String pickupLocation;
    private String pickupTime;
    private String pickupId;
    private int availableSeats;

    public EventPickupDetail() {
        // Empty constructor needed for Firestore serialization
    }

    public EventPickupDetail(String pickupLocation, int availableSeats, String pickupTime) {
        this.pickupLocation = pickupLocation;
        this.pickupTime = pickupTime;
        this.availableSeats = availableSeats;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
    public String getPickupId() {
        return pickupId;
    }

    public void setPickupId(String pickupId) {
        this.pickupId = pickupId;
    }
}
