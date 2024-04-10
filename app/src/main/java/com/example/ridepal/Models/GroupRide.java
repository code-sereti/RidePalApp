package com.example.ridepal.Models;

import java.util.List;

public class GroupRide {
    private Event event;
    private EventPickupDetail eventPickupDetail;
    private List<Users> usersJoined;

    public GroupRide() {
        // Required empty constructor for Firestore
    }

    public GroupRide(Event event, EventPickupDetail eventPickupDetail, List<Users> usersJoined) {
        this.event = event;
        this.eventPickupDetail = eventPickupDetail;
        this.usersJoined = usersJoined;
    }

    public Event getEvent() {
        return event;
    }

    public EventPickupDetail getPickupDetail() {
        return eventPickupDetail;
    }

    public List<Users> getUsersJoined() {
        return usersJoined;
    }
}
