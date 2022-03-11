package com.example.laborator5.socialnetwork.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for an entity
 *
 * @param <ID> - type of variable called id
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /**
     * Get method for id
     *
     * @return the id of the entity
     */
    public ID getId() {
        return id;
    }

    /**
     * Update the id
     *
     * @param id - Abstract
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * hashCode method override
     *
     * @return - the hash value for id
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * equals method override
     *
     * @param obj - Object
     * @return boolean value
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null) return false;
        if (!(obj instanceof Entity<?>)) return false;
        Entity<?> e = (Entity<?>) obj;
        return Objects.equals(this.id, e.getId());
    }
}
