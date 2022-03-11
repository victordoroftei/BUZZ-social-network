package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.EventDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.utils.EntitiesOnPage;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for the Event Menu.
 */
public class EventController implements Observer, Initializable {

    /**
     * The main box of the window.
     */
    @FXML
    private HBox mainBox;

    /**
     * Date picker for the date of an event.
     */
    @FXML
    private DatePicker eventDatePicker;

    /**
     * Field for the name of an event.
     */
    @FXML
    private TextField nameField;

    /**
     * Field for the description of an event.
     */
    @FXML
    private TextArea descriptionField;

    /**
     * ListView for the events.
     */
    @FXML
    private ListView<EventDTO> eventListView;

    /**
     * Button for toggling notifications for a selected event.
     */
    @FXML
    private Button notificationButton;

    /**
     * The list of events.
     */
    private List<EventDTO> eventList;

    /**
     * The super service of the controller.
     */
    private SuperService superService;

    /**
     * The user that is currently logged in.
     */
    private UserDTO currentUser;

    /**
     * The label for the page index.
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
     * The button for deleting an event.
     */
    @FXML
    private Button deleteEventButton;

    /**
     * Method for initializing the constructor of the controller class.
     *
     * @param location  the location
     * @param resources the resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // This if is here to check whether we're initializing the Controller from the main events window
        // or the event participants window.
        if (this.eventDatePicker != null) {
            this.eventDatePicker.setDayCellFactory(param -> new DateCell() {

                @Override
                public void updateItem(LocalDate date, boolean empty) {

                    super.updateItem(date, empty);
                    setDisable(empty || date.compareTo(LocalDate.now()) < 0);
                }
            });
        }
    }

    /**
     * Setter method for the attributes of the controller.
     *
     * @param superService the super service of the controller
     * @param currentUser  the user that is currently logged in
     * @param eventList    the list of events
     */
    public void setAttributes(SuperService superService, UserDTO currentUser, List<EventDTO> eventList) {

        this.superService = superService;
        this.currentUser = currentUser;
        this.eventList = eventList;

        this.descriptionField.setWrapText(true);

        this.superService.getEventService().addObserver(this);

        this.currentListPage = 0;

        Stage stage = (Stage) this.labelPage.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

        this.deleteEventButton.setDisable(true);
        this.notificationButton.setDisable(true);

        this.loadListData();

    }

    /**
     * Method used for updating the data about pages.
     */
    private void updatePages(int value) {

        if (value != -1)
            this.currentListPage = value;

        int maxSize = this.superService.getAllEvents(-1).size();

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
     * Method for loading the data of the events list.
     */
    @FXML
    public void loadListData() {

        this.eventList = this.superService.getAllEvents(this.currentListPage);

        this.updatePages(-1);

        this.eventListView.getItems().setAll(this.eventList);

    }

    /**
     * Method called when selecting an event.
     */
    @FXML
    public void onEventSelected() {

        EventDTO event = this.eventListView.getSelectionModel().getSelectedItem();

        if (event != null) {

            this.deleteEventButton.setDisable(!event.getOrganizer().equals(this.currentUser));

            if (event.getParticipants().contains(this.currentUser)) {

                this.notificationButton.setDisable(false);

                if (this.superService.getNotificationsForUser(event, this.currentUser))
                    this.notificationButton.setText("Turn Off Notifications");

                else
                    this.notificationButton.setText("Turn On Notifications");
            } else

                this.notificationButton.setDisable(true);

            if (event.getOrganizer().equals(this.currentUser)) {

                this.nameField.setText(event.getName());
                this.descriptionField.setText(event.getDescription());

                this.eventDatePicker.setValue(LocalDate.of(event.getDate().getYear(), event.getDate().getMonth().getValue(), event.getDate().getDayOfMonth()));
            } else {

                this.nameField.clear();
                this.descriptionField.clear();
                this.eventDatePicker.setValue(null);
            }
        }

    }

    /**
     * Method called when clicking the Add Event button.
     */
    @FXML
    public void onAddEventButtonClicked() {

        try {

            String name = this.nameField.getText();
            String description = this.descriptionField.getText();
            LocalDate date = this.eventDatePicker.getValue();

            if (name.equals(""))
                throw new ControllerException("The name of the event cannot be empty!");

            if (description.equals(""))
                throw new ControllerException("The name of the description cannot be empty!");

            if (date == null)
                throw new ControllerException("You must insert a date for the event!");

            LocalDateTime dateTime = date.atTime(12, 0);

            this.nameField.clear();
            this.descriptionField.clear();
            this.eventDatePicker.setValue(null);

            this.updatePages(0);

            this.superService.addEvent(new EventDTO(null, name, this.currentUser, description, dateTime, null));

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event Added!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method called when clicking the Update Event button.
     */
    @FXML
    public void onUpdateEventButtonClick() {

        try {

            EventDTO eventDTO = this.eventListView.getSelectionModel().getSelectedItem();
            String name = this.nameField.getText();
            String description = this.descriptionField.getText();
            LocalDate date = this.eventDatePicker.getValue();

            if (eventDTO == null)
                throw new ControllerException("No event was selected!");

            if (name.equals(""))
                throw new ControllerException("The name of the event cannot be empty!");

            if (description.equals(""))
                throw new ControllerException("The name of the description cannot be empty!");

            if (date == null)
                throw new ControllerException("You must insert a date for the event!");

            LocalDateTime dateTime = date.atTime(12, 0);

            this.nameField.clear();
            this.descriptionField.clear();
            this.eventDatePicker.setValue(null);

            this.superService.updateEvent(new EventDTO(eventDTO.getId(), name, this.currentUser, description, dateTime, eventDTO.getParticipants()));

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event Updated!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method called when clicking the Delete Event button.
     */
    @FXML
    public void onDeleteEventButtonClick() {

        try {

            EventDTO eventDTO = this.eventListView.getSelectionModel().getSelectedItem();
            if (eventDTO == null)
                throw new ControllerException("No event was selected!");

            if (!eventDTO.getOrganizer().equals(this.currentUser))
                throw new ControllerException("You cannot delete an event that you did not create!");

            this.updatePages(0);

            this.superService.deleteEvent(eventDTO);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event Deleted!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method called when clicking the Add Participant button.
     */
    @FXML
    public void onAddParticipantButtonClick() {

        try {

            EventDTO eventDTO = this.eventListView.getSelectionModel().getSelectedItem();
            if (eventDTO == null)
                throw new ControllerException("No event was selected!");

            this.superService.addParticipant(eventDTO, this.currentUser);

            this.notificationButton.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Participant Added!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method called when clicking the Delete Participant button.
     */
    @FXML
    public void onDeleteParticipantButtonClick() {

        try {

            EventDTO eventDTO = this.eventListView.getSelectionModel().getSelectedItem();
            if (eventDTO == null)
                throw new ControllerException("No event was selected!");

            this.superService.deleteParticipant(eventDTO, this.currentUser);

            this.notificationButton.setDisable(true);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Participant Deleted!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Method called when clicking the View Participants button.
     */
    @FXML
    public void onViewParticipantsClick() {

        try {

            EventDTO eventDTO = this.eventListView.getSelectionModel().getSelectedItem();
            if (eventDTO == null)
                throw new ControllerException("No event was selected!");

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("participant-view.fxml"));

            Parent root = fxmlLoader.load();

            ParticipantsController participantsController = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            participantsController.setAttributes(eventDTO.getName(), eventDTO.getParticipants());

            stage.show();
        } catch (ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

    }

    /**
     * Method called when clicking the back button on the events window.
     */
    @FXML
    public void onBackButtonClick() {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("page-view.fxml"));

            Parent root = fxmlLoader.load();

            PageController pc = fxmlLoader.getController();

            this.superService.getEventService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            pc.setAttributes(this.superService, this.superService.getPageForUser(this.currentUser));

            Stage stage2 = (Stage) this.nameField.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException ex) {

            ex.printStackTrace();
        }

    }

    /**
     * Method called when clicking the button for toggling notifications.
     */
    @FXML
    public void onNotificationsButtonClick() {

        try {

            EventDTO eventDTO = this.eventListView.getSelectionModel().getSelectedItem();
            if (eventDTO == null)
                throw new ControllerException("No event was selected!");

            boolean status = this.superService.getNotificationsForUser(eventDTO, this.currentUser);

            this.superService.setNotificationsForUser(eventDTO, this.currentUser, !status);

            if (status)
                this.notificationButton.setText("Turn On Notifications");

            else
                this.notificationButton.setText("Turn Off Notifications");

        } catch (ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
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
     * Overridden update button for observers.
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
