package com.example.laborator5.socialnetwork.domain.validators;

import com.example.laborator5.socialnetwork.domain.User;

/**
 * User Validator class.
 */
public class UserValidator implements Validator<User> {

    /**
     * Regex for first names and last names
     */
    private static final String REGEX1 = "^\\p{L}*-?\\p{L}*$";

    /**
     * Regex for usernames
     */
    private static final String REGEX2 = "\\p{L}*[0-9]*";

    /**
     * Validates the given user.
     *
     * @param entity the entity that needs to be validated
     * @throws ValidationException if the given user is not valid
     */
    @Override
    public void validate(User entity) throws ValidationException {

        String str = "";

        if (entity.getFirstName() == null)

            str += "FirstName cannot be null!\n";

        else if (entity.getFirstName().equals(""))

            str += "FirstName cannot be empty!\n";

        else if (!entity.getFirstName().matches(REGEX1))

            str += "FirstName can only contain letters and '-'!\n";

        if (entity.getLastName() == null)

            str += "LastName cannot be null!\n";

        else if (entity.getLastName().equals(""))

            str += "LastName cannot be empty!\n";

        else if (!entity.getLastName().matches(REGEX1))

            str += "LastName can only contain letters and '-'!\n";

        if (entity.getUserName() == null)

            str += "Username cannot be null!\n";

        else if (entity.getUserName().equals(""))

            str += "Username cannot be empty!\n";

        else if (!entity.getUserName().matches(REGEX2))

            str += "Username can only contain letters and digits.\n";

        if (!str.equals(""))

            throw new ValidationException(str);
    }
}