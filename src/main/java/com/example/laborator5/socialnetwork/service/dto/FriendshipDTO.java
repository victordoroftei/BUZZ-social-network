package com.example.laborator5.socialnetwork.service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class which transforms the IDs of a friendship in Users
 */
public class FriendshipDTO {

    /**
     * The first user of the friendship.
     */
    private UserDTO user1;

    /**
     * The second user of the friendship.
     */
    private UserDTO user2;

    /**
     * The date when the friendship was created.
     */
    private LocalDateTime date;

    /**
     * Constructor of the class
     *
     * @param user1 - UserDTO, first user
     * @param user2 - UserDTO, second user
     * @param date  - LocalDateTime, the date of the friendship
     */
    public FriendshipDTO(UserDTO user1, UserDTO user2, LocalDateTime date) {

        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
    }

    /**
     * Getter method for the first user
     *
     * @return first user
     */
    public UserDTO getUser1() {

        return user1;
    }

    /**
     * Getter method for the second user
     *
     * @return second user
     */
    public UserDTO getUser2() {

        return user2;
    }

    /**
     * Getter method for the date
     *
     * @return the date
     */
    public LocalDateTime getDate() {

        return date;
    }

    /**
     * toString method override
     *
     * @return friendship as a string
     */
    @Override
    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return user1.toString() + " is friend with " + user2.toString() + " since " + date.format(formatter);
    }
}
