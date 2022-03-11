package com.example.laborator5.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Cotnains the details of a User
 */
public class User extends Entity<Long> {
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
     * The password of the user.
     */
    private String password;

    /**
     * The date when the user last logged in.
     */
    private LocalDateTime lastLogin;

    /**
     * Constructor for user
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param userName  the username of the user
     * @param password  the password of the user
     */
    public User(String firstName, String lastName, String userName, String password) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
    }

    /**
     * Constructor for user
     *
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param userName  the username of the user
     * @param password  the password of the user
     */
    public User(String firstName, String lastName, String userName, String password, LocalDateTime lastLogin) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.lastLogin = lastLogin;
    }

    /**
     * Getter method for the first name
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter method for the first name
     *
     * @param firstName - the new first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter method for the last name
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter method for the last name
     *
     * @param lastName - the new first name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter method for username
     *
     * @return the username of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Setter methode for username
     *
     * @param newUserName - String, the new username
     */
    public void setUserName(String newUserName) {
        this.userName = newUserName;
    }

    /**
     * Get method for getting the password of the user
     *
     * @return the password of the user
     */
    public String getPassword() {

        return password;
    }

    /**
     * Set method for the user password
     *
     * @param password - the new password of the user
     */
    public void setPassword(String password) {

        this.password = password;
    }


    /**
     * Getter method for the date of the last log-in.
     *
     * @return the date of the last log-in
     */
    public LocalDateTime getLastLogin() {

        return this.lastLogin;
    }

    /**
     * Set method for the date of the last log-in
     *
     * @param lastLogin - the new date of last log-in
     */
    public void setLastLogin(LocalDateTime lastLogin) {

        this.lastLogin = lastLogin;
    }

    /**
     * toString method override
     *
     * @return user as a string
     */
    @Override
    public String toString() {
        return "FirstName = " + firstName +
                ", LastName = " + lastName +
                ", UserName = " + userName;
    }

    /**
     * equals method override
     *
     * @param o - Object
     * @return boolean value
     */
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof User))
            return false;

        User that = (User) o;
        return getUserName().equals(that.getUserName());
    }

    /**
     * hashCode method override
     *
     * @return hash value for user
     */
    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getUserName());
    }
}