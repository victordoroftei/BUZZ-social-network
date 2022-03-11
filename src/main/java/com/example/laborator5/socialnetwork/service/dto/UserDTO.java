package com.example.laborator5.socialnetwork.service.dto;

import java.time.LocalDateTime;

/**
 * The class which converts an ID to a User
 */
public class UserDTO {

    /**
     * The id of the user.
     */
    private Long id;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The username of the user.
     */
    private String userName;

    /**
     * The date of the last log-in.
     */
    private LocalDateTime lastLogin;

    /**
     * The constructor of the class
     *
     * @param id - Long, the id of the user
     * @param fn - String, the first name of the user
     * @param ln - String, the last name of the user
     * @param un - String, the username of the user
     */
    public UserDTO(Long id, String fn, String ln, String un) {

        this.id = id;
        this.firstName = fn;
        this.lastName = ln;
        this.userName = un;
    }

    /**
     * The constructor of the class
     *
     * @param id the id of the user
     * @param fn the first name of the user
     * @param ln the last name of the user
     * @param un the username of the user
     * @param ll the date of the last log-in
     */
    public UserDTO(Long id, String fn, String ln, String un, LocalDateTime ll) {

        this.id = id;
        this.firstName = fn;
        this.lastName = ln;
        this.userName = un;
        this.lastLogin = ll;
    }

    /**
     * Getter method for id
     *
     * @return the id
     */
    public Long getId() {

        return this.id;
    }

    /**
     * Get the first name of a DTO
     *
     * @return the first name
     */
    public String getFirstName() {

        return this.firstName;
    }

    /**
     * Get the last name of a DTO
     *
     * @return the last name
     */
    public String getLastName() {

        return this.lastName;
    }

    /**
     * Get the userName of a DTO
     *
     * @return the username
     */
    public String getUserName() {

        return this.userName;
    }

    /**
     * Getter method for the date of the last log-in.
     *
     * @return the date of the last log-in
     */
    public LocalDateTime getLastLogin() {

        return lastLogin;
    }

    /**
     * Setter method for the date of the last log-in.
     *
     * @param lastLogin the new date of the last log-in
     */
    public void setLastLogin(LocalDateTime lastLogin) {

        this.lastLogin = lastLogin;
    }

    /**
     * Overridden equals method for a MessageDTO object
     *
     * @param o the other object
     * @return true if the objects are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof UserDTO))
            return false;

        UserDTO that = (UserDTO) o;
        return that.id.equals(this.id);

    }

    /**
     * toString method override
     *
     * @return userDTO as a string
     */
    @Override
    public String toString() {

        return this.firstName + " " + this.lastName + " (" + this.userName + ")";
    }
}
