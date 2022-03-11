package com.example.laborator5.socialnetwork.utils;

import javafx.stage.Stage;

import java.time.LocalDateTime;

/**
 * Global Variable class.
 */
public final class GlobalVariable {

    /**
     * Boolean variable representing whether the new messages have been seen.
     */
    private static boolean seenNewMessages = false;

    /**
     * Boolean variable representing whether the new requests have been seen.
     */
    private static boolean seenNewRequests = false;

    /**
     * Variable for retaining the stage of the main page
     */
    private static Stage mainPageStage = null;

    /**
     * The date when the user last entered the message menu.
     */
    private static LocalDateTime messageLast = null;

    /**
     * The date when the user last entered the request menu.
     */
    private static LocalDateTime requestLast = null;

    /**
     * Getter method for the date when the user last entered the message menu.
     *
     * @return the date
     */
    public static LocalDateTime getMessageLast() {
        return messageLast;
    }

    /**
     * Setter method for the date when the user last entered the message menu.
     *
     * @param messageLast the new date
     */
    public static void setMessageLast(LocalDateTime messageLast) {
        GlobalVariable.messageLast = messageLast;
    }

    /**
     * Getter method for the date when the user last entered the request menu.
     *
     * @return the date
     */
    public static LocalDateTime getRequestLast() {
        return requestLast;
    }

    /**
     * Setter method for the date when the user last entered the request menu.
     *
     * @param requestLast the new date
     */
    public static void setRequestLast(LocalDateTime requestLast) {
        GlobalVariable.requestLast = requestLast;
    }

    /**
     * Getter method for the message variable.
     *
     * @return true or false
     */
    public static boolean isSeenNewMessages() {

        return seenNewMessages;
    }

    /**
     * Setter method for the messages.
     *
     * @param seenNewMessages the new value
     */
    public static void setSeenNewMessages(boolean seenNewMessages) {

        GlobalVariable.seenNewMessages = seenNewMessages;
    }

    /**
     * Getter method for the request variable.
     *
     * @return true or false
     */
    public static boolean isSeenNewRequests() {

        return seenNewRequests;
    }

    /**
     * Setter method for the requests.
     *
     * @param seenNewRequests the new value
     */
    public static void setSeenNewRequests(boolean seenNewRequests) {

        GlobalVariable.seenNewRequests = seenNewRequests;
    }

    /**
     * Getter method for the stage
     *
     * @return the stage
     */
    public static Stage getMainPageStage() {

        return mainPageStage;
    }

    /**
     * Settter method for the main page stage
     *
     * @param mainPageStage - the main page stage
     */
    public static void setMainPageStage(Stage mainPageStage) {

        GlobalVariable.mainPageStage = mainPageStage;
    }

}
