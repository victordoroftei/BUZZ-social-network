package com.example.laborator5;

import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class represents an object which will be shown in search bar
 */
public class SearchController {

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
     * Current logged-in user
     */
    private UserDTO currentUser;

    /**
     * The resulted user from search
     */
    private UserDTO user;

    /**
     * Connection to the super service
     */
    private SuperService superService;

    /**
     * Method for setting the attributes
     *
     * @param currentUser  - the current logged-in user
     * @param user         - the resulted user
     * @param superService - connection to the super service
     */
    public void setAttributes(UserDTO currentUser, UserDTO user, SuperService superService) {

        this.currentUser = currentUser;
        this.user = user;
        this.superService = superService;
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

    /**
     * Method called when we want to switch to a user profile
     */
    @FXML
    public void onProfilePictureClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("user-profile-view.fxml"));

            Parent root = fxmlLoader.load();

            UserProfileController userProfileController = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            userProfileController.setAttributes(this.currentUser, this.user, this.superService);

            Stage stage1 = GlobalVariable.getMainPageStage();
            stage1.close();

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    /**
     * Method called when the user hovers the profile picture
     */
    @FXML
    public void onMouseEntered() {

        Scene scene = this.nameLabel.getScene();
        scene.setCursor(Cursor.HAND);
    }

    /**
     * Method called when the user exits the profile picture
     */
    @FXML
    public void onMouseExit() {

        Scene scene = this.nameLabel.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

}
