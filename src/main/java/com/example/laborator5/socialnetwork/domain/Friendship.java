package com.example.laborator5.socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


/**
 * Conatins the friendship between two users and the date when it was created
 */
public class Friendship extends Entity<Tuple<Long, Long>> {

    /**
     * The date when the friendship was created
     */
    private LocalDateTime date;

    /**
     * Constructor using only IDs
     *
     * @param id1 - Long, representing the id of the first user
     * @param id2 - Long, representing the id of the second user
     */
    public Friendship(Long id1, Long id2) {

        if (id1 < id2)
            this.setId(new Tuple<>(id1, id2));
        else
            this.setId(new Tuple<>(id2, id1));

        this.date = LocalDateTime.now();
    }

    /**
     * Constructor using IDs and a date
     *
     * @param id1  - Long, representing the id of the first user
     * @param id2  - Long, representing the id of the second user
     * @param date - LocalDateTime, representing the date when the friendship was created
     */
    public Friendship(Long id1, Long id2, LocalDateTime date) {

        if (id1 < id2)
            this.setId(new Tuple<>(id1, id2));
        else
            this.setId(new Tuple<>(id2, id1));

        this.date = date;
    }

    /**
     * method for getting date
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * method for updating the date
     *
     * @param newDate - LocalDateTime, the new date of the friendship
     */
    public void setDate(LocalDateTime newDate) {

        this.date = newDate;
    }

    /**
     * toString override
     *
     * @return friendship as a string
     */
    @Override
    public String toString() {
        return "ID1 = " + super.getId().getLeft() +
                ", ID2 = " + super.getId().getRight() +
                ", Data = " + getDate();
    }

    /**
     * equals override
     *
     * @param o - Object
     * @return boolean value
     */
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof Friendship))
            return false;

        Friendship that = (Friendship) o;
        return (super.getId().getLeft().equals(that.getId().getLeft()) &&
                super.getId().getRight().equals(that.getId().getRight())) ||
                (super.getId().getLeft().equals(that.getId().getRight()) &&
                        super.getId().getRight().equals(that.getId().getLeft()));
    }

    /**
     * hashCode override
     *
     * @return the hash value of a friendship
     */
    @Override
    public int hashCode() {

        return Objects.hash(getId().getLeft(), getId().getRight(), getDate());
    }
}
