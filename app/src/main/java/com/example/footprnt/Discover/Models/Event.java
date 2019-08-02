package com.example.footprnt.Discover.Models;

import org.json.JSONObject;

/**
 * Event Model for Discover Fragment
 *
 * @author Jocelyn Shen
 */

public class Event {
    private String name;
    private String description;
    private String imageUrl;
    private JSONObject location;
    private String eventId;
    private String timeStart;
    private String timeEnd;
    private String eventUrl;

    public Event(String name, String description, String imageUrl, JSONObject location, String eventId, String timeStart, String timeEnd, String eventUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.eventId = eventId;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.eventUrl = eventUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public JSONObject getLocation() {
        return location;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getEventUrl() {
        return eventUrl;
    }






}
