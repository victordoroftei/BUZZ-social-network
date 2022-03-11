package com.example.laborator5.socialnetwork.service;

import com.example.laborator5.socialnetwork.domain.Profile;
import com.example.laborator5.socialnetwork.domain.User;
import com.example.laborator5.socialnetwork.repository.Repository;
import com.example.laborator5.socialnetwork.utils.observer.Observable;
import com.example.laborator5.socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Profile objects.
 */
public class ProfileService implements Observable {

    /**
     * Profile repository.
     */
    private Repository<Long, Profile> repo;

    /**
     * Observer list.
     */
    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructor method for the ProfileService class.
     *
     * @param repo the profile repository
     */
    public ProfileService(Repository<Long, Profile> repo) {

        this.repo = repo;
    }

    /**
     * Method for adding a profile to the repository.
     *
     * @param profile the profile that will be added
     */
    public void addProfile(Profile profile) {

        this.repo.save(profile);

        this.updateAll();
    }

    /**
     * Method for deleting a profile from the repository.
     *
     * @param profile the profile that will be deleted
     */
    public void deleteProfile(Profile profile) {

        this.repo.delete(profile);

        this.updateAll();
    }

    /**
     * Method for updating a profile in the repository.
     *
     * @param profile the profile that will be updated
     */
    public void updateProfile(Profile profile) {

        this.repo.update(profile);

        this.updateAll();
    }

    /**
     * Method used for getting a profile associated to a certain user.
     *
     * @param user the user that the profile belongs to
     * @return the profile associated to the given user
     */
    public Profile getProfileForUser(User user) {

        return this.repo.findOne(user.getId());
    }

    /**
     * The role of this method is to add an observer to the list
     *
     * @param observer - the observer we want to add
     */
    @Override
    public void addObserver(Observer observer) {

        this.observers.add(observer);
    }

    /**
     * The role of this method is to remove an observer from the list
     *
     * @param observer - the observer we want to remove
     */
    @Override
    public void removeObserver(Observer observer) {

        this.observers.remove(observer);
    }

    /**
     * The role of this method is to notify all the observers
     */
    @Override
    public void updateAll() {

        this.observers.forEach(Observer::update);
    }

}
