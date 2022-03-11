package com.example.laborator5;

import com.example.laborator5.socialnetwork.domain.Tuple;
import com.example.laborator5.socialnetwork.service.dto.EventDTO;
import com.example.laborator5.socialnetwork.service.dto.Page;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.PostDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PageController implements Observer {

    /**
     * The super service of the controller
     */
    private SuperService superService;

    /**
     * The page of the user that is currently logged in
     */
    private Page currentPage;

    /**
     * Button for returning to the log in menu.
     */
    @FXML
    private Button logOutButton;

    /**
     * The label for the current page.
     */
    @FXML
    private Label pageLabel;

    /**
     * The notification button for the events.
     */
    @FXML
    private Button eventNotification;

    /**
     * The notification button for the request.
     */
    @FXML
    private Button requestNotification;

    /**
     * The notification button for the message.
     */
    @FXML
    private Button messageNotification;

    /**
     * The combobox for the upcoming events.
     */
    @FXML
    private ComboBox<HBox> comboBox;

    /**
     * The ComboBox for the search bar
     */
    @FXML
    private ComboBox<HBox> searchBar;

    /**
     * Button for showing my profile
     */
    @FXML
    private Button myProfileButton;

    /**
     * The list for a user's feed
     */
    @FXML
    private ListView<VBox> feedList;

    /**
     * The list of all the users in our application
     */
    private List<UserDTO> users;

    /**
     * The main HBox of the window.
     */
    @FXML
    private HBox mainBox;

    /**
     * The button for showing upcoming events.
     */
    @FXML
    private Button notificationButton;

    /**
     * The popover mini-window shown when clicking the notification button.
     */
    @FXML
    private PopOver popOver;

    /**
     * The role of this method is to set the profile picture of the current logged-in user
     *
     * @param size - the size of the picture
     */
    private void setPicture(int size) {

        String sizeString = String.valueOf(size);

        try {

            String image = getClass().getResource("css/img/profile_pictures/pf_" + this.currentPage.getUser().getUserName() + ".png").toExternalForm();

            this.myProfileButton.setStyle("-fx-background-image: url('" + image + "');" +
                    "-fx-background-size: " + sizeString + "px " + sizeString + "px; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: stretch;" +
                    "-fx-background-color: transparent");
        } catch (NullPointerException ignored) {

            String image = getClass().getResource("css/img/profile_pictures/default.png").toExternalForm();

            this.myProfileButton.setStyle("-fx-background-image: url('" + image + "');" +
                    "-fx-background-size: " + sizeString + "px " + sizeString + "px; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: stretch;" +
                    "-fx-background-color: transparent");
        }
    }

    /**
     * Setter method for the attributes of this class.
     *
     * @param superService the super service of the controller
     * @param currentPage  the page of the user that is currently logged in
     */
    public void setAttributes(SuperService superService, Page currentPage) {

        this.superService = superService;
        this.currentPage = currentPage;

        if (GlobalVariable.getMessageLast() == null)

            GlobalVariable.setMessageLast(this.currentPage.getUser().getLastLogin());

        if (GlobalVariable.getRequestLast() == null)

            GlobalVariable.setRequestLast(this.currentPage.getUser().getLastLogin());

        this.setPicture(80);

        this.pageLabel.setWrapText(true);

        this.pageLabel.setText("Hello, " + this.currentPage.getUser().getFirstName() + " " + this.currentPage.getUser().getLastName() + "!");

        Stage stage = (Stage) this.pageLabel.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        this.eventNotification.setText(String.valueOf(this.currentPage.getEvents().size()));

        /*if (!GlobalVariable.isSeenNewMessages()) {

            this.messageNotification.setVisible(true);
            this.messageNotification.setText(String.valueOf(this.superService.getAllRecentMessages(this.currentPage.getUser()).size()));
        } else
            this.messageNotification.setVisible(false);

        if (!GlobalVariable.isSeenNewRequests()) {

            this.requestNotification.setVisible(true);
            this.requestNotification.setText(String.valueOf(this.superService.getAllRecentRequests(this.currentPage.getUser()).size()));
        } else
            this.requestNotification.setVisible(false);*/

        //this.messageNotification.setText(String.valueOf(this.currentPage.getMessages().size()));  // var functionala veche - merge
        this.messageNotification.setText(String.valueOf(this.currentPage.getMessages().stream().filter(x->x.getDate().isAfter(GlobalVariable.getMessageLast())).count()));

        //this.requestNotification.setText(String.valueOf(this.currentPage.getRequests().size()));  // var functionala veche - merge
        this.requestNotification.setText(String.valueOf((int) this.currentPage.getRequests().stream().filter(x -> x.getDate().isAfter(GlobalVariable.getRequestLast())).count()));

        this.superService.getEventService().addObserver(this);

        this.searchBar.setPromptText("Search for a user");
        this.searchBar.setEditable(true);

        this.users = new ArrayList<>();
        this.superService.getAll().forEach(x -> this.users.add(new UserDTO(x.getId(), x.getFirstName(), x.getLastName(), x.getUserName())));

        this.loadFeedListData();

    }

    /**
     * Method used for loading a user's feed into the list view.
     */
    @FXML
    public void loadFeedListData() {

        GlobalVariable.setMainPageStage((Stage) this.pageLabel.getScene().getWindow());

        this.feedList.getItems().clear();

        List<Object> list = this.superService.getFeedForUser(this.currentPage);
        List<VBox> boxList = new ArrayList<>();

        for (Object o : list) {

            VBox box;

            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("feed-post-view.fxml"));

                box = fxmlLoader.load();

                FeedPostController fpc = fxmlLoader.getController();
                if (o instanceof PostDTO)
                    fpc.setAttributes(this.superService, this.currentPage.getUser(), (PostDTO) o, null);

                else
                    fpc.setAttributes(this.superService, this.currentPage.getUser(), null, (Tuple<UserDTO, EventDTO>) o);

                boxList.add(new VBox(box));
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.feedList.setItems(FXCollections.observableArrayList(boxList));

    }

    /**
     * Method which is called when clicking the event menu button.
     */
    @FXML
    public void onEventMenuButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("event-view.fxml"));

            Parent root = fxmlLoader.load();

            EventController ec = fxmlLoader.getController();

            this.superService.getEventService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            ec.setAttributes(this.superService, this.currentPage.getUser(), this.currentPage.getEvents());

            Stage stage2 = (Stage) this.logOutButton.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * This method is called when we press on message button
     */
    @FXML
    public void onMessageMenuButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("message-view.fxml"));

            Parent root = fxmlLoader.load();

            MessageController mc = fxmlLoader.getController();

            this.superService.getEventService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            mc.setAttributes(this.superService, this.currentPage.getUser());

            Stage stage2 = (Stage) this.logOutButton.getScene().getWindow();
            stage2.close();

            GlobalVariable.setSeenNewMessages(true);
            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * This method is called when we press on friends button
     */
    @FXML
    public void onFriendsListButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("friendship-menu-view.fxml"));

            Parent root = fxmlLoader.load();
            FriendshipController fc = fxmlLoader.getController();

            this.superService.getEventService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            fc.setAttributes(this.superService, this.currentPage.getUser(), this.users);

            Stage stage2 = (Stage) this.logOutButton.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * This method is called when we press on request menu button
     */
    @FXML
    public void onRequestMenuButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("request-view.fxml"));

            Parent root = fxmlLoader.load();

            RequestController rc = fxmlLoader.getController();

            this.superService.getEventService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            rc.setAttributes(this.superService, this.currentPage.getUser(), this.users);

            Stage stage2 = (Stage) this.logOutButton.getScene().getWindow();
            stage2.close();

            GlobalVariable.setSeenNewRequests(true);
            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * Method that is called when clicking the notification icon.
     */
    @FXML
    public void onNotificationClick() {

        if ( this.popOver == null || !this.popOver.isShowing()) {

            List<EventDTO> events = this.currentPage.getEvents();

            List<HBox> list = new ArrayList<>();

            for (EventDTO event : events) {

                HBox box;

                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("notification-hbox-view.fxml"));

                    box = fxmlLoader.load();

                    NotificationHBoxController hc = fxmlLoader.getController();
                    hc.setAttributes(this.superService, this.currentPage.getUser(), event);

                    list.add(new HBox(box));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            ListView<HBox> listView = new ListView<>();
            listView.setItems(FXCollections.observableArrayList(list));
            listView.setPrefWidth(300);
            listView.getStylesheets().add(getClass().getResource("css/popover-style.css").toExternalForm());

            this.popOver = new PopOver(listView);

            this.popOver.setHeaderAlwaysVisible(true);
            this.popOver.setTitle("Upcoming Events");

            this.popOver.show(this.notificationButton);
        }

        else
            this.popOver.hide();

    }

    /**
     * Method called when pressing the report button.
     */
    @FXML
    public void onReportMenuButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("report-view.fxml"));

            Parent root = fxmlLoader.load();

            ReportController rc = fxmlLoader.getController();

            this.superService.getEventService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            rc.setAttributes(this.superService, this.currentPage.getUser());

            Stage stage2 = (Stage) this.logOutButton.getScene().getWindow();
            stage2.close();

            GlobalVariable.setSeenNewRequests(true);
            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

    }

    /**
     * This method is called when we press the log-out button.
     */
    @FXML
    public void onLogOutButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("login-view.fxml"));

            Parent root = fxmlLoader.load();

            Controller controller = fxmlLoader.getController();

            UserDTO currentDTO = this.currentPage.getUser();
            UserDTO userDTO = new UserDTO(currentDTO.getId(), currentDTO.getFirstName(), currentDTO.getLastName(), currentDTO.getUserName());
            userDTO.setLastLogin(LocalDateTime.now());

            this.superService.updateUser(currentDTO, userDTO);

            this.superService.getEventService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            GlobalVariable.setRequestLast(null);
            GlobalVariable.setMessageLast(null);

            controller.setSuperService(this.superService);

            Stage stage2 = (Stage) this.logOutButton.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * Update method for Observer.
     */
    @Override
    public void update() {

        this.currentPage.setEvents(this.superService.getUpcomingEvents(this.currentPage.getUser()));

        this.eventNotification.setText(String.valueOf(this.currentPage.getEvents().size()));

        this.onNotificationClick();
    }

    /**
     * This method is called when the user searches for another user
     */
    @FXML
    public void onInputChangedSearchBox() {

        GlobalVariable.setMainPageStage((Stage) this.searchBar.getScene().getWindow());

        String keyWords = this.searchBar.getEditor().getText().toLowerCase(Locale.ROOT);
        List<UserDTO> filteredUsers = this.users.stream().filter(x -> (!x.getUserName().toLowerCase(Locale.ROOT).equals(this.currentPage.getUser().getUserName().toLowerCase(Locale.ROOT)) &&
                (x.getUserName().toLowerCase(Locale.ROOT).contains(keyWords) || x.getLastName().toLowerCase(Locale.ROOT).contains(keyWords) || x.getFirstName().toLowerCase(Locale.ROOT).contains(keyWords)))).collect(Collectors.toList());

        this.searchBar.getItems().clear();

        List<HBox> list = new ArrayList<>();

        for (UserDTO user : filteredUsers) {

            HBox box;

            try {

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("search-view.fxml"));

                box = fxmlLoader.load();

                SearchController searchController = fxmlLoader.getController();
                searchController.setAttributes(this.currentPage.getUser(), user, this.superService);

                list.add(new HBox(box));

            } catch (IOException e) {

                e.printStackTrace();

            }

            this.searchBar.setItems(FXCollections.observableArrayList(list));
        }
    }

    /**
     * Method called when we want to switch to our profile
     */
    @FXML
    public void onMyProfileButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("user-profile-view.fxml"));

            Parent root = fxmlLoader.load();

            UserProfileController userProfileViewControlle = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            userProfileViewControlle.setAttributes(this.currentPage.getUser(), this.currentPage.getUser(), this.superService);

            Stage stage2 = (Stage) this.myProfileButton.getScene().getWindow();
            stage2.close();

            stage.show();
            GlobalVariable.setMainPageStage(stage);

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    /**
     * This method closes our application and is called when we press exit button
     */
    @FXML
    public void onExitButtonClick() {

        try {

            UserDTO u2 = new UserDTO(null, this.currentPage.getUser().getFirstName(), this.currentPage.getUser().getLastName(), this.currentPage.getUser().getUserName());
            u2.setLastLogin(LocalDateTime.now());
            this.superService.updateUser(this.currentPage.getUser(), u2);

            Platform.exit();
        }

        catch (NullPointerException | IllegalStateException ignored) {

        }
    }

    /**
     * Method called when the yellow button is pressed
     */
    @FXML
    public void onMinimizeButtonClick() {

        Stage stage = (Stage) this.pageLabel.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * Method called when the user hovers the profile picture
     */
    @FXML
    public void onMouseEntered() {

        Scene scene = this.myProfileButton.getScene();
        scene.setCursor(Cursor.HAND);
    }

    /**
     * Method called when the user exits the profile picture
     */
    @FXML
    public void onMouseExit() {

        Scene scene = this.myProfileButton.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

    /**
     * Method called when the user logs in
     */
    public void showNotificationAlert() {

        int eventSize = this.currentPage.getEvents().size();

        int messageSize = this.currentPage.getMessages().size();

        int requestSize = this.currentPage.getRequests().size();

        int total = eventSize + messageSize + requestSize;

        String title = "You have " +  total + " new notifications!";

        String text = "";

        if (eventSize != 0)

            text += "There are " + eventSize + " upcoming events!\n";

        if (messageSize != 0)

            text += "You have received " + messageSize + " new messages!\n";

        if (requestSize != 0)

            text += requestSize + " new users want to be friends with you!\n";

        if (!text.equals(""))

            Notifications.create()
                    .title(title)
                    .text(text)
                    .graphic(null)
                    .hideAfter(Duration.seconds(10))
                    .position(Pos.BOTTOM_RIGHT)
                    .showInformation();
    }
}
