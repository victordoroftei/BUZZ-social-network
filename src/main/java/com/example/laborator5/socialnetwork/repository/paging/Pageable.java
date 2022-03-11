package com.example.laborator5.socialnetwork.repository.paging;

/**
 * The interface of pageable
 */
public interface Pageable {

    /**
     * This method returns the page number
     *
     * @return the number of the page
     */
    int getPageNumber();

    /**
     * This method returns the page size
     *
     * @return the size of the page
     */
    int getPageSize();
}
