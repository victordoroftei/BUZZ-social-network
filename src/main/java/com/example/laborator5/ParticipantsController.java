package com.example.laborator5;

import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.CustomHBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller of the participants window
 */
public class ParticipantsController {

    /**
     * The root of the page
     */
    @FXML
    private VBox mainBox;

    /**
     * The list of participants
     */
    @FXML
    private ListView<CustomHBox> participantsListView;

    /**
     * The name of the event
     */
    @FXML
    private Label participantLabel;

    /**
     * Method used for setting the attributes of this class
     * @param eventName - the name of the event
     * @param users - the list of users
     */
    public void setAttributes(String eventName, List<UserDTO> users){

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        this.participantLabel.setText("Participants of " + eventName);

        this.participantLabel.setMaxWidth(180);

        this.participantLabel.setWrapText(true);

        this.participantsListView.setMouseTransparent(true);
        this.participantsListView.setFocusTraversable(false);

        List<CustomHBox> list = new ArrayList<>();

        for (UserDTO user : users) {

            HBox box;

            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("friend-hbox-view.fxml"));

                box = fxmlLoader.load();

                FriendHBoxController controller = fxmlLoader.getController();
                controller.setAttributes(user);

                list.add(new CustomHBox(box, user));

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
        this.participantsListView.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Method called when clicking the back button on the participants window.
     */
    @FXML
    public void onParticipantsBackButtonClick() {

        Stage stage2 = (Stage) this.participantsListView.getScene().getWindow();
        stage2.close();
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
