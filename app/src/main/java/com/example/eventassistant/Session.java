package com.example.eventassistant;

import java.io.Serializable;

/** Model class representing one row of the "sessions" table (a session that belongs to an event). */
public class Session implements Serializable {

    private long id;            // auto-generated session ID
    private long eventId;       // foreign key to the parent event
    private String name;        // required
    private String time;        // required, stored as HH:mm
    private String speaker;     // optional
    private String room;        // optional
    private String type;        // Workshop / Presentation / Panel Discussion / Networking / Keynote / Others
    private String description; // optional

    public Session() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getEventId() { return eventId; }
    public void setEventId(long eventId) { this.eventId = eventId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getSpeaker() { return speaker; }
    public void setSpeaker(String speaker) { this.speaker = speaker; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
