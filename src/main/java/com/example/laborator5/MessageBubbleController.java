package com.example.laborator5;

import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Message Bubbles.
 */
public class MessageBubbleController {

    /**
     * The label containing the name of the sender.
     */
    @FXML
    private Label nameLabel;

    /**
     * The label containing the content of the message.
     */
    @FXML
    private Label messageLabel;

    /**
     * The label containing the date of the message.
     */
    @FXML
    private Label dateLabel;

    /**
     * The profile picture of the sender.
     */
    @FXML
    private Button profilePicture;

    /**
     * Method for setting the class attributes.
     *
     * @param user the sender
     * @param message the content of the message
     */
    public void setAttributes(UserDTO user, String message, LocalDateTime date) {

        this.nameLabel.setWrapText(true);
        this.messageLabel.setWrapText(true);

        this.nameLabel.setMaxWidth(400);
        this.messageLabel.setMaxWidth(400);

        this.nameLabel.setText(user.toString());
        this.messageLabel.setText(message);

        this.dateLabel.setText(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        this.setPicture(35, user);
    }

    /**
     * The role of this method is to set the profile picture of a search resulted user
     *
     * @param size - the size of the picture
     */
    private void setPicture(int size, UserDTO user) {

        String sizeString = String.valueOf(size);

        try {

            String image = getClass().getResource("css/img/profile_pictures/pf_" + user.getUserName() + ".png").toExternalForm();

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
