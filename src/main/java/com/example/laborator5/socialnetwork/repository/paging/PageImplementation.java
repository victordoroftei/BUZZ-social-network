package com.example.laborator5.socialnetwork.repository.paging;

import java.util.stream.Stream;

/**
 * The implementation of the Page interface
 *
 * @param <E> - type of elements
 */
public class PageImplementation<E> implements Page<E> {

    /**
     * The current pageable item
     */
    private Pageable pageable;

    /**
     * The content of the current page
     */
    private Stream<E> content;

    /**
     * The constructor of the class
     *
     * @param pageable - the pageable item
     * @param content  - the content of the current page
     */
    public PageImplementation(Pageable pageable, Stream<E> content) {

        this.pageable = pageable;
        this.content = content;
    }

    /**
     * Get method for the pageable item
     *
     * @return the pageable item
     */
    @Override
    public Pageable getPageable() {

        return this.pageable;
    }

    /**
     * Method for going to the next pageable item
     *
     * @return next pageable item
     */
    @Override
    public Pageable nextPageable() {

        return new PageableImplementation(this.pageable.getPageNumber() + 1, this.pageable.getPageSize());
    }

    /**
     * Get method for the content of the current page
     *
     * @return the content of the current page
     */
    @Override
    public Stream<E> getContent() {

        return this.content;
    }
}
