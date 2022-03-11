package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all the methods for managing the message repository
 */
public class MessageDB extends AbstractRepoDB<Long, Message> {

    /**
     * Method which extracts a message from an SQL result set
     *
     * @param resultSet - ResultSet
     * @return the request
     */
    @Override
    protected Message extractEntity(ResultSet resultSet) {

        try {

            Message messageResult = null;

            List<Long> toList = new ArrayList<>();

            do {

                Long id = resultSet.getLong("id");
                Long from = resultSet.getLong("from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long originalMessage = resultSet.getLong("originalmessage");
                Long to = resultSet.getLong("to");
                toList.add(to);

                if (messageResult == null) {

                    messageResult = new Message(id, from, new ArrayList<>(toList), message, date);

                    if (!originalMessage.equals(0L))
                        messageResult.setOriginalMessage(originalMessage);
                    else
                        messageResult.setOriginalMessage(null);
                } else if (messageResult.getId().equals(id)) {

                    messageResult.setTo(new ArrayList<>(toList));
                } else {
                    resultSet.previous();
                    return messageResult;
                }

            } while (resultSet.next());

            return messageResult;

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the SQL query for finding a message by its id.
     *
     * @return the query for finding a message
     */
    @Override
    protected String findOneSQL() {

        return "SELECT m.id, m.from, m.message, m.date, m.originalmessage, m2.\"to\" FROM messagedata m\n" +
                "INNER JOIN messagereceivers m2 on m.id = m2.message and m.id = ?";
    }

    /**
     * Returns the SQL query for finding all the messages.
     *
     * @return the query for finding all the messages
     */
    @Override
    protected String findAllSQL() {

        return "SELECT m.id, m.from, m.message, m.date, m.originalmessage, m2.\"to\" FROM messagedata m\n" +
                "INNER JOIN messagereceivers m2 on m.id = m2.message";
    }

    /**
     * Returns the SQL query for saving a message.
     *
     * @return the query for saving a message
     */
    @Override
    protected String saveSQL() {

        return "INSERT INTO messagedata (id, \"from\", message, date, originalmessage) VALUES (?, ?, ?, ?, ?);";
    }

    /**
     * Returns the SQL query for deleting a message.
     *
     * @return the query for deleting a message
     */
    @Override
    protected String deleteSQL() {

        return "DELETE FROM messagedata WHERE id = ?;";
    }

    @Override
    @Deprecated
    protected String updateSQL() {

        throw new UnsupportedOperationException();
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
     * @param entity - The message
     */
    @Override
    protected void saveSet(PreparedStatement ps, Message entity, Connection connection) {

        try {

            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getFrom());
            ps.setString(3, entity.getMessage());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if (entity.getOriginalMessage() == null)

                ps.setNull(5, Types.NULL);

            else

                ps.setLong(5, entity.getOriginalMessage());

            ps.executeUpdate();

            String sql = "INSERT INTO messagereceivers (message, \"to\") VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1, entity.getId());

            for (Long id : entity.getTo()) {

                preparedStatement.setLong(2, id);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the delete prepared statement
     *
     * @param ps     - Prepared statement
     * @param entity - the message we want to delete
     */
    @Override
    protected void deleteSet(PreparedStatement ps, Message entity) {

        try {
            ps.setLong(1, entity.getId());

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    @Override
    @Deprecated
    protected void updateSet(PreparedStatement ps, Message entity) {

        throw new UnsupportedOperationException();
    }
}
