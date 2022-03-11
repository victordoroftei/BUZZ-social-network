package com.example.laborator5.socialnetwork.service;

import com.example.laborator5.socialnetwork.domain.Friendship;
import com.example.laborator5.socialnetwork.domain.Tuple;
import com.example.laborator5.socialnetwork.domain.validators.FriendshipValidator;
import com.example.laborator5.socialnetwork.domain.validators.ValidationException;
import com.example.laborator5.socialnetwork.utils.observer.Observable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;
import com.example.laborator5.socialnetwork.repository.Repository;
import com.example.laborator5.socialnetwork.service.dto.FriendshipDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which handles friendships
 */
public class FriendshipService implements Observable {

    /**
     * Friendship repository
     */
    private Repository<Tuple<Long, Long>, Friendship> repo;

    /**
     * Friendship validator
     */
    private FriendshipValidator validator;

    /**
     * This field retains the observers list
     */
    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructor
     *
     * @param repo      - Repository0<Tuple<Long,Long></Long,Long>>, the connection to the repo
     * @param validator - FriendshipValidator, the validator of Friendship
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> repo, FriendshipValidator validator) {

        this.repo = repo;
        this.validator = validator;
    }

    /**
     * Get all friendships
     *
     * @return all the Friendships
     */
    public Iterable<Friendship> getAll() {

        return repo.findAll();
    }

    /**
     * Add a friendship
     *
     * @param friendship - Friendship, the friendship we want to add
     * @return null
     * @throws ValidationException if the Friendship is not valid
     * @throws ServiceException    if the Friendships already exists
     */
    public Friendship addFriendship(Friendship friendship) {

        this.validator.validate(friendship);
        this.friendshipAlreadyExists(friendship);
        Friendship f = repo.save(friendship);
        return f;
    }

    /**
     * Delete a friendship
     *
     * @param friendshipDTO - the friendship we want to delete
     * @return null
     * @throws ValidationException if the friendship is not valid
     * @throws ServiceException    if the friendship doesn't exist
     */
    public Friendship deleteFriendship(FriendshipDTO friendshipDTO) {

        Long idLeft = friendshipDTO.getUser1().getId();
        Long idRight = friendshipDTO.getUser2().getId();

        Tuple<Long, Long> id = new Tuple<>(idLeft, idRight);

        this.validator.validate(new Friendship(id.getLeft(), id.getRight()));

        if (id.getLeft() > id.getRight()) {
            Long aux = id.getLeft();
            id.setLeft(id.getRight());
            id.setRight(aux);
        }

        Friendship connection = this.repo.findOne(id);
        if (connection == null)
            throw new ServiceException("There is no friendship between those 2 users!\n");

        Friendship f = repo.delete(new Friendship(id.getLeft(), id.getRight()));

        this.updateAll();

        return f;
    }

    /**
     * Get the friendship which has the id equal to the id given as parameter
     *
     * @param friendshipDTO - the friendship we are looking for
     * @return the friendship which has the id equal to the one is given as parameter
     */
    public Friendship findOne(FriendshipDTO friendshipDTO) {

        Long idLeft = friendshipDTO.getUser1().getId();
        Long idRight = friendshipDTO.getUser2().getId();

        Tuple<Long, Long> id = new Tuple<>(idLeft, idRight);

        if (id.getLeft() > id.getRight()) {
            Long aux = id.getLeft();
            id.setLeft(id.getRight());
            id.setRight(aux);
        }

        return this.repo.findOne(id);
    }

    /**
     * The role of this method is to update the date of a friendship
     *
     * @param friendshipDTO - the friendship we want to update
     * @return null
     * @throws ValidationException if the friendship is not valid
     * @throws ServiceException    if there is no friendship with the entered id
     */
    public Friendship updateFriendship(FriendshipDTO friendshipDTO) {

        Long idLeft = friendshipDTO.getUser1().getId();
        Long idRight = friendshipDTO.getUser2().getId();

        Tuple<Long, Long> id = new Tuple<>(idLeft, idRight);

        this.validator.validate(new Friendship(id.getLeft(), id.getRight(), friendshipDTO.getDate()));

        if (id.getLeft() > id.getRight()) {
            Long aux = id.getLeft();
            id.setLeft(id.getRight());
            id.setRight(aux);
        }

        Friendship connection = this.repo.findOne(id);
        if (connection == null)
            throw new ServiceException("Their is no friendship between those 2 users!\n");

        connection.setDate(friendshipDTO.getDate());
        return this.repo.update(connection);
    }

    /**
     * Check if a friendship already exists
     *
     * @param friendship - Friendship, the friendship we are checking
     * @throws ServiceException if the friendship exists
     */
    public void friendshipAlreadyExists(Friendship friendship) {

        Iterable<Friendship> friendships = this.repo.findAll();
        for (Friendship f : friendships)

            if (f.equals(friendship))

                throw new ServiceException("Friendship already exists!\n");
    }

    /**
     * The role of this method is to add an observer to the list
     *
     * @param observer - the observer we want to add
     */
    @Override
    public void addObserver(Observer observer) {

        observers.add(observer);
    }

    /**
     * The role of this method is to remove an observer from the list
     *
     * @param observer - the observer we want to remove
     */
    @Override
    public void removeObserver(Observer observer) {

        observers.remove(observer);
    }

    /**
     * The role of this method is to notify all the observers
     */
    @Override
    public void updateAll() {

        observers.forEach(Observer::update);
    }
}
