package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.ConversationDTO;
import com.example.laborator5.socialnetwork.service.dto.MessageDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller class for the inbox menu.
 */
public class MessageController implements Observer {

    /**
     * The super service of the controller
     */
    private SuperService superService;

    /**
     * The user that is currently logged in
     */
    private UserDTO currentUser;

    /**
     * Button for composing a new message.
     */
    @FXML
    private Button composeButton;

    /**
     * Button for returning to the previous menu.
     */
    @FXML
    private Button backButton;

    /**
     * ListView for displaying conversations.
     */
    @FXML
    private ListView<ConversationDTO> listView;

    /**
     * The main box of the window.
     */
    @FXML
    private HBox mainBox;

    /**
     * Setter method for the attributes of this class.
     *
     * @param superService the super service of the controller
     * @param currentUser  the user that is currently logged in
     */
    public void setAttributes(SuperService superService, UserDTO currentUser) {

        this.superService = superService;
        this.currentUser = currentUser;
        this.superService.getMessageService().addObserver(this);

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
     * Method for loading the table from the memory to the ListView in the GUI.
     */
    @FXML
    public void loadListData() {

        this.listView.getItems().clear();

        Map<Integer, List<MessageDTO>> map = this.superService.getAllConversations(this.currentUser);

        List<ConversationDTO> list = new ArrayList<>();

        for (Integer key : map.keySet()) {

            MessageDTO messageDTO = map.get(key).get(0);

            List<UserDTO> participants = new ArrayList<>(messageDTO.getTo());
            participants.add(messageDTO.getFrom());
            participants.sort(Comparator.comparing(UserDTO::getId));

            ConversationDTO conversationDTO = new ConversationDTO(null, participants, messageDTO);

            String string = "";
            for (UserDTO u : conversationDTO.getParticipants())
                string += u.getFirstName() + " " + u.getLastName() + " | ";

            conversationDTO.setFromString(string);

            list.add(conversationDTO);
        }

        list.sort(Comparator.comparing(o -> o.getLatestMessage().getDate(), Comparator.reverseOrder()));

        this.listView.getItems().setAll(list);

    }

    /**
     * Method for returning to the user friends menu.
     */
    @FXML
    public void onBackButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("page-view.fxml"));

            Parent root = fxmlLoader.load();

            PageController pc = fxmlLoader.getController();

            this.superService.getMessageService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            GlobalVariable.setMessageLast(LocalDateTime.now());

            pc.setAttributes(this.superService, this.superService.getPageForUser(this.currentUser));

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

    }

    /**
     * This method is called when a conversation from the list widget gets selected
     */
    @FXML
    public void onConversationSelected() {

        ConversationDTO conversationDTO = listView.getSelectionModel().getSelectedItem();

        try {

            if (conversationDTO == null)
                throw new ControllerException("No conversation selected!");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("conversation-view.fxml"));
            Parent root = fxmlLoader.load();

            ConversationMenuController controller = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            controller.setAttributes(this.superService, this.currentUser, conversationDTO);

            Stage stage1 = (Stage) this.listView.getScene().getWindow();
            stage1.close();

            stage.show();

        }
        catch (ControllerException ignored) {}

        catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * Method that is called when clicking the button for composing a new message.
     */
    @FXML
    public void onComposeButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("compose-view.fxml"));

            Parent root = fxmlLoader.load();

            ComposeController usc = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            usc.setAttributes(this.superService, this.currentUser);

            stage.showAndWait();

        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * This method closes our application and is called when we press exit button
     */
    @FXML
    public void onExitButtonClick() {

        UserDTO u2 = new UserDTO(null, this.currentUser.getFirstName(), this.currentUser.getLastName(), this.currentUser.getUserName());
        u2.setLastLogin(LocalDateTime.now());
        this.superService.updateUser(this.currentUser, u2);

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
     * This method is called when an observable notifies all his observers
     */
    @Override
    public void update() {

        this.loadListData();
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
