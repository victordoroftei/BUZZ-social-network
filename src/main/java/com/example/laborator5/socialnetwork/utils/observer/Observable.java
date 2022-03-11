package com.example.laborator5.socialnetwork.utils.observer;

/**
 * This is the interface for the Observable which is a component of the observer pattern
 */
public interface Observable {

    /**
     * This method adds an observer
     *
     * @param observer - the observer we want to add
     */
    void addObserver(Observer observer);

    /**
     * This method removes an observer
     *
     * @param observer - the observer we want to remove
     */
    void removeObserver(Observer observer);

    /**
     * This method notifies all the observers
     */
    void updateAll();
}
