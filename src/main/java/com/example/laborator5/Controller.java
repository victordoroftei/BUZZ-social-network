package com.example.laborator5;

import com.example.laborator5.socialnetwork.domain.validators.*;
import com.example.laborator5.socialnetwork.service.*;
import com.example.laborator5.socialnetwork.service.dto.Page;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.service.dto.UserRegisterDTO;
import com.example.laborator5.socialnetwork.utils.GlobalVariable;
import com.example.laborator5.socialnetwork.utils.MD5;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class represents the connection between service and GUI
 */
public class Controller implements Initializable {

    /**
     * The father of the widgets
     */
    @FXML
    private HBox mainBox;

    /**
     * TextField object for the username input
     */
    @FXML
    private TextField usernameTextField;

    /**
     * Login button which is pressed when a user wants to log in
     */
    @FXML
    private Button loginButton;

    /**
     * PasswordField object for the password input
     */
    @FXML
    private PasswordField passwordTextField;

    /**
     * TextField object for the password input when shown
     */
    @FXML
    private TextField passwordTextFieldVisible;

    /**
     * Register button which is pressed when a new User wants to register into our app
     */
    @FXML
    private Button registerButton;

    /**
     * The connection to the lower layer
     */
    private SuperService superService;

    /**
     * The id of the current logged-in user
     */
    private Long currentUser;

    /**
     * The username of the current user
     */
    private String currentUsername;

    /**
     * The constructor of Controller class
     */
    public Controller() {

        this.currentUser = 0L;
        this.currentUsername = null;

    }

    /**
     * Setter method for the super service of the Controller class.
     *
     * @param superService the new super service of the Controller class
     */
    public void setSuperService(SuperService superService) {

        this.superService = superService;

        Stage stage = (Stage) this.mainBox.getScene().getWindow();

        stage.initStyle(StageStyle.UNDECORATED);

        this.mainBox.setOnMousePressed(pressEvent -> {
            this.mainBox.setOnMouseDragged(dragEvent -> {
                stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }

    /**
     * This method is used for logging in a person
     */
    @FXML
    public void onLogInButtonClick() {

        String content = usernameTextField.getText();
        String pass;

        if (!this.passwordTextField.isVisible())
            pass = this.passwordTextFieldVisible.getText();

        else
            pass = this.passwordTextField.getText();

        usernameTextField.clear();
        passwordTextField.clear();

        try {

            this.currentUser = superService.loginUserUsingUsername(new UserRegisterDTO(new UserDTO(1L, "Fn", "Ln", content), MD5.getMD5(pass)));
            this.currentUsername = content;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("page-view.fxml"));
            HBox flowPane = loader.load();

            PageController controller = loader.getController();

            UserDTO userDTO = new UserDTO(this.currentUser, null, null, this.currentUsername);

            Page page = this.superService.getPageForUser(userDTO);

            GlobalVariable.setSeenNewMessages(false);
            GlobalVariable.setSeenNewRequests(false);

            Scene scene = new Scene(flowPane);

            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            controller.setAttributes(this.superService, page);
            this.superService.deleteOldEvents();

            Stage stage2 = (Stage) loginButton.getScene().getWindow();
            stage2.close();

            stage.show();

            controller.showNotificationAlert();

        } catch (ValidationException | ServiceException err) {
            Alert alert = new Alert(Alert.AlertType.ERROR, err.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
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
     * This method opens the register menu
     */
    @FXML
    public void onRegisterButtonClick() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("register-view.fxml"));

        try {

            Parent parent = loader.load();
            RegisterMenuController controller = loader.getController();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            Stage stage1 = (Stage) this.registerButton.getScene().getWindow();
            stage1.close();

            controller.setParametersForRegister(this.superService);

            stage.initStyle(StageStyle.UNDECORATED);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Method called when we press the eye icon for showing / hiding the current password
     */
    @FXML
    public void onShowPasswordButtonClick() {

        if (this.passwordTextField.isVisible()) {

            this.passwordTextField.setVisible(false);
            this.passwordTextFieldVisible.setText(this.passwordTextField.getText());
            this.passwordTextField.clear();
            this.passwordTextFieldVisible.setVisible(true);
        } else {

            this.passwordTextFieldVisible.setVisible(false);
            this.passwordTextField.setText(this.passwordTextFieldVisible.getText());
            this.passwordTextFieldVisible.clear();
            this.passwordTextField.setVisible(true);
        }
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
     * We had to implement this method because we implement initializable
     *
     * @param location  - the location
     * @param resources - the resource
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.passwordTextFieldVisible.setVisible(false);
        this.passwordTextField.setVisible(true);
        Platform.runLater(() -> this.loginButton.requestFocus());
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