package com.example.laborator5.socialnetwork.repository.paging;

/**
 * The implementation of the Pageable interface
 */
public class PageableImplementation implements Pageable {

    /**
     * The number of the current page
     */
    private int pageNumber;

    /**
     * The size of the page
     */
    private int pageSize;

    /**
     * The constructor of our class
     *
     * @param pageNumber - the number of the page
     * @param pageSize   - the size of the page
     */
    public PageableImplementation(int pageNumber, int pageSize) {

        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * Get method for page number
     *
     * @return the number of the page
     */
    @Override
    public int getPageNumber() {

        return this.pageNumber;
    }

    /**
     * Get method for the page size
     *
     * @return the size of the page
     */
    @Override
    public int getPageSize() {

        return this.pageSize;
    }
}
