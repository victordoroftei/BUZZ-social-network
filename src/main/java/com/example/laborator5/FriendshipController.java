package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.service.dto.RequestDTO;
import com.example.laborator5.socialnetwork.utils.CustomHBox;
import com.example.laborator5.socialnetwork.utils.EntitiesOnPage;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.FriendshipDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.collections.FXCollections;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the controller of the user-menu-view
 */
public class FriendshipController implements Observer {

    /**
     * The connection to the super service
     */
    private SuperService superService;

    /**
     * The user that is currently logged in.
     */
    private UserDTO currentUser;

    /**
     * The logout button
     */
    @FXML
    private Button backButton;

    /**
     * The list view object in which we show the friendships of the current user
     */
    @FXML
    private ListView<CustomHBox> listView;

    /**
     * The label for the current page.
     */
    @FXML
    private Label labelPage;

    /**
     * The current page of the list.
     */
    private int currentListPage;

    /**
     * The maximum number of pages.
     */
    private int maxPages;

    /**
     * The main box of the window.
     */
    @FXML
    private HBox mainBox;

    /**
     * The search bar for searching users.
     */
    @FXML
    private ComboBox<CustomHBox> searchBar;

    /**
     * The users of the application.
     */
    private List<UserDTO> users;

    /**
     * The constructor of the user menu controller
     */
    public FriendshipController() {

        this.listView = new ListView<>();
    }

    /**
     * Method used for updating the data about pages.
     */
    private void updatePages(int value) {

        if (value != -1)
            this.currentListPage = value;

        int maxSize = this.superService.getAllFriendshipsForAUser(this.currentUser).size();

        if (maxSize % EntitiesOnPage.getNumber() == 0)
            this.maxPages = maxSize / EntitiesOnPage.getNumber();

        else
            this.maxPages = maxSize / EntitiesOnPage.getNumber() + 1;

        if (maxSize == 0)

            this.labelPage.setText("0/0");

        else
            this.labelPage.setText(String.valueOf(this.currentListPage + 1) + "/" + this.maxPages);

    }

    /**
     * This method updates the list view
     */
    public void updateListView() {

        this.listView.getItems().clear();

        this.updatePages(-1);

        //this.superService.getAllFriendshipsForAUserOnPage(this.currentUser, this.currentListPage).forEach(x -> this.listView.getItems().add(x.getUser2()));

        List<UserDTO> l = this.superService.getAllFriendshipsForAUserOnPage(this.currentUser, this.currentListPage).stream().map(FriendshipDTO::getUser2).collect(Collectors.toList());

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
     * This method sets the fields of this controller
     *
     * @param superService the connection to the super service
     * @param currentUser  the user that is currently logged in
     */
    public void setAttributes(SuperService superService, UserDTO currentUser, List<UserDTO> users) {

        this.superService = superService;
        this.currentUser = currentUser;

        this.superService.getFriendshipService().addObserver(this);

        this.currentListPage = 0;

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        this.searchBar.setPromptText("Search for a user");
        this.searchBar.setEditable(true);
        
        this.users = users;

        this.searchBar.setConverter(new StringConverter<CustomHBox>() {

            @Override
            public String toString(CustomHBox object) {

                return object == null ? "" : object.toString();
            }

            @Override
            public CustomHBox fromString(String string) {

                if (string == null || string.isEmpty()) {
                    return null;
                }

                // try matching names
                for (CustomHBox box : searchBar.getItems()) {
                    if (box.toString().equals(string)) {
                        return box;
                    }
                }

                return null;
            }

        });

        this.updateListView();

    }


    /**
     * This method is called when we press the Logout button which closes the current window and opens the log in one
     */
    @FXML
    public void onBackButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("page-view.fxml"));

            Parent root = fxmlLoader.load();

            PageController pc = fxmlLoader.getController();

            this.superService.getFriendshipService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            pc.setAttributes(this.superService, this.superService.getPageForUser(this.currentUser));

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.close();

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is called when we press the delete button, and deletes a friendship between two users
     */
    public void onDeleteFriendButtonAction() {

        try {

            UserDTO userDTO = listView.getSelectionModel().getSelectedItem().getUserDTO();

            this.updatePages(0);

            this.superService.removeFriendship(new FriendshipDTO(this.currentUser, new UserDTO(2L, "x", "x", userDTO.getUserName()), LocalDateTime.now()));
            this.updateListView();


            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Friendship deleted!");
            alert.showAndWait();
        } catch (NullPointerException err) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "No friend selected!");
            alert.showAndWait();
        } catch (ValidationException | ServiceException err) {

            Alert alert = new Alert(Alert.AlertType.ERROR, err.getMessage());
            alert.showAndWait();
        }

    }

    /**
     * This method is called when we press the add friend button which tries to add a friend request between our user and the user with the username entered in the text field
     */
    public void onAddFriendButtonAction() {

        try {

            CustomHBox selectedBox = (CustomHBox) this.searchBar.getSelectionModel().getSelectedItem();
            if (selectedBox == null)
                throw new ControllerException("You must select a user!");

            String addedUser = selectedBox.getUserDTO().getUserName();

            this.updatePages(0);

            this.superService.addRequest(new RequestDTO(this.currentUser, new UserDTO(0L, "x", "x", addedUser), LocalDateTime.now(), "pending"));

            this.searchBar.setValue(null);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Request Sent!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException err) {

            Alert alert = new Alert(Alert.AlertType.ERROR, err.getMessage());
            alert.showAndWait();
        }

    }

    /**
     * Method called when clicking the previous page button.
     */
    @FXML
    public void onPreviousPageButtonClick() {

        if (this.currentListPage > 0) {

            this.currentListPage--;

            this.labelPage.setText(String.valueOf(this.currentListPage + 1) + "/" + this.maxPages);

            this.update();
        }

    }

    /**
     * Method called when clicking the next page button.
     */
    @FXML
    public void onNextPageButtonClick() {

        if (this.currentListPage < this.maxPages - 1) {

            this.currentListPage++;

            this.labelPage.setText(String.valueOf(this.currentListPage + 1) + "/" + this.maxPages);

            this.update();
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

        Stage stage = (Stage) this.labelPage.getScene().getWindow();
        stage.close();
    }

    /**
     * Method called when the yellow button is pressed
     */
    @FXML
    public void onMinimizeButtonClick() {

        Stage stage = (Stage) this.labelPage.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * This method is called when an observable calls the updateAll method
     */
    @Override
    public void update() {

        this.updateListView();
    }

    /**
     * This method is called when the user searches for another user
     */
    @FXML
    public void onInputChangedSearchBox() {

        String keyWords = this.searchBar.getEditor().getText();
        List<UserDTO> filteredUsers = this.users.stream().filter(x -> (!x.getUserName().equals(this.currentUser.getUserName()) &&
                (x.getUserName().contains(keyWords) || x.getLastName().contains(keyWords) || x.getFirstName().contains(keyWords)))).collect(Collectors.toList());

        this.searchBar.getItems().clear();

        List<CustomHBox> list = new ArrayList<>();

        for (UserDTO user : filteredUsers) {

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

            this.searchBar.setItems(FXCollections.observableArrayList(list));
        }
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
