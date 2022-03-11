package com.example.laborator5;

import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * This class represents an object which will be shown in search bar
 */
public class FriendHBoxController {

    /**
     * The name of the user
     */
    @FXML
    private Label nameLabel;

    /**
     * The profile picture of the user and the button for going to his profile
     */
    @FXML
    private Button profilePicture;

    /**
     * The resulted user from search
     */
    private UserDTO user;

    /**
     * Method for setting the attributes
     *
     * @param user         - the resulted user
     */
    public void setAttributes(UserDTO user) {

        this.user = user;
        this.nameLabel.setText(user.toString());

        this.setPicture(28);
    }

    /**
     * The role of this method is to set the profile picture of a search resulted user
     *
     * @param size - the size of the picture
     */
    private void setPicture(int size) {

        String sizeString = String.valueOf(size);

        try {

            String image = getClass().getResource("css/img/profile_pictures/pf_" + this.user.getUserName() + ".png").toExternalForm();

            this.profilePicture.setStyle("-fx-background-image: url('" + image + "');" +
                    "-fx-background-size: " + sizeString + "px " + sizeString + "px; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: stretch;" +
                    "-fx-background-color: transparent");
        } catch (NullPointerException ignored) {

            String image = getClass().getResource("css/img/profile_pictures/default.png").toExternalForm();

            this.profilePicture.setStyle("-fx-background-image: url('" + image + "');" +
                    "-fx-background-size: " + sizeString + "px " + sizeString + "px; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: stretch;" +
                    "-fx-background-color: transparent");
        }
    }
}
