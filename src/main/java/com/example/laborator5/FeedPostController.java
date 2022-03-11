package com.example.laborator5;

import com.example.laborator5.socialnetwork.domain.Tuple;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.EventDTO;
import com.example.laborator5.socialnetwork.service.dto.PostDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class FeedPostController {

    /**
     * The name of the user
     */
    @FXML
    private Label nameLabel;

    /**
     * The content of the post
     */
    @FXML
    private Label contentLabel;

    /**
     * The date of the post
     */
    @FXML
    private Label dateLabel;

    /**
     * The profile picture of the user and the button for going to his profile
     */
    @FXML
    private Button profilePicture;

    /**
     * The post.
     */
    private PostDTO post;

    /**
     * The tuple containing a participant and an event.
     */
    private Tuple<UserDTO, EventDTO> tuple;

    /**
     * Connection to the super service
     */
    private SuperService superService;

    /**
     * The user that is currently logged in
     */
    private UserDTO currentUser;

    /**
     * The user that the feed belongs to
     */
    private UserDTO feedUser;

    /**
     * Method for setting a picture on a button.
     *
     * @param size the size of the picture
     */
    private void setPicture(int size) {

        String sizeString = String.valueOf(size);

        try {

            String image = getClass().getResource("css/img/profile_pictures/pf_" + this.feedUser.getUserName() + ".png").toExternalForm();

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
     * Method for setting the attributes
     *
     * @param superService - connection to the super service
     * @param post the post
     */
    public void setAttributes(SuperService superService, UserDTO currentUser, PostDTO post, Tuple<UserDTO, EventDTO> tuple) {

        this.currentUser = currentUser;
        this.post = post;
        this.tuple = tuple;
        this.superService = superService;

        this.contentLabel.setWrapText(true);

        if (tuple == null) {

            this.nameLabel.setText(this.post.getUser().toString());
            this.dateLabel.setText(this.post.getPostedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            this.contentLabel.setText(this.post.getContent());
            this.feedUser = this.post.getUser();
        }

        else {

            this.nameLabel.setText(this.tuple.getLeft().toString());
            this.dateLabel.setText("attends");
            this.contentLabel.setText(this.tuple.getRight().toString());
            this.feedUser = this.tuple.getLeft();
        }

        this.setPicture(52);
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

            userProfileController.setAttributes(this.currentUser, this.feedUser, this.superService);

            Stage stage1 = GlobalVariable.getMainPageStage();
            stage1.close();

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

}
