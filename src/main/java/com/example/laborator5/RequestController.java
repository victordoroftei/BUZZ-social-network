package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.domain.User;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.utils.CustomHBox;
import com.example.laborator5.socialnetwork.utils.EntitiesOnPage;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.RequestDTO;

import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
 * Controller class for the requests menu.
 */
public class RequestController implements Observer {

    /**
     * TableView object for the requests table
     */
    @FXML
    private TableView<RequestDTO> tableView;

    /**
     * TableColumn object for the username of the sender of the request
     */
    @FXML
    private TableColumn<RequestDTO, String> username1Col;

    /**
     * TableColumn object for the username of the receiver of the request
     */
    @FXML
    private TableColumn<RequestDTO, String> username2Col;

    /**
     * TableColumn object for the date when the request was sent
     */
    @FXML
    private TableColumn<RequestDTO, String> dateCol;

    /**
     * TableColumn object for the status of the request
     */
    @FXML
    private TableColumn<RequestDTO, String> statusCol;

    /**
     * The search bar for searching users.
     */
    @FXML
    private ComboBox<CustomHBox> searchBar;

    /**
     * Button for sending a request
     */
    @FXML
    private Button sendRequestButton;

    /**
     * Button for accepting a request
     */
    @FXML
    private Button acceptRequestButton;

    /**
     * Button for deleting a request
     */
    @FXML
    private Button deleteRequestButton;

    /**
     * Button for rejecting a request
     */
    @FXML
    private Button rejectRequestButton;

    /**
     * Button for returning to the friendship menu.
     */
    @FXML
    private Button backButton;

    /**
     * The super service of the controller
     */
    private SuperService superService;

    /**
     * The user that is currently logged in
     */
    private UserDTO currentUser;

    /**
     * The label for the page index
     */
    @FXML
    private Label labelPage;

    /**
     * The list of users
     */
    private List<UserDTO> users;

    /**
     * The current page from the table.
     */
    private int currentTablePage;

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
     * Setter method for the attributes of this class.
     *
     * @param superService the super service of the controller
     * @param currentUser  the user that is currently logged in
     */
    public void setAttributes(SuperService superService, UserDTO currentUser, List<UserDTO> users) {

        this.users = users;
        this.superService = superService;
        this.currentUser = currentUser;
        this.superService.getRequestService().addObserver(this);

        this.searchBar.setPromptText("Search for a user");
        this.searchBar.setEditable(true);

        this.currentTablePage = 0;

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });

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

        this.loadTableData();

    }

    /**
     * Method used for updating the data about pages.
     */
    private void updatePages(int value) {

        if (value != -1)
            this.currentTablePage = value;

        int maxSize = this.superService.getAllRequestsForAUser(this.currentUser, -1).size();

        if (maxSize % EntitiesOnPage.getNumber() == 0)
            this.maxPages = maxSize / EntitiesOnPage.getNumber();

        else
            this.maxPages = maxSize / EntitiesOnPage.getNumber() + 1;

        if (maxSize == 0)

            this.labelPage.setText("0/0");

        else
            this.labelPage.setText(String.valueOf(this.currentTablePage + 1) + "/" + this.maxPages);

    }

    /**
     * Method for loading the data from the request repository into the table view.
     */
    @FXML
    public void loadTableData() {

        List<RequestDTO> list = this.superService.getAllRequestsForAUser(this.currentUser, this.currentTablePage);
        //List<RequestDTO> list = this.currentPage.getRequests();

        username1Col.setCellValueFactory(new PropertyValueFactory<>("User1"));
        username2Col.setCellValueFactory(new PropertyValueFactory<>("User2"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("Status"));

        this.updatePages(-1);

        this.tableView.getItems().setAll(list);

    }

    /**
     * Controller method for sending a request.
     */
    @FXML
    public void onSendRequestButtonClick() {

        try {

            CustomHBox selectedBox = (CustomHBox) this.searchBar.getSelectionModel().getSelectedItem();
            if (selectedBox == null)
                throw new ControllerException("You must select a user!");

            String content = selectedBox.getUserDTO().getUserName();

            this.updatePages(0);

            this.superService.addRequest(new RequestDTO(this.currentUser, new UserDTO(0L, "x", "x", content), LocalDateTime.now(), "pending"));

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Request Sent!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        }

    }

    /**
     * Controller method for accepting a request.
     */
    @FXML
    public void onAcceptRequestButtonClick() {

        try {

            RequestDTO rd = tableView.getSelectionModel().getSelectedItem();

            if (rd.getUser1().getUserName().equals(this.currentUser.getUserName()))
                throw new ServiceException("You cannot accept a request that you've sent!");

            this.updatePages(0);

            this.superService.acceptRequest(rd);

            //this.loadTableData();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Request Accepted!");
            alert.showAndWait();
        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        } catch (NullPointerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "You must select a request!");
            alert.showAndWait();
        }

    }

    /**
     * Controller method for rejecting a request.
     */
    @FXML
    public void onRejectRequestButtonClick() {

        try {

            RequestDTO rd = tableView.getSelectionModel().getSelectedItem();

            if (rd.getUser1().getUserName().equals(this.currentUser.getUserName()))
                throw new ServiceException("You cannot reject a request that you've sent!");

            this.updatePages(0);

            this.superService.rejectRequest(rd);

            //this.loadTableData();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Request Rejected!");
            alert.showAndWait();
        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        } catch (NullPointerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "You must select a request!");
            alert.showAndWait();
        }

    }

    /**
     * Controller method for deleting a request.
     */
    @FXML
    public void onDeleteRequestButtonClick() {

        try {
            RequestDTO rd = tableView.getSelectionModel().getSelectedItem();

            if (rd.getUser2().getUserName().equals(this.currentUser.getUserName()))
                throw new ControllerException("You cannot delete a request that you've received!");

            this.updatePages(0);

            this.superService.deleteRequest(rd);

            //this.loadTableData();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Request Deleted!");
            alert.showAndWait();
        } catch (ValidationException | ServiceException | ControllerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.showAndWait();
        } catch (NullPointerException ex) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "You must select a request!");
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

            this.superService.getRequestService().removeObserver(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            GlobalVariable.setRequestLast(LocalDateTime.now());

            pc.setAttributes(this.superService, this.superService.getPageForUser(this.currentUser));

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.close();

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Method called when clicking the previous page button.
     */
    @FXML
    public void onPreviousPageButtonClick() {

        if (this.currentTablePage > 0) {

            this.currentTablePage--;

            this.labelPage.setText(String.valueOf(this.currentTablePage + 1) + "/" + this.maxPages);

            this.acceptRequestButton.setDisable(false);
            this.rejectRequestButton.setDisable(false);
            this.deleteRequestButton.setDisable(false);

            this.update();
        }

    }

    /**
     * Method called when clicking the next page button.
     */
    @FXML
    public void onNextPageButtonClick() {

        if (this.currentTablePage < this.maxPages - 1) {

            this.currentTablePage++;

            this.labelPage.setText(String.valueOf(this.currentTablePage + 1) + "/" + this.maxPages);

            this.acceptRequestButton.setDisable(false);
            this.rejectRequestButton.setDisable(false);
            this.deleteRequestButton.setDisable(false);

            this.update();
        }

    }

    /**
     * Method called when clicking a row in the table.
     */
    @FXML
    public void onRequestSelected() {

        RequestDTO request = this.tableView.getSelectionModel().getSelectedItem();

        if (request != null) {

            if (request.getUser1().equals(this.currentUser)) {

                this.acceptRequestButton.setDisable(true);
                this.rejectRequestButton.setDisable(true);
                this.deleteRequestButton.setDisable(false);
            }

            else {

                this.acceptRequestButton.setDisable(false);
                this.rejectRequestButton.setDisable(false);
                this.deleteRequestButton.setDisable(true);
            }
        }

        else {

            this.acceptRequestButton.setDisable(false);
            this.rejectRequestButton.setDisable(false);
            this.deleteRequestButton.setDisable(false);
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
     * This method is called when an observable calls updateAll function
     */
    @Override
    public void update() {

        this.loadTableData();
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

        }
        this.searchBar.setItems(FXCollections.observableArrayList(list));
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
