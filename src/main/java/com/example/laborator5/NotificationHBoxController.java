package com.example.laborator5;

import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.EventDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

/**
 * Controller for the HBox GUI.
 */
public class NotificationHBoxController {

    /**
     * The button of the HBox.
     */
    @FXML
    private Button button;

    /**
     * The label of the HBox.
     */
    @FXML
    private Label label;

    /**
     * The super service of the controller.
     */
    private SuperService superService;

    /**
     * The user that is currently logged in.
     */
    private UserDTO currentUser;

    /**
     * The current event.
     */
    private EventDTO currentEvent;

    /**
     * Setter method for the attributes of the NotificationHBoxController.
     *
     * @param superService the super service of the controller
     * @param currentUser  the user that is currently logged in
     * @param event        the current event
     */
    public void setAttributes(SuperService superService, UserDTO currentUser, EventDTO event) {

        this.superService = superService;
        this.currentUser = currentUser;
        this.currentEvent = event;

        this.label.setWrapText(true);
        this.label.setMaxWidth(273);

        String labelText = event.getName() + " | " + event.getDescription() + " | " + event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.label.setText(labelText);
    }

    /**
     * Method called when clicking the mute notifications button.
     */
    @FXML
    public void onButtonClick() {

        this.superService.setNotificationsForUser(this.currentEvent, this.currentUser, false);
    }
}
