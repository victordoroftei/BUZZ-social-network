package com.example.laborator5;

import com.example.laborator5.socialnetwork.Main;
import com.example.laborator5.socialnetwork.domain.*;
import com.example.laborator5.socialnetwork.domain.validators.*;
import com.example.laborator5.socialnetwork.repository.Repository;
import com.example.laborator5.socialnetwork.repository.database.*;
import com.example.laborator5.socialnetwork.repository.paging.PagingRepository;
import com.example.laborator5.socialnetwork.service.*;
import com.example.laborator5.socialnetwork.utils.EntitiesOnPage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class starts the GUI of our applciation
 */
public class GUI extends Application {

    /**
     * This method starts the application
     *
     * @param stage - the stage which will be shown
     */
    @Override
    public void start(Stage stage) {

        stage.initStyle(StageStyle.UNDECORATED);

        EntitiesOnPage.setNumber(3);

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("main.properties")) {

            Properties properties = new Properties();

            if (input == null) {

                System.out.println("Error reading from properties file!");
                return;
            }

            properties.load(input);

            System.setProperty("jdbc_url", properties.getProperty("db.url"));

            FriendshipValidator friendshipValidator = new FriendshipValidator();
            UserValidator validator = new UserValidator();
            RequestValidator requestValidator = new RequestValidator();
            MessageValidator messageValidator = new MessageValidator();
            EventValidator eventValidator = new EventValidator();

            Repository<Long, User> repositoryUserDB = new UserDB();
            Repository<Tuple<Long, Long>, Friendship> repositoryFriendshipDB = new FriendshipDB();
            PagingRepository<Long, Request> repositoryRequestDB = new RequestDB();
            PagingRepository<Long, Message> repositoryMessageDB = new MessageDB();
            PagingRepository<Long, Event> repositoryEventDB = new EventDB();
            EventParticipantsDB repositoryEventParticipantsDB = new EventParticipantsDB();
            Repository<Long, Profile> repositoryProfileDB = new ProfileDB();
            Repository<Long, Post> repositoryPostDB = new PostDB();

            UserService userService = new UserService(repositoryUserDB, validator);
            FriendshipService friendshipService = new FriendshipService(repositoryFriendshipDB, friendshipValidator);
            RequestService requestService = new RequestService(repositoryRequestDB, requestValidator);
            MessageService messageService = new MessageService(repositoryMessageDB, messageValidator);
            EventService eventService = new EventService(repositoryEventDB, repositoryEventParticipantsDB, eventValidator);
            ProfileService profileService = new ProfileService(repositoryProfileDB);
            PostService postService = new PostService(repositoryPostDB);

            SuperService superService = new SuperService(userService, friendshipService, requestService, messageService, eventService, profileService, postService);

            FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Socialnetwork");
            stage.setScene(scene);
            stage.getIcons().add(new Image("file:icon.jpg"));

            Controller controller = fxmlLoader.getController();

            controller.setSuperService(superService);

            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    /**
     * This is the method we call from main which runs our application
     */
    public static void runApplication() {

        launch();
    }
}