package com.example.ridepal.Models;

import java.util.List;

// Event class
public class Event {
    private String eventName;
    private String eventLocation;
    private String organizerPhoneNumber;
    private String rideType;

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    private String eventDateTime;
    private String userId;
    private String id;
    private String fcmToken;

    private List<EventPickupDetail> pickups;

    // Other existing fields and methods...

    public List<EventPickupDetail> getPickups() {
        return pickups;
    }

    public void setPickups(List<EventPickupDetail> pickups) {
        this.pickups = pickups;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setOrganizerPhoneNumber(String organizerPhoneNumber) {
        this.organizerPhoneNumber = organizerPhoneNumber;
    }

    public Event() {
        // Empty constructor needed for Firestore serialization
    }

    public Event(String eventName, String eventLocation,String organizerPhoneNumber , String rideType,String userId, String fcmToken,String eventDateTime) {
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.organizerPhoneNumber = organizerPhoneNumber;
        this.rideType=rideType;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.eventDateTime=eventDateTime;
    }
    public String getEventName() {
        return eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getOrganizerPhoneNumber() {
        return organizerPhoneNumber;
    }
}
