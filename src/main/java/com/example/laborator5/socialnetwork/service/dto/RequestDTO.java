package com.example.laborator5.socialnetwork.service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestDTO {

    /**
     * The first user of the RequestDTO.
     */
    private UserDTO user1;

    /**
     * The second user of the RequestDTO.
     */
    private UserDTO user2;

    /**
     * The date of the RequestDTO.
     */
    private LocalDateTime date;

    /**
     * The status of the RequestDTO.
     */
    private String status;

    /**
     * Constructor for the RequestDTO class.
     *
     * @param user1  the first user of the RequestDTO
     * @param user2  the second user of the RequestDTO
     * @param date   the date of the RequestDTO
     * @param status the status of the RequestDTO
     */
    public RequestDTO(UserDTO user1, UserDTO user2, LocalDateTime date, String status) {

        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
        this.status = status;

    }

    /**
     * @return the first user
     */
    public UserDTO getUser1() {

        return user1;
    }

    /**
     * @return the second user
     */
    public UserDTO getUser2() {

        return user2;
    }

    /**
     * @return the date
     */
    public LocalDateTime getDate() {

        return date;
    }

    /**
     * @return the status
     */
    public String getStatus() {

        return status;
    }

    @Override
    public String toString() {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return this.user1.getUserName() + " requested to be friends with " + this.user2.getUserName() + " on " + this.getDate().format(formatter);
    }
}
