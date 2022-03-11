package com.example.laborator5.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class contains all the details about an event
 */
public class Event extends Entity<Long> {

    /**
     * The name of the event
     */
    private String name;

    /**
     * The id of the organizer
     */
    private Long organizer;

    /**
     * The description of the event
     */
    private String description;

    /**
     * The date of the event
     */
    private LocalDateTime date;

    /**
     * The participants of the event
     */
    private List<Long> participants;

    /**
     * This is the constructor of our class
     *
     * @param name         - the name of the event
     * @param organizer    - the id of the organizer
     * @param description  - the description of the event
     * @param date         - the date of the event
     * @param participants - the participants of the event
     */
    public Event(Long id, String name, Long organizer, String description, LocalDateTime date, List<Long> participants) {

        this.setId(id);
        this.name = name;
        this.organizer = organizer;
        this.description = description;
        this.date = date;
        this.participants = participants;
    }

    /**
     * Get method for the name of the event
     *
     * @return the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Set method for the name of the event
     *
     * @param name - the new name of the event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get method for the organizer of the event
     *
     * @return the id of the organizer
     */
    public Long getOrganizer() {
        return organizer;
    }

    /**
     * Set method for the organizer of the event
     *
     * @param organizer - the new organizer of the event
     */
    public void setOrganizer(Long organizer) {
        this.organizer = organizer;
    }

    /**
     * Get method for the event description
     *
     * @return the description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set method for the description
     *
     * @param description - the new description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get method for the date of the event
     *
     * @return the date of the event
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Set method for the date of the event
     *
     * @param date - the new date of the event
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Get method for the list of participants
     *
     * @return the list of participants
     */
    public List<Long> getParticipants() {
        return participants;
    }

    /**
     * Set method for the list of the participants
     *
     * @param participants - the new list of participants
     */
    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }
}
