package com.example.laborator5.socialnetwork.domain.validators;

import com.example.laborator5.socialnetwork.domain.Request;

/**
 * RequestValidator implements Validator and validates a request between two users
 */
public class RequestValidator implements Validator<Request> {

    /**
     * The role of this method is to check if a request is valid
     *
     * @param entity the entity that needs to be validated
     * @throws ValidationException if the request is not valid
     */
    @Override
    public void validate(Request entity) throws ValidationException {

        String errors = "";
        boolean throwErr = false;

        if (entity.getIdUser1() == null) {

            errors += "The id of the first user can't be null!\n";
            throwErr = true;
        }

        if (entity.getIdUser2() == null) {

            errors += "The id of the second user can't be null!\n";
            throwErr = true;
        }

        if (entity.getStatus() == null)

            errors += "The request can have this status!\n";

        if (throwErr)    // this is the case when one of the user's id is null

            throw new ValidationException(errors);

        if (entity.getIdUser1().equals(entity.getIdUser2()))

            errors += "You can't have a request between identical users!\n";

        if (!errors.equals(""))

            throw new ValidationException(errors);

    }
}
