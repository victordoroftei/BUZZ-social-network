package com.example.laborator5;

import com.example.laborator5.socialnetwork.utils.observer.Observer;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.ConversationDTO;
import com.example.laborator5.socialnetwork.service.dto.MessageDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is used for managing the conversation menu
 */
public class ConversationMenuController implements Observer {

    /**
     * The connection to the super service
     */
    private SuperService superService;

    /**
     * The user that is currently logged in.
     */
    private UserDTO currentUser;

    /**
     * The current conversation
     */
    private ConversationDTO currentConversation;

    /**
     * The back button who closes the current window and opens the messages window
     */
    @FXML
    private Button backButton;

    /**
     * The name of the conversation
     */
    @FXML
    private Label nameLabel;

    /**
     * The widget used for showing the messages from our current conversation
     */
    @FXML
    private ListView<VBox> listView;

    /**
     * The widget used for getting the message we want to send to the participants
     */
    @FXML
    private TextArea textArea;

    /**
     * The send button
     */
    @FXML
    private Button sendButton;

    /**
     * The root of the window.
     */
    @FXML
    private VBox mainBox;

    /**
     * The constructor of our class
     */
    public ConversationMenuController() {

        this.listView = new ListView<>();
    }

    /**
     * This method sets the private fields of our controller
     *
     * @param superService    the connection to the super service
     * @param currentUser     the user that is currently logged in
     * @param conversationDTO the current conversation
     */
    public void setAttributes(SuperService superService, UserDTO currentUser, ConversationDTO conversationDTO) {

        this.superService = superService;
        this.currentUser = currentUser;
        this.currentConversation = conversationDTO;
        this.sendButton.setVisible(false);

        this.textArea.setWrapText(true);

        this.nameLabel.setWrapText(true);
        this.nameLabel.setMaxWidth(370);
        this.nameLabel.setText(this.currentConversation.getFromString());

        this.superService.getMessageService().addObserver(this);

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        this.updateListView();
    }

    /**
     * This method updates the list where the conversation is displayed
     */
    public void updateListView() {

        this.listView.getItems().clear();

        List<MessageDTO> list = this.superService.getAllConversations(this.currentUser).get(this.currentConversation.getParticipants().stream().map(UserDTO::getId).collect(Collectors.toList()).hashCode());
        Collections.reverse(list);

        // E posibil sa fie bug cu JavaFX 17 sa nu mearga scrollTo() pe ListView-uri.
        //this.listView.scrollTo(this.listView.getItems().size() - 1);

        List<VBox> boxList = new ArrayList<>();
        for (MessageDTO message : list) {

            VBox box;

            try {

                FXMLLoader fxmlLoader = new FXMLLoader();

                if (message.getFrom().equals(this.currentUser))
                    fxmlLoader.setLocation(getClass().getResource("message-bubble-sent-view.fxml"));

                else
                    fxmlLoader.setLocation(getClass().getResource("message-bubble-received-view.fxml"));

                box = fxmlLoader.load();

                MessageBubbleController controller = fxmlLoader.getController();
                controller.setAttributes(message.getFrom(), message.getMessage(), message.getDate());

                boxList.add(new VBox(box));

            } catch (IOException e) {

                e.printStackTrace();

            }
        }

        this.listView.setItems(FXCollections.observableArrayList(boxList));
    }

    /**
     * This function is used when back button is pressed
     */
    @FXML
    public void onBackButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("message-view.fxml"));

            Parent root = fxmlLoader.load();

            this.superService.getMessageService().removeObserver(this);

            MessageController mc = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            mc.setAttributes(this.superService, this.currentUser);

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method gets called every time our text field gets modified
     */
    @FXML
    public void showOrHideSendButton() {

        this.sendButton.setVisible(!this.textArea.getText().equals(""));
    }

    /**
     * This function is used when send button is pressed
     */
    @FXML
    public void onSendButtonClick() {

        String message = this.textArea.getText();
        this.textArea.clear();

        if (this.currentConversation.getParticipants().size() == 2)

            this.superService.replyToMessage(new MessageDTO(null, this.currentUser, this.currentConversation.getParticipants().stream().filter(x -> !Objects.equals(x.getId(), this.currentUser.getId())).collect(Collectors.toList()), message, LocalDateTime.now(), this.currentConversation.getLatestMessage().getId()));

        else

            this.superService.replyToAll(new MessageDTO(null, this.currentUser, this.currentConversation.getParticipants().stream().filter(x -> !Objects.equals(x.getId(), this.currentUser.getId())).collect(Collectors.toList()), message, LocalDateTime.now(), this.currentConversation.getLatestMessage().getId()));

        this.sendButton.setVisible(false);

        //this.updateListView();
    }

    /**
     * This method is called when an observable object calls the updateAll method
     */
    @Override
    public void update() {

        this.updateListView();
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
