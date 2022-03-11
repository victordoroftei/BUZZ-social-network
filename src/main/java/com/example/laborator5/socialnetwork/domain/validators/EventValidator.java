package com.example.laborator5.socialnetwork.domain.validators;

import com.example.laborator5.socialnetwork.domain.Event;

import java.time.LocalDateTime;

/**
 * The role of this class is to validate an event
 */
public class EventValidator implements Validator<Event> {

    /**
     * This mehod is responsible for validating an event
     *
     * @param entity the entity that needs to be validated
     * @throws ValidationException if the event is not valid
     */
    @Override
    public void validate(Event entity) throws ValidationException {

        String errors = "";

        if (entity.getName() == null)

            errors += "The name of the event can't be null!\n";

        else if (entity.getName().equals(""))

            errors += "The name of the event can't be empty!\n";

        if (entity.getDescription() == null)

            errors += "The description of the event can't be null!\n";

        else if (entity.getDescription().equals(""))

            errors += "The description of the event can't be empty!\n";

        if (entity.getDate() == null)

            errors += "The date of the event can't be null!\n";

        else if (entity.getDate().compareTo(LocalDateTime.now()) < 0)

            errors += "The date of the event can't be set in past!\n";

        if (!errors.equals(""))

            throw new ValidationException(errors);
    }
}
