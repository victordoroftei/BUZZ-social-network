package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.domain.Request;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.*;
import com.example.laborator5.socialnetwork.utils.CustomVBox;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * The role of this class is to handle the user profile actions which can occur
 */
public class UserProfileController implements Initializable, Observer {

    /**
     * Current logged-in user
     */
    private UserDTO currentLoggedInUser;

    /**
     * The owner of the profile
     */
    private UserDTO usersPage;

    /**
     * Connection to the service
     */
    private SuperService superService;

    /**
     * The profile
     */
    private ProfileDTO profile;

    /**
     * Back button
     */
    @FXML
    private Button backButton;

    /**
     * Edit button
     */
    @FXML
    private Button editButton;

    /**
     * Button for profile picture
     */
    @FXML
    private Button profilePicture;

    /**
     * Button for multi usage purpose
     */
    @FXML
    private Button multiUsageButton;

    /**
     * The button for rejecting a request.
     */
    @FXML
    private Button rejectButton;

    /**
     * About me text area widget
     */
    @FXML
    private TextArea aboutTextArea;

    /**
     * Birthday date picker
     */
    @FXML
    private DatePicker birthdayDatePicker;

    /**
     * Town text area widget
     */
    @FXML
    private TextArea townTextArea;

    /**
     * Hobbies text area widget
     */
    @FXML
    private TextArea hobbiesTextArea;

    /**
     * Friends list widget
     */
    @FXML
    private ListView<HBox> friendsList;

    /**
     * Posts list widget
     */
    @FXML
    private ListView<CustomVBox> postsList;

    /**
     * The label that displays the name of the users profile
     */
    @FXML
    private Label nameLabel;

    /**
     * The boolean value used if we want to edit our fields from profile
     */
    private boolean editStatus;

    /**
     * Button for editing the profile picture
     */
    @FXML
    private Button editProfileButton;

    /**
     * Button for editing the cover picture
     */
    @FXML
    private Button editCoverButton;

    /**
     * The widget that contains the cover image of a profile
     */
    @FXML
    private HBox coverImage;

    /**
     * The root of the window.
     */
    @FXML
    private VBox mainBox;

    /**
     * The label above the feed list.
     */
    @FXML
    private Label feedLabel;

    /**
     * The button for adding a new post.
     */
    @FXML
    private Button addPostButton;

    /**
     * Method for initializing the constructor of the controller class.
     *
     * @param location  the location
     * @param resources the resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (this.birthdayDatePicker != null) {
            this.birthdayDatePicker.setDayCellFactory(param -> new DateCell() {

                @Override
                public void updateItem(LocalDate date, boolean empty) {

                    super.updateItem(date, empty);
                    setDisable(empty || date.compareTo(LocalDate.now()) > 0);
                }
            });
        }

    }

    /**
     * Method which loads a user's posts into the list view.
     */
    @FXML
    public void loadPostsListData() {

        this.postsList.getItems().clear();

        List<PostDTO> posts = this.superService.getAllPostsForUser(this.usersPage);

        List<CustomVBox> boxList = new ArrayList<>();

        for (PostDTO post : posts) {

            VBox box;

            try {

                FXMLLoader fxmlLoader = new FXMLLoader();

                fxmlLoader.setLocation(getClass().getResource("profile-post-view.fxml"));

                box = fxmlLoader.load();

                ProfilePostController controller = fxmlLoader.getController();
                controller.setAttributes(this.usersPage, post);

                boxList.add(new CustomVBox(box, this.usersPage, post));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.postsList.setItems(FXCollections.observableArrayList(boxList));

    }

    /**
     * The role of this method is to load the friends list data into the list view
     */
    @FXML
    public void loadFriendsListData() {

        this.friendsList.getItems().clear();

        List<UserDTO> friends = this.superService.getAllFriendshipsForAUser(this.usersPage).stream().map(FriendshipDTO::getUser2).collect(Collectors.toList());

        List<HBox> list = new ArrayList<>();

        for (UserDTO u : friends) {

            HBox box;

            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("search-view.fxml"));

                box = fxmlLoader.load();

                SearchController searchController = fxmlLoader.getController();
                searchController.setAttributes(this.currentLoggedInUser, u, this.superService);

                list.add(new HBox(box));

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

        this.friendsList.getItems().setAll(list);

    }

    /**
     * The role of this method is to set the profile picture of the current user's page
     *
     * @param size - the size of the picture
     */
    private void setPicture(int size) {

        String sizeString = String.valueOf(size);

        try {

            String image = getClass().getResource("css/img/profile_pictures/pf_" + this.usersPage.getUserName() + ".png").toExternalForm();

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
     * The role of this method is to set the cover picture of the current user's page
     */
    private void setCoverPicture() {

        try {

            String image = getClass().getResource("css/img/profile_pictures/cv_" + this.usersPage.getUserName() + ".png").toExternalForm();

            this.coverImage.setStyle("-fx-background-image: url('" + image + "');" +
                    "-fx-background-size: " + 775 + "px " + 245 + "px; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: stretch;" +
                    "-fx-background-color: transparent");

        } catch (NullPointerException ignored) {

            String image = getClass().getResource("css/img/profile_pictures/cv_default.png").toExternalForm();

            this.coverImage.setStyle("-fx-background-image: url('" + image + "');" +
                    "-fx-background-size: " + 775 + "px " + 245 + "px; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: stretch;" +
                    "-fx-background-color: transparent");
        }
    }


    /**
     * Method used for setting the attributes and initializing the window
     *
     * @param currentLoggedInUser - current logged-in user
     * @param usersPage           - the owner of the profile
     * @param superService        - connection to the super service
     */
    public void setAttributes(UserDTO currentLoggedInUser, UserDTO usersPage, SuperService superService) {

        this.currentLoggedInUser = currentLoggedInUser;
        this.usersPage = usersPage;
        this.superService = superService;
        this.profile = this.superService.getProfileForUser(usersPage);

        this.setPicture(100);
        this.setCoverPicture();

        this.loadPostsListData();
        this.loadFriendsListData();

        this.nameLabel.setWrapText(true);
        this.nameLabel.setMaxWidth(400);

        this.editStatus = false;
        this.editButton.setText("Edit");

        // means we are not on my profile
        this.editButton.setVisible(this.currentLoggedInUser.getUserName().equals(this.usersPage.getUserName()));

        this.aboutTextArea.setText(this.profile.getAboutMe());
        this.aboutTextArea.setEditable(false);

        this.birthdayDatePicker.setEditable(false);

        this.townTextArea.setText(this.profile.getHomeTown());
        this.townTextArea.setEditable(false);

        this.hobbiesTextArea.setText(this.profile.getHobbies());
        this.hobbiesTextArea.setEditable(false);

        this.birthdayDatePicker.setValue(this.profile.getBirthday());
        this.birthdayDatePicker.setDisable(true);
        this.birthdayDatePicker.getEditor().setOpacity(2);

        this.nameLabel.setText(this.usersPage.toString());

        this.rejectButton.setVisible(false);

        if (this.currentLoggedInUser.getUserName().equals(this.usersPage.getUserName())) {

            this.multiUsageButton.setText("Delete Account");
            this.addPostButton.setVisible(true);
            this.feedLabel.setText("Your Feed");

        } else {

            this.editProfileButton.setVisible(false);
            this.editCoverButton.setVisible(false);

            this.addPostButton.setVisible(false);
            this.feedLabel.setText(this.usersPage.getFirstName() + "'s Feed");

            boolean friendshipTest = this.superService.searchFriendship(new FriendshipDTO(this.currentLoggedInUser, this.usersPage, null));

            if (friendshipTest)

                this.multiUsageButton.setText("Delete Friendship");

            else {

                Request request = this.superService.searchRequest(new RequestDTO(this.currentLoggedInUser, this.usersPage, null, "pending"));

                if (request == null)

                    this.multiUsageButton.setText("Send Friend Request");

                else if (request.getIdUser1().equals(this.currentLoggedInUser.getId()))

                    this.multiUsageButton.setText("Delete Friend Request");

                else {

                    this.multiUsageButton.setText("Accept Friend Request");
                    this.rejectButton.setVisible(true);
                }
            }
        }

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });


        this.superService.getPostService().addObserver(this);

    }

    /**
     * Method called when clicking the button for rejecting a request.
     */
    @FXML
    public void onRejectButtonClick() {

        try {

            this.superService.rejectRequest(new RequestDTO(this.usersPage, this.currentLoggedInUser, null, null));

            this.rejectButton.setVisible(false);
            this.multiUsageButton.setText("Send Friend Request");

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Friendship request rejected!");
            alert.showAndWait();
        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method called when clicking the multi-usage button click.
     */
    @FXML
    public void onMultiUsageButtonClick() {

        try {

            String text = this.multiUsageButton.getText();
            String outputMessage = "";

            if (text.equals("Delete Account")) {

                this.superService.removeUser(this.currentLoggedInUser);
                outputMessage = "Account Deleted!";

                Alert alert = new Alert(Alert.AlertType.INFORMATION, outputMessage);
                alert.showAndWait();

                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("login-view.fxml"));

                    Parent root = fxmlLoader.load();

                    Controller controller = fxmlLoader.getController();
                    controller.setSuperService(this.superService);

                    this.superService.getEventService().removeObserver(this);

                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Socialnetwork");
                    stage.getIcons().add(new Image("file:icon.jpg"));
                    stage.setScene(scene);

                    Stage stage2 = (Stage) this.multiUsageButton.getScene().getWindow();
                    stage2.close();

                    stage.show();

                    return;
                } catch (IOException ex) {

                    ex.printStackTrace();
                }
            } else if (text.equals("Delete Friendship")) {

                this.superService.removeFriendship(new FriendshipDTO(this.currentLoggedInUser, this.usersPage, LocalDateTime.now()));
                this.multiUsageButton.setText("Send Friend Request");
                outputMessage = "You are no longer friends!";
            } else if (text.equals("Send Friend Request")) {

                this.superService.addRequest(new RequestDTO(this.currentLoggedInUser, this.usersPage, LocalDateTime.now(), "pending"));
                this.multiUsageButton.setText("Delete Friend Request");
                outputMessage = "Friendship request sent!";
            } else if (text.equals("Delete Friend Request")) {

                this.superService.deleteRequest(new RequestDTO(this.currentLoggedInUser, this.usersPage, null, null));
                this.multiUsageButton.setText("Send Friend Request");
                outputMessage = "Friendship request deleted!";
            } else if (text.equals("Accept Friend Request")) {

                this.superService.acceptRequest(new RequestDTO(this.usersPage, this.currentLoggedInUser, null, null));
                this.multiUsageButton.setText("Delete Friendship");
                this.rejectButton.setVisible(false);
                outputMessage = "You are now friends!";
            }

            Alert alert2 = new Alert(Alert.AlertType.INFORMATION, outputMessage);
            alert2.showAndWait();
        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }

    }

    /**
     * Controller method for returning to the main menu.
     */
    @FXML
    public void onBackButtonClick() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("page-view.fxml"));

            Parent root = fxmlLoader.load();

            PageController pc = fxmlLoader.getController();

            this.superService.getPostService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            pc.setAttributes(this.superService, this.superService.getPageForUser(this.currentLoggedInUser));

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method called when clicking the button for editing a profile.
     */
    @FXML
    public void onEditButtonClick() {

        if (!editStatus) {

            this.editStatus = true;
            this.editButton.setText("Save");
        } else {

            this.editStatus = false;
            this.editButton.setText("Edit");
            String aboutMe = this.aboutTextArea.getText();
            LocalDate birthday = this.birthdayDatePicker.getValue();
            String town = this.townTextArea.getText();
            String hobbies = this.hobbiesTextArea.getText();

            this.superService.updateProfile(new ProfileDTO(this.currentLoggedInUser, aboutMe, town, birthday, hobbies));
        }

        this.aboutTextArea.setEditable(editStatus);
        this.townTextArea.setEditable(editStatus);
        this.hobbiesTextArea.setEditable(editStatus);
        this.birthdayDatePicker.setDisable(!editStatus);
    }

    /**
     * Method called when clicking a post from the post list.
     */
    @FXML
    public void onPostListItemClicked() {

        if (this.currentLoggedInUser.equals(this.usersPage)) {

            try {

                CustomVBox selectedBox = this.postsList.getSelectionModel().getSelectedItem();

                if (selectedBox != null) {

                    PostDTO post = selectedBox.getPost();

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("post-edit-view.fxml"));

                    Parent root = loader.load();

                    PostEditController pec = loader.getController();

                    this.postsList.getSelectionModel().clearSelection();

                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Socialnetwork");
                    stage.getIcons().add(new Image("file:icon.jpg"));
                    stage.setScene(scene);

                    pec.setAttributes(this.superService, this.currentLoggedInUser, post);

                    stage.show();
                }
            }

            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * Method called when clicking the button for adding a post.
     */
    @FXML
    public void onAddPostButtonClick() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("post-edit-view.fxml"));

            Parent root = loader.load();

            PostEditController pec = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            pec.setAttributes(this.superService, this.currentLoggedInUser, null);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    /**
     * This method gets called when we press the edit button for the profile picture
     */
    @FXML
    public void onEditProfileButtonClick() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );

        Stage stage = (Stage) this.editProfileButton.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "No file selected!");
            alert.showAndWait();
        } else {

            String path = file.toString();

            String newPath = new File("").getAbsolutePath() + "\\src\\main\\resources\\com\\example\\laborator5\\css\\img\\profile_pictures\\pf_" + this.currentLoggedInUser.getUserName() + ".png";

            try (
                    InputStream in = new BufferedInputStream(
                            new FileInputStream(path));
                    OutputStream out = new BufferedOutputStream(
                            new FileOutputStream(newPath))) {

                byte[] buffer = new byte[1024];
                int lengthRead;
                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    out.flush();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your changes will be seen the next time you enter the app!");
                alert.showAndWait();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

    }

    /**
     * This method gets called when we press the edit cover picture button
     */
    @FXML
    public void onEditCoverButtonClick() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );

        Stage stage = (Stage) this.editCoverButton.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "No file selected!");
            alert.showAndWait();
        } else {

            String path = file.toString();

            String newPath = new File("").getAbsolutePath() + "\\src\\main\\resources\\com\\example\\laborator5\\css\\img\\profile_pictures\\cv_" + this.currentLoggedInUser.getUserName() + ".png";

            try (
                    InputStream in = new BufferedInputStream(
                            new FileInputStream(path));
                    OutputStream out = new BufferedOutputStream(
                            new FileOutputStream(newPath))) {

                byte[] buffer = new byte[1024];
                int lengthRead;
                while ((lengthRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, lengthRead);
                    out.flush();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your changes will be seen the next time you enter the app!");
                alert.showAndWait();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * Method called when the mouse hovers over the friends list.
     */
    @FXML
    public void setStage() {

        GlobalVariable.setMainPageStage((Stage) this.friendsList.getScene().getWindow());
    }

    /**
     * This method closes our application and is called when we press exit button
     */
    @FXML
    public void onExitButtonClick() {

        UserDTO u2 = new UserDTO(null, this.currentLoggedInUser.getFirstName(), this.currentLoggedInUser.getLastName(), this.currentLoggedInUser.getUserName());
        u2.setLastLogin(LocalDateTime.now());
        this.superService.updateUser(this.currentLoggedInUser, u2);

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

    /**
     * Method called when the observables are updated.
     */
    @Override
    public void update() {

        this.loadPostsListData();
    }
}
