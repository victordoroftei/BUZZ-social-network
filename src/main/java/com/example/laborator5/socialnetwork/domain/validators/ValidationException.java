package com.example.laborator5.socialnetwork.domain.validators;

/**
 * ValidationException contains the errors which can occur while validating an entity
 */
public class ValidationException extends RuntimeException {

    /**
     * Default constructor
     */
    public ValidationException() {
    }

    /**
     * Constructor using a string
     *
     * @param message - String, representing the error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructor using a string and a throwable
     *
     * @param message - String, representing the error message
     * @param cause   - Throwable, the cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor using a throwable
     *
     * @param cause - Throwable, the cause
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor using string, Throwable and 2 booleans
     *
     * @param message            - String, representing the error message
     * @param cause              - Throwable, the cause
     * @param enableSuppression  - boolean
     * @param writableStackTrace - boolean
     */
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}