package com.example.laborator5;

import com.example.laborator5.socialnetwork.service.dto.PostDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

/**
 * Controller for the post displayed on a user's profile.
 */
public class ProfilePostController {

    /**
     * The owner of the profile.
     */
    private UserDTO user;

    /**
     * The post displayed on the user's profile.
     */
    private PostDTO post;

    /**
     * The label for the name of the user that created the post.
     */
    @FXML
    private Label nameLabel;

    /**
     * The date when the post was created.
     */
    @FXML
    private Label dateLabel;

    /**
     * The content of the post.
     */
    @FXML
    private Label contentLabel;

    /**
     * The profile picture of the user that created the post.
     */
    @FXML
    private Button profilePicture;

    /**
     * Method used for setting the attributes of the controller class.
     *
     * @param user the user that created the post
     * @param post the post that the user created
     */
    public void setAttributes(UserDTO user, PostDTO post) {

        this.user = user;
        this.post = post;

        this.setPicture(52);

        this.contentLabel.setWrapText(true);
        this.contentLabel.setMaxWidth(320);

        this.nameLabel.setText(user.toString());
        this.dateLabel.setText(post.getPostedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.contentLabel.setText(post.getContent());
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
     * Getter method for the user associated to the object.
     *
     * @return the user associated to the object
     */
    public UserDTO getUser() {

        return this.user;
    }

    /**
     * Getter method for the post associated to the object.
     *
     * @return the post associated to the object
     */
    public PostDTO getPost() {

        return this.post;
    }

}
