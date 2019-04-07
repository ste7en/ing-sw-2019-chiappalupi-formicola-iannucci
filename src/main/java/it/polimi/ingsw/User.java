package it.polimi.ingsw;

import java.util.*;

/**
 *
 * @author  Daniele Chiappalupi
 */
public class User {

    /**
     * ID of the user
     */
    private final UUID userID;

    /**
     * Username of the user
     */
    private final String username;

    /**
     * Constructor: creates a new User from a username and generating a random UUID
     * @param username username of the user
     */
    public User(String username) {
        this.username = username;
        userID = UUID.randomUUID();
    }

    /**
     * User's ID getter
     * @return the ID of the user
     */
    public UUID getUserID() {
        return userID;
    }

    /**
     * User's username getter
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

}