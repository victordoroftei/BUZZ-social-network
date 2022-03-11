package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.PostDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;

public class PostEditController {

    /**
     * The super service of the controller.
     */
    private SuperService superService;

    /**
     * The current user.
     */
    private UserDTO currentUser;

    /**
     * The post that may currently be edited.
     */
    private PostDTO currentPost;

    /**
     * The text area for the content of the post.
     */
    @FXML
    private TextArea contentTextArea;

    /**
     * The root of the window
     */
    @FXML
    private VBox mainBox;

    /**
     * The button for submitting changes for a post.
     */
    @FXML
    private Button submitButton;

    /**
     * Method for setting the attributes of the controller.
     *
     * @param superService the super service of the controller
     * @param currentUser  the current user of the controller
     * @param currentPost  the current post of the controller
     */
    public void setAttributes(SuperService superService, UserDTO currentUser, PostDTO currentPost) {

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        this.superService = superService;
        this.currentUser = currentUser;
        this.currentPost = currentPost;

        this.contentTextArea.setWrapText(true);

        if (this.currentPost != null) {

            this.contentTextArea.setText(this.currentPost.getContent());
            this.submitButton.setText("Save Changes");
        } else
            this.submitButton.setText("Post");

    }

    /**
     * Method called when clicking the submit button.
     */
    @FXML
    public void onSubmitButtonClick() {

        try {

            if (this.contentTextArea.getText().equals(""))
                throw new ControllerException("The content of the post cannot be empty!");

            if (this.currentPost == null) {

                this.superService.addPost(new PostDTO(null, this.currentUser, this.contentTextArea.getText(), LocalDateTime.now()));

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Post Added Successfully!");
                alert.showAndWait();

                Stage stage = (Stage) this.contentTextArea.getScene().getWindow();
                stage.close();
            }

            else {

                this.superService.updatePost(new PostDTO(this.currentPost.getId(), this.currentUser, this.contentTextArea.getText(), LocalDateTime.now()));

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Post Updated Successfully!");
                alert.showAndWait();
            }

        } catch (ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method called when clicking the back button.
     */
    @FXML
    public void onBackButtonClick() {

        Stage stage = (Stage) this.contentTextArea.getScene().getWindow();
        stage.close();
    }

    /**
     * This method closes our application and is called when we press exit button
     */
    @FXML
    public void onExitButtonClick() {

        Stage stage = (Stage) this.mainBox.getScene().getWindow();
        stage.close();
    }

    /**
     * Method called when the yellow button is pressed
     */
    @FXML
    public void onMinimizeButtonClick() {

        Stage stage = (Stage) this.mainBox.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Method called when the user hovers the profile picture
     */
    @FXML
    public void onMouseEntered() {

        Scene scene = this.mainBox.getScene();
        scene.setCursor(Cursor.HAND);
    }

    /**
     * Method called when the user exits the profile picture
     */
    @FXML
    public void onMouseExit() {

        Scene scene = this.mainBox.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

}
