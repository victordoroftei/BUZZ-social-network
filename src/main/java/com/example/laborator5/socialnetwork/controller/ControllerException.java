package com.example.laborator5.socialnetwork.controller;

/**
 * This class extends RunTimeException and stores the exceptions which can appear in service
 */
public class ControllerException extends RuntimeException {

    /**
     * Constructor
     *
     * @param message - String, representing the error message
     */
    public ControllerException(String message) {

        super(message);
    }
}
