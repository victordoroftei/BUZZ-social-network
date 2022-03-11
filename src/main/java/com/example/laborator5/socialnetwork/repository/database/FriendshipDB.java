package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.Friendship;
import com.example.laborator5.socialnetwork.domain.Tuple;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * The class which represents the repository of friendships using a database
 */
public class FriendshipDB extends AbstractRepoDB<Tuple<Long, Long>, Friendship> {

    /**
     * Method which extracts a friendship from a result set
     *
     * @param resultSet - ResultSet
     * @return the friendship
     */
    @Override
    public Friendship extractEntity(ResultSet resultSet) {
        try {

            return new Friendship(resultSet.getLong(1), resultSet.getLong(2),
                    resultSet.getObject(3, LocalDateTime.class));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the sql query for finding a friendship by id
     *
     * @return the query for finding a friendship
     */
    @Override
    public String findOneSQL() {

        return "SELECT * FROM friendships WHERE id1 = ? AND id2 = ?;";
    }

    /**
     * Returns the sql query for finding all the friendships
     *
     * @return the query for finding all the friendships
     */
    @Override
    public String findAllSQL() {
        return "SELECT * FROM friendships";
    }

    /**
     * Returns the sql query for saving a friendship
     *
     * @return the query for saving a friendship
     */
    @Override
    public String saveSQL() {
        return "INSERT INTO friendships (id1, id2, date) VALUES (?, ?, ?)";
    }

    /**
     * Returns the sql query for deleting a friendship
     *
     * @return the query for deleting a friendship
     */
    @Override
    public String deleteSQL() {
        return "DELETE FROM friendships WHERE id1 = ? AND id2 = ?;";
    }

    /**
     * Returns the sql query for updating a friendship
     *
     * @return the query for updating a friendship
     */
    @Override
    public String updateSQL() {
        return "UPDATE friendships SET date = ? WHERE id1 = ? AND id2 = ?;";
    }


    /**
     * Sets the parameters for the find one prepared statement
     *
     * @param ps            - PreparedStatement
     * @param longLongTuple - The id
     */
    @Override
    protected void findOneSet(PreparedStatement ps, Tuple<Long, Long> longLongTuple) {

        try {

            ps.setLong(1, longLongTuple.getLeft());
            ps.setLong(2, longLongTuple.getRight());

        } catch (SQLException ex) {

            ex.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the save prepared statement
     *
     * @param ps     - Prepared statement
     * @param entity - The friendship
     */
    @Override
    protected void saveSet(PreparedStatement ps, Friendship entity, Connection connection) {

        try {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            ps.executeUpdate();

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

    }

    /**
     * Sets the parameters for the delete prepared statement
     *
     * @param ps     - Prepared statement
     * @param entity - the friendship we want to delete
     */
    @Override
    protected void deleteSet(PreparedStatement ps, Friendship entity) {

        try {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

    }

    /**
     * Sets the parameters for the update prepared statement
     *
     * @param ps     - Prepared statement
     * @param entity - The new friendship
     */
    @Override
    protected void updateSet(PreparedStatement ps, Friendship entity) {

        try {

            ps.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            ps.setLong(2, entity.getId().getLeft());
            ps.setLong(3, entity.getId().getRight());

        } catch (SQLException ex) {

            ex.printStackTrace();
        }

    }
}
