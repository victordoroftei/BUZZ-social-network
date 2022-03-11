package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.Post;

import java.sql.*;
import java.time.LocalDateTime;

public class PostDB extends AbstractRepoDB<Long, Post> {

    /**
     * Creates a post from an SQL query result.
     *
     * @param resultSet the result of the SQL query
     * @return the profile created from the given result
     */
    @Override
    public Post extractEntity(ResultSet resultSet) {

        try {

            Post p = new Post(resultSet.getLong(2), resultSet.getString(3), resultSet.getObject(4, LocalDateTime.class));
            p.setId(resultSet.getLong(1));

            return p;
        } catch (SQLException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the sql query for finding a post by id
     *
     * @return the query for finding a post
     */
    @Override
    public String findOneSQL() {

        return "SELECT * FROM posts WHERE id = ?;";
    }

    /**
     * Returns the sql query for finding all the posts
     *
     * @return the query for finding all posts
     */
    @Override
    public String findAllSQL() {

        return "SELECT * FROM posts;";
    }

    /**
     * Returns the sql query for saving a post
     *
     * @return the query for saving a post
     */
    @Override
    public String saveSQL() {

        return "INSERT INTO posts (id, iduser, content, postedon) VALUES (?, ?, ?, ?);";
    }

    /**
     * Returns the sql query for deleting a post
     *
     * @return the query for deleting a post
     */
    @Override
    public String deleteSQL() {

        return "DELETE FROM posts WHERE id = ?;";
    }

    /**
     * Returns the sql query for updating a post
     *
     * @return the query for updating a post
     */
    @Override
    public String updateSQL() {

        return "UPDATE posts SET content = ?, postedon = ? WHERE id = ?;";
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
     * @param entity the post that will be saved
     */
    @Override
    protected void saveSet(PreparedStatement ps, Post entity, Connection connection) {

        try {

            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getIdUser());
            ps.setString(3, entity.getContent());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getPostedOn()));

            ps.executeUpdate();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the delete prepared statement
     *
     * @param ps     the prepared statement
     * @param entity the post that will be deleted
     */
    @Override
    protected void deleteSet(PreparedStatement ps, Post entity) {

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
    protected void updateSet(PreparedStatement ps, Post entity) {

        try {

            ps.setString(1, entity.getContent());
            ps.setTimestamp(2, Timestamp.valueOf(entity.getPostedOn()));
            ps.setLong(3, entity.getId());

            ps.executeUpdate();
        } catch (SQLException e) {

            e.printStackTrace();
        }

    }
}
