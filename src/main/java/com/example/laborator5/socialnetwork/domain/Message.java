package com.example.laborator5.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents a message which can be sent between 2 users
 */
public class Message extends Entity<Long> {

    /**
     * The id of the user who sent the message
     */
    private Long from;

    /**
     * The IDs list of the receivers of the message
     */
    private List<Long> to;

    /**
     * The content of the message
     */
    private String message;

    /**
     * The date when the message was sent
     */
    private LocalDateTime date;

    /**
     * The message which we answer to
     */
    private Long originalMessage;

    /**
     * The first constructor of message, without date
     *
     * @param id      - The id of the message
     * @param from    - The id of the sender
     * @param to      - The list of id's of receivers
     * @param message - The content of the message
     */
    public Message(Long id, Long from, List<Long> to, String message) {

        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.originalMessage = null;
    }

    /**
     * The first constructor of message, with date
     *
     * @param id      - The id of the message
     * @param from    - The id of the sender
     * @param to      - The list of id's of receivers
     * @param message - The content of the message
     * @param date    - The date when the message was sent
     */
    public Message(Long id, Long from, List<Long> to, String message, LocalDateTime date) {

        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.originalMessage = null;
    }

    /**
     * Get method for the sender's id
     *
     * @return the id of the sender
     */
    public Long getFrom() {
        return from;
    }

    /**
     * Set method for sender's id
     *
     * @param from - The new sender's id
     */
    public void setFrom(Long from) {
        this.from = from;
    }

    /**
     * Get method for receivers list
     *
     * @return the list of receivers
     */
    public List<Long> getTo() {
        return to;
    }

    /**
     * Set method for receivers list
     *
     * @param to - the new list of receivers
     */
    public void setTo(List<Long> to) {
        this.to = to;
    }

    /**
     * Get method for the content of the message
     *
     * @return the message content
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set method for the content of the message
     *
     * @param message - the new message content
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get method for the date
     *
     * @return the date of the message
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Set method for the date
     *
     * @param date - the new date of the message
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Get method for reply
     *
     * @return the reply's id
     */
    public Long getOriginalMessage() {
        return originalMessage;
    }

    /**
     * Set method for reply's id
     *
     * @param originalMessage - the new id of the message we reply to
     */
    public void setOriginalMessage(Long originalMessage) {
        this.originalMessage = originalMessage;
    }
}
