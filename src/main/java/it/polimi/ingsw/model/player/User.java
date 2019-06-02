package it.polimi.ingsw.model.player;

import java.util.UUID;

/**
 * The User class describes a single user of the network
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
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
    public static final String username_key = "USERNAME";

    /**
     * Constructor: creates a new User from a username and generating a random UUID
     * @param username username of the user
     */
    public User(String username) {
        this.username = username;
        userID = UUID.randomUUID();
    }

    /**
     * Constructor: creates a new User from a username and a given UUID
     * @param username username of the user
     * @param uuid unique user identifier assigned by the server
     */
    public User(String username, UUID uuid) {
        this.username = username;
        userID = uuid;
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