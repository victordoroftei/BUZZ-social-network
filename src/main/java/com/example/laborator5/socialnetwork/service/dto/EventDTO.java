package com.example.laborator5.socialnetwork.service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class is used for printing an event in our application
 */
public class EventDTO {

    /**
     * The id of our event
     */
    private Long id;

    /**
     * The name of our event;
     */
    private String name;

    /**
     * The details of the organizer
     */
    private UserDTO organizer;

    /**
     * The description of the event
     */
    private String description;

    /**
     * The date of the event
     */
    private LocalDateTime date;

    /**
     * The list of participants
     */
    private List<UserDTO> participants;

    /**
     * The constructor of the class
     *
     * @param id           - the id of the event
     * @param name         - the name of the event
     * @param organizer    - the organizer of the event
     * @param description  - the description of the event
     * @param date         - the date of the event
     * @param participants - the list of the participants
     */
    public EventDTO(Long id, String name, UserDTO organizer, String description, LocalDateTime date, List<UserDTO> participants) {

        this.id = id;
        this.name = name;
        this.organizer = organizer;
        this.description = description;
        this.date = date;
        this.participants = participants;
    }

    /**
     * Get method for the id of the event
     *
     * @return the id of the event
     */
    public Long getId() {

        return id;
    }

    /**
     * Set method for the id of the event
     *
     * @param id - the new id of the event
     */
    public void setId(Long id) {

        this.id = id;
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
     * @return the organizer
     */
    public UserDTO getOrganizer() {

        return organizer;
    }

    /**
     * Set method for the organizer of the event
     *
     * @param organizer - the new organizer of the event
     */
    public void setOrganizer(UserDTO organizer) {

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
    public List<UserDTO> getParticipants() {

        return participants;
    }

    /**
     * Set method for the list of the participants
     *
     * @param participants - the new list of participants
     */
    public void setParticipants(List<UserDTO> participants) {

        this.participants = participants;
    }

    /**
     * Overridden toString method.
     *
     * @return a string containing the object's data
     */
    @Override
    public String toString() {

        return this.name + "\n" + this.description + "\nTakes place on: " + this.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\nOrganized by: " + this.getOrganizer().toString();
    }
}
