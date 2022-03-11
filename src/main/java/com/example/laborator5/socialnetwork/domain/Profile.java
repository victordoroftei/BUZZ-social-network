package com.example.laborator5.socialnetwork.domain;

import java.time.LocalDate;

/**
 * Class for a user's profile.
 */
public class Profile extends Entity<Long> {

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
     * Constructor for the Profile class.
     *
     * @param aboutMe  the about me section of the profile
     * @param homeTown the hometown of the user
     * @param birthday the birthday of the user
     * @param hobbies  the hobbies section of the profile
     */
    public Profile(String aboutMe, String homeTown, LocalDate birthday, String hobbies) {

        this.aboutMe = aboutMe;
        this.homeTown = homeTown;
        this.birthday = birthday;
        this.hobbies = hobbies;

    }

    /**
     * Getter method for the about me section of the profile.
     *
     * @return the about me section of the profile
     */
    public String getAboutMe() {

        return this.aboutMe;
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

        if (!(o instanceof Profile))
            return false;

        Profile that = (Profile) o;
        return getId().equals(that.getId());
    }
}
