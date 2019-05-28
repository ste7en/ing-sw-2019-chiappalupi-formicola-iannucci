package it.polimi.ingsw.model.player;

import java.util.UUID;

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
     * String constants used in messages between client-server
     */
    public final static String username_key = "USERNAME";

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