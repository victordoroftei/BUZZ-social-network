package com.example.laborator5.socialnetwork.service.dto;

/**
 * This class contains the data that a user enters when he wants to register into our application
 */
public class UserRegisterDTO {

    /**
     * The data about the user
     */
    private UserDTO user;

    /**
     * The password of the current account - encrypted already
     */
    private String password;

    /**
     * The constructor of our class
     *
     * @param userDTO  - the data about the user
     * @param password - the password of the account
     */
    public UserRegisterDTO(UserDTO userDTO, String password) {

        this.user = userDTO;
        this.password = password;
    }

    /**
     * Get method for getting all the details about a user
     *
     * @return the data about the user
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Get method for getting the password of the account
     *
     * @return the password of the account
     */
    public String getPassword() {
        return password;
    }
}
