package com.example.laborator5.socialnetwork.service;

/**
 * This class extends RunTimeException and stores the exceptions which can appear in service
 */
public class ServiceException extends RuntimeException {

    /**
     * Constructor
     *
     * @param message - String, representing the error message
     */
    public ServiceException(String message) {

        super(message);
    }
}
