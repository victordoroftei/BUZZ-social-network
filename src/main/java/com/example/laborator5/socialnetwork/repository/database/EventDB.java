package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.Event;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The role of this class is to manage the events
 */
public class EventDB extends AbstractRepoDB<Long, Event> {

    /**
     * The role of this method is to extract the event from our database
     *
     * @param resultSet - ResultSet
     * @return the event extracted from the ResultSet
     */
    @Override
    protected Event extractEntity(ResultSet resultSet) {

        try {

            Event event = null;

            List<Long> list = new ArrayList<>();

            do {

                Long id_event = resultSet.getLong("id_event");
                String name = resultSet.getString("name");
                Long organizer = resultSet.getLong("organizer");
                String description = resultSet.getString("description");
                LocalDateTime dateTime = resultSet.getTimestamp("datetime").toLocalDateTime();
                Long participant = resultSet.getLong("idparticipant");
                list.add(participant);

                if (event == null) {

                    event = new Event(id_event, name, organizer, description, dateTime, new ArrayList<>(list));
                } else if (event.getId().equals(id_event)) {

                    event.setParticipants(new ArrayList<>(list));
                } else {

                    resultSet.previous();
                    return event;
                }
            } while (resultSet.next());

            return event;

        } catch (SQLException throwables) {

            throwables.printStackTrace();

        }

        return null;
    }

    /**
     * This method returns the sql query for finding an event by its id
     *
     * @return the query for finding an event
     */
    @Override
    protected String findOneSQL() {

        return "SELECT id_event, name, organizer, description, datetime, idparticipant FROM event e\n" +
                "INNER JOIN eventparticipants e2 on e.id_event = e2.idevent and e.id_event = ?";
    }

    /**
     * Returns the sql query for finding all the events
     *
     * @return the query for finding all the events
     */
    @Override
    protected String findAllSQL() {

        return "SELECT id_event, name, organizer, description, datetime, idparticipant FROM event e\n" +
                "INNER JOIN eventparticipants e2 on e.id_event = e2.idevent ORDER BY id_event";
    }

    /**
     * Returns the sql query for saving an event
     *
     * @return the query for saving an event
     */
    @Override
    protected String saveSQL() {

        return "INSERT INTO event (id_event, name, organizer, description, datetime) VALUES (?, ?, ?, ?, ?);";
    }

    /**
     * Returns the sql query for deleting an event
     *
     * @return the query for deleting an event
     */
    @Override
    protected String deleteSQL() {

        return "DELETE FROM event WHERE id_event = ?";
    }

    /**
     * Returns the sql query for updating an event
     *
     * @return the query for updating an event
     */
    @Override
    protected String updateSQL() {

        return "UPDATE event SET name = ?, description = ?, datetime = ? WHERE id_event = ?;";
    }

    /**
     * Sets the parameters for the find one prepared statement
     *
     * @param ps    - the prepared statement
     * @param aLong - the id
     */
    @Override
    protected void findOneSet(PreparedStatement ps, Long aLong) {

        try {
            ps.setLong(1, aLong);

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the save prepared statement
     *
     * @param ps     - the prepared statement
     * @param entity - the entity
     */
    @Override
    protected void saveSet(PreparedStatement ps, Event entity, Connection connection) {

        try {
            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getName());
            ps.setLong(3, entity.getOrganizer());
            ps.setString(4, entity.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(entity.getDate()));

            ps.executeUpdate();

            String sql = "INSERT INTO eventparticipants (idevent, idparticipant) VALUES (?, ?);";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getOrganizer());

            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the delete prepared statement
     *
     * @param ps     - the prepared statement
     * @param entity - the event we want to delete
     */
    @Override
    protected void deleteSet(PreparedStatement ps, Event entity) {

        try {

            ps.setLong(1, entity.getId());

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }
    }

    /**
     * Sets the parameters for the update prepared statement
     *
     * @param ps     - prepared statement
     * @param entity - the new entity
     */
    @Override
    protected void updateSet(PreparedStatement ps, Event entity) {

        try {

            ps.setString(1, entity.getName());
            ps.setString(2, entity.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            ps.setLong(4, entity.getId());

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }
    }
}
