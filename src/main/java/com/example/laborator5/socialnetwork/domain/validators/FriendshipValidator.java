package com.example.laborator5.socialnetwork.domain.validators;

import com.example.laborator5.socialnetwork.domain.Friendship;

/**
 * FriendshipValidator implements Validator and validates friendships between two users
 */
public class FriendshipValidator implements Validator<Friendship> {

    /**
     * The role of this method is to check if a friendship is valid
     *
     * @param entity the entity that needs to be validated
     * @throws ValidationException if the friendship is not valid
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {

        String errors = "";

        if (entity.getId().getLeft() == null)

            errors += "First id can't be null!\n";

        if (entity.getId().getRight() == null)

            errors += "Second id can't be null!\n";

        if (!errors.isEmpty())

            throw new ValidationException(errors);

        if (entity.getId().getLeft().equals(entity.getId().getRight()))

            errors += "You can't have a friendship between identical users!\n";

        if (!errors.equals(""))

            throw new ValidationException(errors);
    }
}
