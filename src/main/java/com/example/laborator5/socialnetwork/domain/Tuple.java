package com.example.laborator5.socialnetwork.domain;

import com.example.laborator5.socialnetwork.service.dto.EventDTO;
import com.example.laborator5.socialnetwork.service.dto.UserDTO;

import java.util.Objects;


/**
 * Define a Tuple of generic type entities
 *
 * @param <E1> - tuple first entity type
 * @param <E2> - tuple second entity type
 */
public class Tuple<E1, E2> {
    /**
     * The first entity of the tuple
     */
    private E1 e1;

    /**
     * The second entity of the tuple
     */
    private E2 e2;

    /**
     * Constructor for the Tuple class.
     *
     * @param e1 the first entity of the tuple
     * @param e2 the second entity of the tuple
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Returns the first entity of the tuple.
     *
     * @return the first entity of the tuple
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     * Sets the first entity of the tuple.
     *
     * @param e1 the new first entity of the tuple
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     * Returns the second entity of the tuple.
     *
     * @return the second entity of the tuple
     */
    public E2 getRight() {
        return e2;
    }

    /**
     * Sets the second entity of the tuple.
     *
     * @param e2 the new second entity of the tuple
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    /**
     * Overrided toString() method.
     *
     * @return a string containing the data of the tuple
     */
    @Override
    public String toString() {
        if (this.e1 instanceof UserDTO && this.e2 instanceof EventDTO)
            return ((UserDTO) this.e1).getFirstName() + " " + ((UserDTO) this.e1).getLastName() + " attends:\n" + this.e2.toString();

        return "" + e1 + "," + e2;

    }

    /**
     * Checks if the tuple is equal to the given object.
     *
     * @param obj the object that needs to be checked
     * @return true if the object is equal to the tuple; false, otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2);
    }

    /**
     * Calculates the hash code of the tuple.
     *
     * @return the hash code of the tuple
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}