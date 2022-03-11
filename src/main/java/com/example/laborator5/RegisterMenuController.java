package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.example.laborator5.socialnetwork.service.dto.UserRegisterDTO;
import com.example.laborator5.socialnetwork.utils.MD5;
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
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * This class handles the Register Menu window
 */
public class RegisterMenuController implements Initializable {

    /**
     * This field represents the connection to the super service
     */
    private SuperService superService;

    /**
     * The father of all the widgets
     */
    @FXML
    private HBox mainBox;

    /**
     * This is the object that collects the first name of a user
     */
    @FXML
    private TextField firstNameField;

    /**
     * This is the object that collects the last name of a user
     */
    @FXML
    private TextField lastNameField;

    /**
     * This is the object that collects the username of a user
     */
    @FXML
    private TextField usernameField;

    /**
     * This is the button which a user presses when he wants to register into our application
     */
    @FXML
    private Button registerButton;

    /**
     * This is the button which a user presses when he wants to go back to the log in menu
     */
    @FXML
    private Button backButton;

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
     * PasswordField object for the confirmation for the password input
     */
    @FXML
    private PasswordField confirmPasswordTextField;

    /**
     * TextField object for the confirmation for the password input
     */
    @FXML
    private TextField confirmPasswordTextFieldVisible;

    /**
     * This label is used for showing if the password match or not
     */
    @FXML
    private Label errorLabel;

    /**
     * This method is called to set the connection to the super service from the login controller
     *
     * @param service - the connection to the super service
     */
    public void setParametersForRegister(SuperService service) {

        this.superService = service;

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
     * This method is called when the user presses the back button
     */
    @FXML
    public void onBackButtonClicked() {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));

        try {

            Parent flowPane = loader.load();

            Controller controller = loader.getController();

            Scene scene = new Scene(flowPane);

            Stage stage = new Stage();

            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            controller.setSuperService(this.superService);

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.close();

            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the user presses the register button which tries to create him an account
     */
    @FXML
    public void onRegisterButtonClicked() {

        try {

            String firstName = this.firstNameField.getText();
            String lastName = this.lastNameField.getText();
            String username = this.usernameField.getText();

            String password;
            if (!this.passwordTextField.isVisible())
                password = MD5.getMD5(this.passwordTextFieldVisible.getText());

            else
                password = MD5.getMD5(this.passwordTextField.getText());

            String passwordConfirm;
            if (!this.confirmPasswordTextField.isVisible())
                passwordConfirm = MD5.getMD5(this.confirmPasswordTextFieldVisible.getText());

            else
                passwordConfirm = MD5.getMD5(this.confirmPasswordTextField.getText());

            if (!passwordConfirm.equals(password))
                throw new ControllerException("The passwords do not match!");

            this.firstNameField.clear();
            this.lastNameField.clear();
            this.usernameField.clear();

            this.passwordTextField.clear();
            this.passwordTextFieldVisible.clear();

            this.confirmPasswordTextField.clear();
            this.confirmPasswordTextFieldVisible.clear();

            UserDTO u1 = new UserDTO(null, firstName, lastName, username);
            this.superService.addUser(new UserRegisterDTO(u1, password));

            UserDTO u2 = new UserDTO(null, firstName, lastName, username);
            u2.setLastLogin(LocalDateTime.now());
            this.superService.updateUser(u1, u2);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Registered successfully!");
            alert.showAndWait();

            this.onBackButtonClicked();

        } catch (ValidationException | ServiceException | ControllerException err) {

            Alert alert = new Alert(Alert.AlertType.ERROR, err.getMessage());
            alert.showAndWait();
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
     * Method called when pressing the button for showing or hiding the confirmation of the password.
     */
    @FXML
    public void onShowConfirmPasswordButtonClick() {

        if (this.confirmPasswordTextField.isVisible()) {

            this.confirmPasswordTextField.setVisible(false);
            this.confirmPasswordTextFieldVisible.setText(this.confirmPasswordTextField.getText());
            this.confirmPasswordTextField.clear();
            this.confirmPasswordTextFieldVisible.setVisible(true);
        } else {

            this.confirmPasswordTextFieldVisible.setVisible(false);
            this.confirmPasswordTextField.setText(this.confirmPasswordTextFieldVisible.getText());
            this.confirmPasswordTextFieldVisible.clear();
            this.confirmPasswordTextField.setVisible(true);
        }

    }

    /**
     * This method gets called each time the user types a letter inside the password confirmation field
     */
    @FXML
    public void passwordCompare() {

        String confirmContent = "";
        String normalContent = "";

        if (this.passwordTextField.isVisible())
            normalContent = this.passwordTextField.getText();

        else
            normalContent = this.passwordTextFieldVisible.getText();

        if (this.confirmPasswordTextField.isVisible())
            confirmContent = this.confirmPasswordTextField.getText();

        else
            confirmContent = this.confirmPasswordTextFieldVisible.getText();

        if (!normalContent.equals(confirmContent))
            this.errorLabel.setText("The passwords do not match!");

        else
            this.errorLabel.setText("");

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
    }

    /**
     * Method called when the yellow button is pressed
     */
    @FXML
    public void onMinimizeButtonClick() {

        Stage stage = (Stage) this.registerButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * This method closes our application and is called when we press exit button
     */
    @FXML
    public void onExitButtonClick() {

        Stage stage = (Stage) this.registerButton.getScene().getWindow();
        stage.close();
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
