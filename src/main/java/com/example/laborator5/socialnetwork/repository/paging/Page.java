package com.example.laborator5.socialnetwork.repository.paging;

import java.util.stream.Stream;

/**
 * This interface defines the method for page
 *
 * @param <E> - the type of elements
 */
public interface Page<E> {

    /**
     * This method returns the current pageable item
     *
     * @return the current pageable
     */
    Pageable getPageable();

    /**
     * This method returns the next pageable item
     *
     * @return the next pageable
     */
    Pageable nextPageable();

    /**
     * This method returns the content of the current item
     *
     * @return the content of the current item as stream
     */
    Stream<E> getContent();
}
