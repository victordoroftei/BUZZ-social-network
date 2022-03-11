package com.example.laborator5.socialnetwork.repository.paging;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class contains information about the paginator
 *
 * @param <E> the type of elements
 */
public class Paginator<E> {

    /**
     * The current pageable
     */
    private Pageable pageable;

    /**
     * The elements
     */
    private Iterable<E> elements;

    /**
     * The constructor of our class
     *
     * @param pageable - the pageable object
     * @param elements - the elements
     */
    public Paginator(Pageable pageable, Iterable<E> elements) {

        this.pageable = pageable;
        this.elements = elements;
    }

    /**
     * This is the method responsible with the pagination
     *
     * @return the requested page
     */
    public Page<E> paginate() {

        Stream<E> result = StreamSupport.stream(elements.spliterator(), false)
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize());

        return new PageImplementation<>(pageable, result);
    }
}
