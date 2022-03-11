package com.example.laborator5.socialnetwork.utils;

/**
 * Global "variable" for the number of entities displayed on one page of the paged repository.
 */
public final class EntitiesOnPage {

    /**
     * The number of entities on a page.
     */
    private static int number = 10;

    /**
     * Getter method for the number of entities displayed on one page.
     *
     * @return the number of entities
     */
    public static int getNumber() {

        return number;
    }

    /**
     * Setter method for the number of entities displayed on one page.
     *
     * @param number the new number of entities
     */
    public static void setNumber(int number) {

        EntitiesOnPage.number = number;
    }
}
