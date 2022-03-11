package com.example.laborator5.socialnetwork.domain;

import java.time.LocalDateTime;

/**
 * The class that contains the fields of a post and the methods for a post
 */
public class Post extends Entity<Long> {

    /**
     * The id of the user
     */
    private Long idUser;

    /**
     * The content of the post
     */
    private String content;

    /**
     * The date when the post got posted
     */
    private LocalDateTime postedOn;

    /**
     * The constructor of the class
     *
     * @param idUser   - the id of the user
     * @param content  - the content of the post
     * @param postedOn - the date when the post got posted
     */
    public Post(Long idUser, String content, LocalDateTime postedOn) {

        this.idUser = idUser;
        this.content = content;
        this.postedOn = postedOn;
    }

    /**
     * Get method for the id of the user
     *
     * @return the id of the user
     */
    public Long getIdUser() {

        return idUser;
    }

    /**
     * Set method for the id of the user
     *
     * @param idUser - the new id of the user
     */
    public void setIdUser(Long idUser) {

        this.idUser = idUser;
    }

    /**
     * Get method for the content of the post
     *
     * @return the content of the post
     */
    public String getContent() {

        return content;
    }

    /**
     * Set method for the content of a post
     *
     * @param content - the new content of the post
     */
    public void setContent(String content) {

        this.content = content;
    }

    /**
     * Get method for the date
     *
     * @return the date when the post was posted
     */
    public LocalDateTime getPostedOn() {

        return postedOn;
    }

    /**
     * Set method for the date
     *
     * @param postedOn - the new date of the post
     */
    public void setPostedOn(LocalDateTime postedOn) {

        this.postedOn = postedOn;
    }

    /**
     * Overridden equals method.
     *
     * @param o the object that is compared to the current one
     * @return true, if the objects are equal; false, otherwise
     */
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof Post))
            return false;

        Post that = (Post) o;
        return getId().equals(that.getId());
    }
}
