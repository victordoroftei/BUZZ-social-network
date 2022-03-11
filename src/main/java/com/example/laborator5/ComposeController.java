package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.FriendshipDTO;
import com.example.laborator5.socialnetwork.service.dto.MessageDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.CustomHBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComposeController {

    /**
     * The super service of the controller
     */
    private SuperService superService;

    /**
     * The user that is currently logged in.
     */
    private UserDTO currentUser;

    /**
     * ListView for displaying the user's friends.
     */
    @FXML
    private ListView<CustomHBox> listView;

    /**
     * Button for selecting the recipients of the new message.
     */
    @FXML
    private Button selectButton;

    /**
     * Button for returning to the message menu window.
     */
    @FXML
    private Button backButton;

    /**
     * Text area used for writing the message
     */
    @FXML
    private TextArea messageTextArea;

    /**
     * The root of the window
     */
    @FXML
    private VBox mainBox;

    /**
     * Setter method for the attributes of this class.
     *
     * @param superService the super service of the controller
     * @param currentUser  the user that is currently logged in
     */
    public void setAttributes(SuperService superService, UserDTO currentUser) {

        this.superService = superService;
        this.currentUser = currentUser;

        this.messageTextArea.setWrapText(true);

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        this.loadListData();
    }

    /**
     * Method for loading the data of the user's friends into the list view.
     */
    @FXML
    public void loadListData() {

        this.listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.listView.getItems().clear();

        List<UserDTO> l = this.superService.getAllFriendshipsForAUser(this.currentUser).stream().map(FriendshipDTO::getUser2).collect(Collectors.toList());

        List<CustomHBox> list = new ArrayList<>();

        for (UserDTO user : l) {

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
        this.listView.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Method that is called when clicking the select button.
     */
    @FXML
    public void onSendMessageButtonClick() {

        try {

            String textAreaString = this.messageTextArea.getText();

            if (textAreaString.equals(""))
                throw new ControllerException("You cannot send an empty message!");

            List<CustomHBox> list = this.listView.getSelectionModel().getSelectedItems();

            if (list == null || list.size() == 0)
                throw new ControllerException("You must select at least a user!");

            List<UserDTO> users = list.stream().map(CustomHBox::getUserDTO).collect(Collectors.toList());

            this.superService.addMessage(new MessageDTO(null, this.currentUser, users, textAreaString, LocalDateTime.now(), null));

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("message-view.fxml"));

            Parent root = fxmlLoader.load();

            MessageController mc = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            mc.setAttributes(this.superService, this.currentUser);

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.hide();

        } catch (ControllerException | ServiceException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

    }

    /**
     * Method that is called when clicking the button for returning to the message menu.
     */
    @FXML
    public void onBackButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("message-view.fxml"));

            Parent root = fxmlLoader.load();

            MessageController mc = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            mc.setAttributes(this.superService, this.currentUser);

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.hide();
        } catch (IOException ex) {

            ex.printStackTrace();
        }
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
