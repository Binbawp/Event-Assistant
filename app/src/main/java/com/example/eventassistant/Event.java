package com.example.eventassistant;


import java.io.Serializable;

/**
 * Model class representing one event row in the "events" table.
 * Serializable so a whole Event can later be passed between activities via an Intent.
 */
public class Event implements Serializable {

    private long id;                 // local auto-generated primary key
    private String cloudId;          // UUID used as the key in the cloud database (feature e)
    private String name;             // required
    private String date;             // required, stored as DD/MM/YYYY
    private String location;
    private String organiser;
    private String description;      // optional
    private String contactEmail;     // optional
    private String contactPhone;     // optional
    private boolean synced;          // false = changed locally and not yet uploaded
    private int sessionCount;        // calculated by the query, not stored in the table

    public Event() { }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCloudId() { return cloudId; }
    public void setCloudId(String cloudId) { this.cloudId = cloudId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOrganiser() { return organiser; }
    public void setOrganiser(String organiser) { this.organiser = organiser; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }

    public int getSessionCount() { return sessionCount; }
    public void setSessionCount(int sessionCount) { this.sessionCount = sessionCount; }
}
