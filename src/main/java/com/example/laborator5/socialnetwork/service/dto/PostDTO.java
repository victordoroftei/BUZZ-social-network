package com.example.laborator5.socialnetwork.service.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The DTO of a post object
 */
public class PostDTO {

    /**
     * The id of the post
     */
    private Long id;

    /**
     * The owner of the post
     */
    private UserDTO user;

    /**
     * The content of the post
     */
    private String content;

    /**
     * The date when the post was posted
     */
    private LocalDateTime postedOn;

    /**
     * The constructor of the class
     *
     * @param id       - the id of the post
     * @param user     - the owner of the post
     * @param content  - the content of the post
     * @param postedOn - the date when the post was posted
     */
    public PostDTO(Long id, UserDTO user, String content, LocalDateTime postedOn) {

        this.id = id;
        this.user = user;
        this.content = content;
        this.postedOn = postedOn;

    }

    /**
     * Get method for the id
     *
     * @return the id
     */
    public Long getId() {

        return id;
    }

    /**
     * Set method for the id
     *
     * @param id - the new id
     */
    public void setId(Long id) {

        this.id = id;
    }

    /**
     * Get method for the user
     *
     * @return the user
     */
    public UserDTO getUser() {

        return user;
    }

    /**
     * Set method for the user
     *
     * @param user - the new user
     */
    public void setUser(UserDTO user) {

        this.user = user;
    }

    /**
     * Get method for the content
     *
     * @return the content of the post
     */
    public String getContent() {

        return content;
    }

    /**
     * Set method for the content
     *
     * @param content - the new content
     */
    public void setContent(String content) {

        this.content = content;
    }

    /**
     * Get method for the date
     *
     * @return the date of the post
     */
    public LocalDateTime getPostedOn() {

        return postedOn;
    }

    /**
     * Set method for the date
     *
     * @param postedOn - the new date
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

        if (!(o instanceof PostDTO))
            return false;

        PostDTO that = (PostDTO) o;
        return getId().equals(that.getId());

    }

    /**
     * Overridden toString method.
     *
     * @return a string containing the object's data
     */
    @Override
    public String toString() {

        return this.getUser().toString() + "\n" + this.content + "\nPosted on: " + this.getPostedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
