package com.example.laborator5.socialnetwork.domain;

import java.time.LocalDateTime;

/**
 * This class retains all the fields of a friend request
 */
public class Request extends Entity<Long> {

    /**
     * The id of user1
     */
    private Long idUser1;

    /**
     * The id of user2
     */
    private Long idUser2;

    /**
     * The date of the request
     */
    private LocalDateTime date;

    /**
     * The status of the request
     */
    private Status status;

    /**
     * The constructor of the class without date
     *
     * @param id      - The id of the friendship
     * @param idUser1 - The id of the first user
     * @param idUser2 - The id of the second user
     * @param status  - The status of the request
     */
    public Request(Long id, Long idUser1, Long idUser2, String status) {

        this.setId(id);
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.date = LocalDateTime.now();
        this.status = Status.getBySymbol(status);
    }

    /**
     * The constructor of the class with date
     *
     * @param id      - The id of the friendship
     * @param idUser1 - The id of the first user
     * @param idUser2 - The id of the second user
     * @param date    - The date when the request was created
     * @param status  - The status of the request
     */
    public Request(Long id, Long idUser1, Long idUser2, LocalDateTime date, String status) {

        this.setId(id);
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.date = date;
        this.status = Status.getBySymbol(status);
    }

    /**
     * Get method for user's 1 id
     *
     * @return the id of the first user
     */
    public Long getIdUser1() {
        return idUser1;
    }

    /**
     * Set method for user's 1 id
     *
     * @param idUser1 the new id of the user
     */
    public void setIdUser1(Long idUser1) {
        this.idUser1 = idUser1;
    }

    /**
     * Get method for user's 2 id
     *
     * @return the id of the second user
     */
    public Long getIdUser2() {
        return idUser2;
    }

    /**
     * Set method for user's 2 id
     *
     * @param idUser2 the new id of the second user
     */
    public void setIdUser2(Long idUser2) {
        this.idUser2 = idUser2;
    }

    /**
     * Get method for the date of the request
     *
     * @return the date when request was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Set method for the date of the request
     *
     * @param date the new date of the request
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Get method for request status
     *
     * @return the status of the request
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Set method for request status
     *
     * @param status the new status
     */
    public void setStatus(Status status) {
        this.status = status;
    }
}
