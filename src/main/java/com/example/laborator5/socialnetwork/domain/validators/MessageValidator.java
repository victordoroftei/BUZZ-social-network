package com.example.laborator5.socialnetwork.domain.validators;

import com.example.laborator5.socialnetwork.domain.Message;

/**
 * The role of this class is to validate a message
 */
public class MessageValidator implements Validator<Message> {

    /**
     * This method validates a message
     *
     * @param entity the entity that needs to be validated
     * @throws ValidationException if the entity is not valid
     */
    @Override
    public void validate(Message entity) throws ValidationException {

        String error = "";

        if (entity.getId() == null)

            error += "Id can't be null!\n";

        else if (entity.getId() <= 0)

            error += "Id can't be less or equal than 0!\n";

        if (entity.getFrom() == null)

            error += "From can't be null!\n";

        if (entity.getMessage() == null)

            error += "Message can't be null!\n";

        else if (entity.getMessage().isEmpty())

            error += "Message can't be empty!\n";

        if (entity.getTo() == null)

            error += "To list can't be null!\n";

        else if (entity.getTo().isEmpty())

            error += "There must be at least a receiver!\n";

        if (!error.equals(""))

            throw new ValidationException(error);
    }
}
