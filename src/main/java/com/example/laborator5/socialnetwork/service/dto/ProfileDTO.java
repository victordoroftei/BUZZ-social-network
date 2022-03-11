package com.example.laborator5.socialnetwork.service.dto;

import java.time.LocalDate;

/**
 * DTO Class for profiles.
 */
public class ProfileDTO {

    /**
     * The user associated to the profile.
     */
    private UserDTO user;

    /**
     * The about me section of the profile.
     */
    private String aboutMe;

    /**
     * The hometown of the user.
     */
    private String homeTown;

    /**
     * The birthday of the user.
     */
    private LocalDate birthday;

    /**
     * The hobbies section of the profile.
     */
    private String hobbies;

    /**
     * Constructor of the ProfileDTO class.
     *
     * @param user     the user associated to the profile
     * @param aboutMe  the about me section of the profile
     * @param homeTown the hometown of the user
     * @param birthday the birthday of the user
     * @param hobbies  the hobbies section of the profile
     */
    public ProfileDTO(UserDTO user, String aboutMe, String homeTown, LocalDate birthday, String hobbies) {

        this.user = user;
        this.aboutMe = aboutMe;
        this.homeTown = homeTown;
        this.birthday = birthday;
        this.hobbies = hobbies;

    }

    /**
     * Getter method for the user associated to the profile.
     *
     * @return the user associated to the profile
     */
    public UserDTO getUser() {

        return user;
    }

    /**
     * Setter method for the user associated to the profile.
     *
     * @param user the new user associated to the profile
     */
    public void setUser(UserDTO user) {

        this.user = user;
    }

    /**
     * Getter method for the about me section of the profile.
     *
     * @return the about me section of the profile
     */
    public String getAboutMe() {

        return aboutMe;
    }

    /**
     * Setter method for the about me section of the profile.
     *
     * @param aboutMe the new about me section of the profile
     */
    public void setAboutMe(String aboutMe) {

        this.aboutMe = aboutMe;
    }

    /**
     * Getter method for the hometown.
     *
     * @return the hometown
     */
    public String getHomeTown() {

        return homeTown;
    }

    /**
     * Setter method for the hometown.
     *
     * @param homeTown the new hometown
     */
    public void setHomeTown(String homeTown) {

        this.homeTown = homeTown;
    }

    /**
     * Getter method for the birthday.
     *
     * @return the birthday
     */
    public LocalDate getBirthday() {

        return birthday;
    }

    /**
     * Setter method for the birthday.
     *
     * @param birthday the new birthday
     */
    public void setBirthday(LocalDate birthday) {

        this.birthday = birthday;
    }

    /**
     * Getter method for the hobbies section of the profile.
     *
     * @return the hobbies section of the profile
     */
    public String getHobbies() {

        return hobbies;
    }

    /**
     * Setter method for the hobbies section of the profile.
     *
     * @param hobbies the new hobbies section of the profile
     */
    public void setHobbies(String hobbies) {

        this.hobbies = hobbies;
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

        if (!(o instanceof ProfileDTO))
            return false;

        ProfileDTO that = (ProfileDTO) o;
        return getUser().equals(that.getUser());
    }
}
