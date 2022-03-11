package com.example.laborator5;

import com.example.laborator5.socialnetwork.controller.ControllerException;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.service.ServiceException;
import com.example.laborator5.socialnetwork.service.SuperService;
import com.example.laborator5.socialnetwork.service.dto.FriendshipDTO;
import com.example.laborator5.socialnetwork.service.dto.MessageDTO;
import com.example.laborator5.socialnetwork.service.dto.Page;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReportController {

    /**
     * The super service of the controller.
     */
    private SuperService superService;

    /**
     * The user that is currently logged in.
     */
    private UserDTO currentUser;

    /**
     * The root of the window
     */
    @FXML
    private VBox mainBox;

    /**
     * The button for browsing the location.
     */
    @FXML
    private Button browseLocationButton;

    /**
     * The text field for the path.
     */
    @FXML
    private TextField pathTextField;

    /**
     * The date picker for the start date.
     */
    @FXML
    private DatePicker fromDatePicker;

    /**
     * The date picker for the end date.
     */
    @FXML
    private DatePicker toDatePicker;

    /**
     * The back button for returning to the main menu
     */
    @FXML
    private Button backButton;

    /**
     * The text field used for entering the username
     */
    @FXML
    private TextField usernameTextField;

    /**
     * Setter method for the attributes of this class.
     *
     * @param superService the super service of the controller
     * @param currentUser  the user that is currently logged in
     */
    public void setAttributes(SuperService superService, UserDTO currentUser) {

        this.superService = superService;
        this.currentUser = currentUser;

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
     * Method called when clicking the button for browsing a location.
     */
    @FXML
    public void onBrowseLocationButtonClick() {

        DirectoryChooser directoryChooser = new DirectoryChooser();

        Stage stage = (Stage) this.browseLocationButton.getScene().getWindow();

        File file = directoryChooser.showDialog(stage);

        if (file != null)

            this.pathTextField.setText(file.toString());

        else

            this.pathTextField.setText("No folder was selected!");

    }

    /**
     * Method called when clicking the button for generating a PDF Activity Report.
     */
    @FXML
    public void onGenerateActivityReportButtonClick() {

        try {

            String path = this.pathTextField.getText();
            this.pathTextField.clear();

            LocalDate fromDate = this.fromDatePicker.getValue();

            LocalDate toDate = this.toDatePicker.getValue();

            if (fromDate == null)
                throw new ControllerException("You must select a start date for the report!");

            if (toDate == null)
                throw new ControllerException("You must select an end date for the report!");

            if (fromDate.compareTo(toDate) > 0)
                throw new ControllerException("The start date cannot be greater than the end date!");

            List<FriendshipDTO> friendshipList = this.superService.getAllFriendshipsForAUser(this.currentUser);
            List<MessageDTO> messageList = this.superService.getAllMessagesForAUser(this.currentUser);

            Font font = new Font(Font.TIMES_ROMAN, 14);
            Font fontBold = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
            Font bigFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);

            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + "\\ActivityReport_" + this.currentUser.getUserName() + ".pdf"));

            document.open();

            friendshipList.sort(Comparator.comparing(FriendshipDTO::getDate, Comparator.reverseOrder()));

            List<UserDTO> newFriendsList = new ArrayList<>();
            List<MessageDTO> newMessageList = new ArrayList<>();

            for (FriendshipDTO f : friendshipList) {

                if (f.getDate().toLocalDate().compareTo(fromDate) >= 0 && f.getDate().toLocalDate().compareTo(toDate) <= 0) {

                    if (f.getUser2().equals(this.currentUser))
                        newFriendsList.add(f.getUser1());

                    else
                        newFriendsList.add(f.getUser2());
                }
            }

            for (MessageDTO m : messageList) {

                if (m.getTo().contains(this.currentUser) && m.getDate().toLocalDate().compareTo(fromDate) >= 0 && m.getDate().toLocalDate().compareTo(toDate) <= 0)
                    newMessageList.add(m);
            }

            newMessageList.sort(Comparator.comparing(MessageDTO::getDate, Comparator.reverseOrder()));

            Paragraph title = new Paragraph("Activity Report\n\n", bigFont);
            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            Paragraph genBy = new Paragraph("Generated by: " + this.currentUser.toString(), font);

            document.add(genBy);

            Paragraph dateInt = new Paragraph("Date interval: " + fromDate + " - " + toDate, font);

            document.add(dateInt);

            Paragraph newFriends;

            if (newFriendsList.size() == 0)
                newFriends = new Paragraph("\nYou have made no new friends in the selected interval!", font);

            else {

                String string = "\nYou have made " + newFriendsList.size() + " new friends!\nThe new friends are:\n";

                for (UserDTO u : newFriendsList)
                    string += u.toString() + "\n";

                newFriends = new Paragraph(string, font);
            }

            document.add(newFriends);

            Paragraph newMessage;
            if (newMessageList.size() == 0)
                newMessage = new Paragraph("\nYou have received no messages in the selected interval!", font);

            else {

                String string = "\nYou have received " + newMessageList.size() + " new messages!\nThe new received messages are:\n";

                for (MessageDTO m : newMessageList)
                    string += m.toString() + "\n\n";

                newMessage = new Paragraph(string, font);
            }

            document.add(newMessage);

            document.close();
            writer.close();

            this.fromDatePicker.setValue(null);
            this.toDatePicker.setValue(null);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Activity Report Generated!");
            alert.showAndWait();

        } catch (ServiceException | ValidationException | ControllerException err) {

            Alert alert = new Alert(Alert.AlertType.ERROR, err.getMessage());
            alert.showAndWait();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }
    }

    /**
     * The role of this method is to generate the message report between our user and a specific user
     */
    @FXML
    public void onGenerateMessageReportButtonClick() {

        try {

            String path = this.pathTextField.getText();

            if (path == null || path.equals(""))

                throw new ControllerException("No path selected!");

            String username = this.usernameTextField.getText();

            LocalDate fromDate = this.fromDatePicker.getValue();

            LocalDate toDate = this.toDatePicker.getValue();

            String dateErrors = "";

            if (fromDate == null)

                dateErrors += "You must select a start date for the message report!\n";

            if (toDate == null)

                dateErrors += "You must select an end date for the message report!\n";

            if (!dateErrors.equals(""))

                throw new ControllerException(dateErrors);

            if (fromDate.compareTo(toDate) > 0)

                throw new ControllerException("The start date cannot be greater than the end date!\n");

            Predicate<MessageDTO> predicate = x -> !(x.getDate().toLocalDate().isBefore(fromDate) || x.getDate().toLocalDate().isAfter(toDate));

            List<MessageDTO> messageDTOList = this.superService.getConversations(this.currentUser, new UserDTO(null, null, null, username)).stream().filter(predicate).collect(Collectors.toList());

            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path + "\\MessageReport_" + this.currentUser.getUserName() + ".pdf"));

            document.open();

            Font font = new Font(Font.TIMES_ROMAN, 14);
            Font boldFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD);
            Font titleFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);

            // Title
            Paragraph title = new Paragraph("Messages report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Generated by:
            Paragraph generated = new Paragraph("\n\nGenerated by: ", boldFont);
            Paragraph generatedContent = new Paragraph(this.currentUser.toString(), font);
            generated.add(generatedContent);
            document.add(generated);

            // The name of the user:
            Paragraph userName = new Paragraph("The username of the user: ", boldFont);
            Paragraph userNameContent = new Paragraph(username, font);
            userName.add(userNameContent);
            document.add(userName);

            // Date interval:
            Paragraph dateInterval = new Paragraph("Date interval: ", boldFont);
            Paragraph dateIntervalContent = new Paragraph(fromDate + " - " + toDate, font);
            dateInterval.add(dateIntervalContent);
            document.add(dateInterval);

            // The conversation has x messages.
            Paragraph part1 = new Paragraph("The conversation has ", font);
            Paragraph number = new Paragraph(String.valueOf(messageDTOList.size()), boldFont);
            Paragraph part2 = new Paragraph(" messages.", font);
            number.add(part2);
            part1.add(number);
            document.add(part1);

            // The conversation is:
            Paragraph conversation = new Paragraph("The conversation is: ", boldFont);
            document.add(conversation);

            // We are going to print the messages list
            messageDTOList.forEach(x -> {

                Paragraph paragraph;

                if (x.getFrom().getUserName().equals(this.currentUser.getUserName())) {

                    paragraph = new Paragraph(x.toString(), font);
                    paragraph.setAlignment(Element.ALIGN_RIGHT);

                } else {

                    paragraph = new Paragraph(x.toString(), font);
                    paragraph.setAlignment(Element.ALIGN_LEFT);

                }

                document.add(paragraph);

            });

            this.pathTextField.clear();

            this.usernameTextField.clear();

            this.fromDatePicker.setValue(null);
            this.toDatePicker.setValue(null);

            document.close();
            writer.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Message report generated!");
            alert.showAndWait();

        } catch (ValidationException | ServiceException | ControllerException err) {

            Alert alert = new Alert(Alert.AlertType.ERROR, err.getMessage());
            alert.showAndWait();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

    }

    /**
     * Controller method for returning to the friendships menu.
     */
    @FXML
    public void onBackButtonClick() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("page-view.fxml"));

            Parent root = fxmlLoader.load();

            PageController pc = fxmlLoader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Socialnetwork");
            stage.getIcons().add(new Image("file:icon.jpg"));
            stage.setScene(scene);

            pc.setAttributes(this.superService, this.superService.getPageForUser(this.currentUser));

            Stage stage2 = (Stage) this.backButton.getScene().getWindow();
            stage2.close();

            stage.show();
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
