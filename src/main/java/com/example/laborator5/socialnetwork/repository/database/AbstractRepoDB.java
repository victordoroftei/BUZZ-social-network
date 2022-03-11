package com.example.laborator5.socialnetwork.repository.database;

import com.example.laborator5.socialnetwork.domain.Entity;
import com.example.laborator5.socialnetwork.repository.database.hikari.DataSource;
import com.example.laborator5.socialnetwork.repository.paging.Page;
import com.example.laborator5.socialnetwork.repository.paging.Pageable;
import com.example.laborator5.socialnetwork.repository.paging.Paginator;
import com.example.laborator5.socialnetwork.repository.paging.PagingRepository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the AbstractDBRepo class which implements the interface Repository and defines some abstract methods
 *
 * @param <ID> - type E must have an attribute of type ID
 * @param <E>  -  type of entities saved in repository
 */
public abstract class AbstractRepoDB<ID, E extends Entity<ID>> implements PagingRepository<ID, E> {

    /**
     * Method which extracts entity from a result set
     *
     * @param resultSet - ResultSet
     * @return the entity
     */
    protected abstract E extractEntity(ResultSet resultSet);

    /**
     * Returns the sql query for finding an entity by id
     *
     * @return the find an entity query
     */
    protected abstract String findOneSQL();

    /**
     * Returns the sql query for finding all the entities
     *
     * @return the find all query
     */
    protected abstract String findAllSQL();

    /**
     * Returns the sql query for saving an entity
     *
     * @return the save entity query
     */
    protected abstract String saveSQL();

    /**
     * Returns the sql query for deleting an entity
     *
     * @return the delete entity query
     */
    protected abstract String deleteSQL();

    /**
     * Returns the sql query for updating an entity
     *
     * @return the update entity query
     */
    protected abstract String updateSQL();

    /**
     * Abstract method for setting the parameters of the find one prepared statement
     *
     * @param ps - the prepared statement
     * @param id - the if of the entity we are looking for
     */
    protected abstract void findOneSet(PreparedStatement ps, ID id);

    /**
     * Abstract method for setting the parameters of the save prepared statement
     *
     * @param ps         - the prepared statement
     * @param entity     - the entity we want to save
     * @param connection - the connection we are using at this moment
     */
    protected abstract void saveSet(PreparedStatement ps, E entity, Connection connection);

    /**
     * Abstract method for setting the parameters of the delete prepared statement
     *
     * @param ps     - the prepared statement
     * @param entity - the entity we want to delete
     */
    protected abstract void deleteSet(PreparedStatement ps, E entity);

    /**
     * Abstract method for setting the parameters of the update prepared statement
     *
     * @param ps     - the prepared statement
     * @param entity - the entity we want to update
     */
    protected abstract void updateSet(PreparedStatement ps, E entity);

    /**
     * Find an entity by id
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return entity if we find it
     * null if we dont find it
     * @throws IllegalArgumentException if the id is null
     */
    @Override
    public E findOne(ID id) {

        if (id == null)

            throw new IllegalArgumentException("ID must not be null!\n");

        String sql = this.findOneSQL();

        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            this.findOneSet(statement, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())

                return extractEntity(resultSet);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    /**
     * Returns all entities
     *
     * @return the iterable which contains entities
     */
    @Override
    public Iterable<E> findAll() {

        Set<E> entities = new HashSet<>();

        String sql = this.findAllSQL();

        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                E e = extractEntity(resultSet);
                entities.add(e);
            }

            return entities;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return entities;
    }

    /**
     * The role of this method is to save an entity in the db
     *
     * @param entity - The entity we want to add
     *               entity must be not null
     * @return null if the save was done successfully
     * @throws IllegalArgumentException if entity = null
     */
    @Override
    public E save(E entity) {

        if (entity == null)

            throw new IllegalArgumentException("Entity must not be null!\n");

        String sql = this.saveSQL();

        try (Connection connection = DataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            this.saveSet(ps, entity, connection);

            //ps.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    /**
     * The role of this method is to delete an entity from the db
     *
     * @param entity - the entity we want to delete it
     *               id must be not null
     * @return null if the friendship was deleted successfully
     * @throws IllegalArgumentException if the entity is null
     */
    @Override
    public E delete(E entity) {

        if (entity == null)

            throw new IllegalArgumentException("Entity must not be null!\n");

        String sql = this.deleteSQL();

        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            this.deleteSet(statement, entity);

            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    /**
     * The role of this method is to update an entity from the db
     *
     * @param entity - The entity we want to delete it
     *               entity must not be null
     * @return null if the entity was updated successfully
     * @throws IllegalArgumentException if the entity is null
     */
    @Override
    public E update(E entity) {

        if (entity == null)

            throw new IllegalArgumentException("Entity must not be null!\n");

        String sql = this.updateSQL();

        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            this.updateSet(statement, entity);

            statement.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    /**
     * This method is used for paginated repository
     *
     * @param pageable - the current pageable item
     * @param iterable - the entities we want to paginate
     * @return the page we are looking for
     */
    @Override
    public Page<E> findAll(Pageable pageable, Iterable<E> iterable) {

        Paginator<E> paginator = new Paginator<>(pageable, iterable);
        return paginator.paginate();
    }
}
