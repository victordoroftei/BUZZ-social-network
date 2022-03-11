package com.example.laborator5.socialnetwork.service.dto;

import java.util.List;

/**
 * DTO Class for a conversation.
 */
public class ConversationDTO {

    /**
     * The string that contains data about who is a part of a conversation.
     */
    private String fromString;

    /**
     * The list of participant users.
     */
    private List<UserDTO> participants;

    /**
     * The string that contains most recent message of the conversation.
     */
    private MessageDTO latestMessage;

    /**
     * Constructor for the ConversationDTO class.
     */
    public ConversationDTO(String fromString, List<UserDTO> participants, MessageDTO latestMessage) {

        this.fromString = fromString;
        this.participants = participants;
        this.latestMessage = latestMessage;
    }

    /**
     * Getter method for the fromString attribute.
     *
     * @return the string that contains data about who is a part of a conversation
     */
    public String getFromString() {

        return this.fromString;
    }

    /**
     * Getter method for the participants attribute.
     *
     * @return the list of participant users
     */
    public List<UserDTO> getParticipants() {

        return this.participants;
    }

    /**
     * Getter method for the latestMessage attribute.
     *
     * @return the string that contains the most recent message
     */
    public MessageDTO getLatestMessage() {

        return this.latestMessage;
    }

    /**
     * Overridden equals method for a ConversationDTO object
     *
     * @param o the other object
     * @return true if the objects are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof ConversationDTO))
            return false;

        ConversationDTO that = (ConversationDTO) o;

        if (that.participants.size() != this.participants.size())
            return false;

        for (UserDTO u : this.participants)
            if (!that.participants.contains(u))
                return false;

        return true;
    }

    /**
     * Method for calculating the hashCode of a ConversationDTO object.
     *
     * @return an integer which represents the hash code
     */
    @Override
    public int hashCode() {

        return 1234;
    }

    /**
     * Overridden toString method
     *
     * @return the string associated to the object
     */
    @Override
    public String toString() {

        return this.fromString + '\n' + this.latestMessage.getMessage();
    }

    /**
     * Setter method for the latestMessage attribute.
     *
     * @param latestMessage the new latest message
     */
    public void setLatestMessage(MessageDTO latestMessage) {

        this.latestMessage = latestMessage;
    }

    /**
     * Setter method for the fromString attribute.
     *
     * @param fromString the new fromString
     */
    public void setFromString(String fromString) {

        this.fromString = fromString;
    }
}
