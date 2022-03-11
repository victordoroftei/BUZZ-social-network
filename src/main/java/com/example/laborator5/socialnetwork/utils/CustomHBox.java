package com.example.laborator5.socialnetwork.utils;

import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.scene.layout.HBox;

/**
 * Class for a HBox with a custom toString() method.
 */
public class CustomHBox extends HBox {

    /**
     * The user associated to this HBox.
     */
    private UserDTO userDTO;

    /**
     * Constructor of the CustomHBox class.
     *
     * @param hbox the original HBox
     * @param userDTO the user associated to the custom HBox
     */
    public CustomHBox(HBox hbox, UserDTO userDTO) {

        super(hbox);
        this.userDTO = userDTO;
    }

    /**
     * Getter method for the user associated to the custom HBox.
     *
     * @return a UserDTO object
     */
    public UserDTO getUserDTO() {
        return userDTO;
    }

    /**
     * Overridden toString method.
     *
     * @return a string associated to the CustomHBox object
     */
    @Override
    public String toString() {

        return this.userDTO.toString();
    }
}
