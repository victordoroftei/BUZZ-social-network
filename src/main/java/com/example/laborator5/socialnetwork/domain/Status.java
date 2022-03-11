package com.example.laborator5.socialnetwork.domain;

/**
 * This enum retains the statuses which a request can have
 */
public enum Status {

    /**
     * The values of the enum
     */
    PENDING("pending"), APPROVED("approved"), REJECTED("rejected");

    /**
     * The field representing the status of the request
     */
    private String status;

    /**
     * The constructor of the class
     *
     * @param status - the status as a string
     */
    Status(String status) {

        this.status = status;
    }

    /**
     * This method returns the status as enum by a given status a string
     *
     * @param stat - the status as a string
     * @return Status enum
     * null, if there was no match between an enum component and our string
     */
    public static Status getBySymbol(String stat) {

        for (Status st : Status.values())

            if (st.status.equals(stat))

                return st;

        return null;
    }

    /**
     * Get method for status as string
     *
     * @return status as string
     */
    public String getStatus() {

        return status;
    }
}
