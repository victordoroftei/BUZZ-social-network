package com.example.laborator5.socialnetwork.service.dto;

import java.util.List;

/**
 * This class is used for storing information about current logged-in user
 */
public class Page {

    /**
     * This field contains the information of our user
     */
    private UserDTO user;

    /**
     * This field contains the friends list of the current user
     */
    private List<UserDTO> friendsList;

    /**
     * This field contains the messages of our user
     */
    private List<MessageDTO> messages;

    /**
     * This field contains the requests of our user
     */
    private List<RequestDTO> requests;

    /**
     * This field contains the events of the current user
     */
    private List<EventDTO> events;

    /**
     * This is the constructor of our class
     *
     * @param user        the information about our user
     * @param friendsList the friends list
     * @param messages    the messages list
     * @param requests    the requests list
     * @param events      the events list
     */
    public Page(UserDTO user, List<UserDTO> friendsList, List<MessageDTO> messages, List<RequestDTO> requests, List<EventDTO> events) {

        this.user = user;
        this.friendsList = friendsList;
        this.messages = messages;
        this.requests = requests;
        this.events = events;
    }

    /**
     * Get method for user
     *
     * @return the information about our user
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Set method for user
     *
     * @param user - the new information about our user
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    /**
     * Get method for friends list
     *
     * @return the friends list
     */
    public List<UserDTO> getFriendsList() {
        return friendsList;
    }

    /**
     * Set method for friends list
     *
     * @param friendsList - the new friends list
     */
    public void setFriendsList(List<UserDTO> friendsList) {
        this.friendsList = friendsList;
    }

    /**
     * Get method for messages list
     *
     * @return the messages list
     */
    public List<MessageDTO> getMessages() {
        return messages;
    }

    /**
     * Set method for messages
     *
     * @param messages - the new list of messages
     */
    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    /**
     * Get method for requests
     *
     * @return the list of requests
     */
    public List<RequestDTO> getRequests() {
        return requests;
    }

    /**
     * Set method for requests
     *
     * @param requests - the new list of requests
     */
    public void setRequests(List<RequestDTO> requests) {
        this.requests = requests;
    }

    /**
     * Getter method for events.
     *
     * @return the list of requests
     */
    public List<EventDTO> getEvents() {
        return this.events;
    }

    /**
     * Setter method for events.
     *
     * @param events the list of events
     */
    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }
}
