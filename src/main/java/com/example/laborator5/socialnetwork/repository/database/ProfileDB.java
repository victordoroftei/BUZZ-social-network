package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.Profile;

import java.sql.*;
import java.time.LocalDate;

/**
 * The class for the profile database repository.
 */
public class ProfileDB extends AbstractRepoDB<Long, Profile> {

    /**
     * Creates a profile from an SQL query result.
     *
     * @param resultSet the result of the SQL query
     * @return the profile created from the given result
     */
    @Override
    public Profile extractEntity(ResultSet resultSet) {

        try {

            Profile p = new Profile(resultSet.getString(2), resultSet.getString(3),
                    resultSet.getObject(4, LocalDate.class), resultSet.getString(5));

            p.setId(resultSet.getLong(1));

            return p;
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the sql query for finding a profile by id
     *
     * @return the query for finding a profile
     */
    @Override
    public String findOneSQL() {

        return "SELECT * FROM profiles WHERE id = ?;";
    }

    /**
     * Returns the sql query for finding all the profiles
     *
     * @return the query for finding all profiles
     */
    @Override
    public String findAllSQL() {

        return "SELECT * FROM profiles;";
    }

    /**
     * Returns the sql query for saving a profile
     *
     * @return the query for saving a profile
     */
    @Override
    public String saveSQL() {

        return "INSERT INTO profiles (id, aboutme, hometown, birthday, hobbies) VALUES (?, ?, ?, ?, ?);";
    }

    /**
     * Returns the sql query for deleting a profile
     *
     * @return the query for deleting a profile
     */
    @Override
    public String deleteSQL() {

        return "DELETE FROM profiles WHERE id = ?;";
    }

    /**
     * Returns the sql query for updating a profile
     *
     * @return the query for updating a profile
     */
    @Override
    public String updateSQL() {

        return "UPDATE profiles SET aboutme = ?, hometown = ?, birthday = ?, hobbies = ? WHERE id = ?;";
    }

    /**
     * Sets the parameters for the find one prepared statement
     *
     * @param ps the prepared statement
     * @param id the id of the entity that is searched
     */
    @Override
    protected void findOneSet(PreparedStatement ps, Long id) {

        try {

            ps.setLong(1, id);
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the save prepared statement
     *
     * @param ps     the prepared statement
     * @param entity the profile that will be saved
     */
    @Override
    protected void saveSet(PreparedStatement ps, Profile entity, Connection connection) {

        try {

            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getAboutMe());
            ps.setString(3, entity.getHomeTown());
            ps.setDate(4, Date.valueOf(entity.getBirthday()));
            ps.setString(5, entity.getHobbies());

            ps.executeUpdate();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the delete prepared statement
     *
     * @param ps     the prepared statement
     * @param entity the profile that will be deleted
     */
    @Override
    protected void deleteSet(PreparedStatement ps, Profile entity) {

        try {

            ps.setLong(1, entity.getId());
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the update prepared statement
     *
     * @param ps     the prepared statement
     * @param entity the profile that will be updated
     */
    @Override
    protected void updateSet(PreparedStatement ps, Profile entity) {

        try {

            ps.setString(1, entity.getAboutMe());
            ps.setString(2, entity.getHomeTown());
            ps.setDate(3, Date.valueOf(entity.getBirthday()));
            ps.setString(4, entity.getHobbies());
            ps.setLong(5, entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

}
