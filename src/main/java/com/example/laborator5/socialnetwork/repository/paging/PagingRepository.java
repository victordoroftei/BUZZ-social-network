package com.example.laborator5.socialnetwork.repository.paging;

import com.example.laborator5.socialnetwork.domain.Entity;
import com.example.laborator5.socialnetwork.repository.Repository;

/**
 * This interface extends the Repository and adds findAll method using pagination
 *
 * @param <ID> - type of the id
 * @param <E>  - type of entities
 */
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    /**
     * This method defines the findAll method using pagination
     *
     * @param pageable - the current pageable item
     * @return the requested page
     */
    Page<E> findAll(Pageable pageable, Iterable<E> iterable);
}
