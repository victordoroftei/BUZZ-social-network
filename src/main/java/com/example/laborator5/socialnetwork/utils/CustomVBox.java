package com.example.laborator5.socialnetwork.utils;

import com.example.laborator5.socialnetwork.service.dto.PostDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.scene.layout.VBox;

/**
 * Custom VBox class.
 */
public class CustomVBox extends VBox {

    /**
     * The user associated to the custom VBox.
     */
    private UserDTO user;

    /**
     * The post associated to the custom VBox.
     */
    private PostDTO post;

    /**
     * Constructor for the Custom VBox class.
     *
     * @param vbox the original VBox
     * @param user the user of the custom VBox
     * @param post the post of the custom VBox
     */
    public CustomVBox(VBox vbox, UserDTO user, PostDTO post) {

        super(vbox);
        this.user = user;
        this.post = post;

    }

    /**
     * Getter method for the user associated to the VBox.
     *
     * @return the user associated to the VBox
     */
    public UserDTO getUser() {

        return user;
    }

    /**
     * Getter method for the post associated to the VBox.
     *
     * @return the post associated to the VBox
     */
    public PostDTO getPost() {

        return post;
    }
}
