package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.User;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * The class which represents the repository of users using a database
 */
public class UserDB extends AbstractRepoDB<Long, User> {

    /**
     * Creates a user from an SQL query result.
     *
     * @param resultSet the result of the SQL query
     * @return the user created from the given result
     */
    @Override
    public User extractEntity(ResultSet resultSet) {

        try {

            User u = new User(resultSet.getString(2),
                    resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));

            u.setId(resultSet.getLong(1));
            u.setLastLogin(resultSet.getObject(6, LocalDateTime.class));

            return u;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the sql query for finding a user by id
     *
     * @return the find a user query
     */
    @Override
    public String findOneSQL() {

        return "SELECT * FROM users WHERE id = ?;";
    }

    /**
     * Returns the sql query for finding all the users
     *
     * @return the find all query
     */
    @Override
    public String findAllSQL() {

        return "SELECT * FROM users;";
    }

    /**
     * Returns the sql query for saving a user
     *
     * @return the save user query
     */
    @Override
    public String saveSQL() {

        return "INSERT INTO users (id, first_name, last_name, user_name, password) VALUES (?, ?, ?, ?, ?);";
    }


    /**
     * Returns the sql query for deleting a user
     *
     * @return the delete user query
     */
    @Override
    public String deleteSQL() {

        return "DELETE FROM users WHERE id = ?;";
    }

    /**
     * Returns the sql query for updating a user
     *
     * @return the update user query
     */
    @Override
    public String updateSQL() { // For now, the update method doesn't update the user's password

        return "UPDATE users SET first_name = ?, last_name = ?, user_name = ?, last_login = ? WHERE id = ?;";
    }

    /**
     * Sets the parameters for the find one prepared statement
     *
     * @param ps    - PreparedStatement
     * @param aLong - The id
     */
    @Override
    protected void findOneSet(PreparedStatement ps, Long aLong) {

        try {

            ps.setLong(1, aLong);
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the save prepared statement
     *
     * @param ps     - Prepared statement
     * @param entity - The user
     */
    @Override
    protected void saveSet(PreparedStatement ps, User entity, Connection connection) {

        try {

            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setString(4, entity.getUserName());
            ps.setString(5, entity.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the delete prepared statement
     *
     * @param ps     - Prepared statement
     * @param entity - the user we want to delete
     */
    @Override
    protected void deleteSet(PreparedStatement ps, User entity) {

        try {

            ps.setLong(1, entity.getId());
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the update prepared statement
     *
     * @param ps     - Prepared statement
     * @param entity - The new user
     */
    @Override
    protected void updateSet(PreparedStatement ps, User entity) {
        try {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getUserName());

            if (entity.getLastLogin() != null)
                ps.setTimestamp(4, Timestamp.valueOf(entity.getLastLogin()));

            else
                ps.setObject(4, null);

            ps.setLong(5, entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}
