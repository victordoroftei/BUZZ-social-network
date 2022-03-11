package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.Event;
import com.example.laborator5.socialnetwork.domain.User;
import com.example.laborator5.socialnetwork.repository.database.hikari.DataSource;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The role of this class is to manage the participants of an event
 */
public class EventParticipantsDB extends AbstractRepoDB<Long, Event> {

    /**
     * The role of this method is to extract an event from a resultSet
     *
     * @param resultSet - ResultSet
     * @return the event
     */
    @Override
    protected Event extractEntity(ResultSet resultSet) {

        try {

            return new Event(resultSet.getLong("id_event"), resultSet.getString("name"), resultSet.getLong("organizer"), resultSet.getString("description"), resultSet.getTimestamp("datetime").toLocalDateTime(), null);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    @Deprecated
    protected String findOneSQL() {

        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    protected String findAllSQL() {

        throw new UnsupportedOperationException();
    }

    /**
     * Returns the sql query for inserting a participant in an event
     *
     * @return the query for inserting a participant
     */
    @Override
    protected String saveSQL() {

        return "INSERT INTO eventparticipants (idevent, idparticipant) VALUES (?, ?);";
    }

    /**
     * Returns the sql query for deleting a participant from an event
     *
     * @return the query for deleting a participant
     */
    @Override
    protected String deleteSQL() {

        return "DELETE FROM eventparticipants WHERE idevent = ? AND idparticipant = ?";
    }

    @Override
    @Deprecated
    protected String updateSQL() {

        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    protected void findOneSet(PreparedStatement ps, Long aLong) {

        throw new UnsupportedOperationException();
    }

    /**
     * Method for setting up the parameters of the save prepared statement
     *
     * @param ps     - prepared statement
     * @param entity - contains the participant we want to save
     */
    @Override
    protected void saveSet(PreparedStatement ps, Event entity, Connection connection) {

        try {

            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getParticipants().get(0));

            ps.executeUpdate();

        } catch (SQLException throwables) {

            throwables.printStackTrace();

        }
    }

    /**
     * Method for setting up the parameters of the delete prepared statement
     *
     * @param ps     - prepared statement
     * @param entity - the participant we want to delete
     */
    @Override
    protected void deleteSet(PreparedStatement ps, Event entity) {

        try {

            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getParticipants().get(0));

        } catch (SQLException throwables) {

            throwables.printStackTrace();

        }
    }

    @Override
    @Deprecated
    protected void updateSet(PreparedStatement ps, Event entity) {

        throw new UnsupportedOperationException();
    }

    /**
     * The role of this method is to set the notifications for a participant for an event
     *
     * @param event    - the event
     * @param variable - true - if the user wants to receive notifications
     *                 - false - otherwise
     */
    public void setNotificationsForParticipant(Event event, Boolean variable) {

        String sql = "UPDATE eventparticipants SET notifications = ? WHERE idevent = ? AND idparticipant = ?";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, variable);
            statement.setLong(2, event.getId());
            statement.setLong(3, event.getParticipants().get(0));

            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Method that gets the notification status for a participant for a certain event.
     *
     * @param event the event (also contains the participant's id)
     * @return true if the notifications are enabled; false otherwise
     */
    public boolean getNotificationsForParticipant(Event event) {

        String sql = "SELECT notifications FROM eventparticipants WHERE idevent = ? AND idparticipant = ?";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, event.getId());
            statement.setLong(2, event.getParticipants().get(0));

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())

                return resultSet.getBoolean(1);

        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }

        return true;
    }

    /**
     * The role of this method is to get the upcoming events for a user
     *
     * @param user - the participant
     * @return the list of the upcoming events
     */
    public List<Event> getUpcomingEvents(User user) {

        List<Event> list = new ArrayList<>();

        String sql = "SELECT * FROM event e INNER JOIN eventparticipants ep ON e.id_event = ep.idevent AND ep.idparticipant = ? AND ep.notifications = ?";

        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            statement.setLong(1, user.getId());
            statement.setBoolean(2, true);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Event e = this.extractEntity(resultSet);

                Duration duration = Duration.between(LocalDateTime.now(), e.getDate());
                long days = duration.toDays();

                if (days <= 7 && days >= 0)

                    list.add(e);
            }

            return list;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }
}
