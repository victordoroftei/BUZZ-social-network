package com.example.laborator5.socialnetwork.service;


import com.example.laborator5.socialnetwork.domain.User;
import com.example.laborator5.socialnetwork.domain.validators.UserValidator;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.repository.Repository;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;

/**
 * Class which handles users
 */
public class UserService {
    /**
     * User repository
     */
    private Repository<Long, User> repo;

    /**
     * User validator.
     */
    private UserValidator validator;

    /**
     * Constructor
     *
     * @param repo      - Repository0<Long,User></Long,User>, the connection to the user repo
     * @param validator - UserValidator, the validator of the User
     */
    public UserService(Repository<Long, User> repo, UserValidator validator) {

        this.repo = repo;
        this.validator = validator;
    }

    /**
     * Method for adding a user
     *
     * @param user - User, the user we want to add
     * @return null
     * @throws ValidationException if the user is not valid
     * @throws ServiceException    if the a user with the same username already exists
     */
    public User addUser(User user) {
        this.validator.validate(user);
        this.checkUniqueUserName(user.getUserName());
        this.setIdForUser(user);
        User task = repo.save(user);
        return task;
    }

    /**
     * Get all the users
     *
     * @return all the users
     */
    public Iterable<User> getAll() {

        return repo.findAll();
    }

    /**
     * Delete a user
     *
     * @param userDTO - the user we want to delete
     * @return null
     * @throws ValidationException if the username is not valid
     * @throws ServiceException    if there is no user with the username
     */
    public User deleteUser(UserDTO userDTO) {

        this.validateUsernameOrUsernames(userDTO.getUserName());
        Long id = this.fromUserNameToId(userDTO.getUserName());
        if (id == null)
            throw new ServiceException("No user found with the username that you've entered!");

        User u = new User(null, null, null, null);
        u.setId(id);
        return repo.delete(u);
    }

    /**
     * Method used for updating all users fields
     *
     * @param userDTO1 - the old user
     * @param userDTO2 - the old user but updated
     * @return null
     * @throws ServiceException    if the old username is invalid or no user found with old username
     * @throws ValidationException if the new fields of the user are invalid
     */
    public User updateUser(UserDTO userDTO1, UserDTO userDTO2) {

        try {
            this.validateUsernameOrUsernames(userDTO1.getUserName());
        } catch (ValidationException err) {
            throw new ServiceException("Old username is invalid!\n");
        }

        User user = new User(userDTO2.getFirstName(), userDTO2.getLastName(), userDTO2.getUserName(), null, userDTO2.getLastLogin());
        this.validator.validate(user);

        // This prevents an error being raised if the new username is the same as the old username.
        if (!userDTO2.getUserName().equals(userDTO1.getUserName()))

            this.checkUniqueUserName(userDTO2.getUserName());

        Long id = this.fromUserNameToId(userDTO1.getUserName());

        if (id == null)
            throw new ServiceException("No user found with the username that you've entered!\n");
        user.setId(id);

        return this.repo.update(user);
    }

    /**
     * Set the id for an user given as an argument
     *
     * @param user - User
     */
    private void setIdForUser(User user) {

        Long id = 1L;
        User u = this.repo.findOne(id);
        while (u != null) { // means already exists
            id++;
            u = this.repo.findOne(id);
        }
        user.setId(id);
    }

    /**
     * Returns the id of a user which has a specific username
     *
     * @param userName - String, the username we are looking for
     * @return id, if the user is found
     * null, otherwise
     */
    public Long fromUserNameToId(String userName) {
        Iterable<User> users = this.repo.findAll();
        for (User user : users)

            if (user.getUserName().equals(userName))

                return user.getId();

        return null;
    }

    /**
     * Check if a username is unique
     *
     * @param userName - String, the username we are searching for
     * @throws ServiceException if the username is not unique
     */
    public void checkUniqueUserName(String userName) {

        Iterable<User> users = this.repo.findAll();
        for (User user : users)

            if (user.getUserName().equals(userName))

                throw new ServiceException("A user with this username already exists!\n");
    }

    /**
     * Get user by id
     *
     * @param userDTO - the user we are looking for
     * @return the user having the id given as parameter
     */
    public User findOne(UserDTO userDTO) {

        return this.repo.findOne(userDTO.getId());
    }

    /**
     * Validate one or more usernames
     *
     * @param userNames - String[], containing usernames which we want to check if they are valid or not
     * @throws ServiceException if the usernames / usernames are not valid
     */
    public void validateUsernameOrUsernames(String... userNames) {

        if (userNames.length == 1) {

            User u = new User("a", "a", userNames[0], null);
            u.setId(1L);
            this.validator.validate(u);
        } else if (userNames.length == 2) {

            String erori = "";
            User u1 = new User("a", "a", userNames[0], null);
            u1.setId(1L);
            User u2 = new User("a", "a", userNames[1], null);
            u2.setId(2L);

            try {
                this.validator.validate(u1);
            } catch (ValidationException e) {
                erori += "First username can't be empty!\n";
            }
            try {
                this.validator.validate(u2);
            } catch (ValidationException e) {
                erori += "Second username can't be empty!\n";
            }

            if (!erori.equals(""))

                throw new ServiceException(erori);
        }
    }
}
