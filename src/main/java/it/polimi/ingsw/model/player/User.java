package it.polimi.ingsw.model.player;

/**
 * The User class describes a single user of the network
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 */
public class User {

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
    }

    /**
     * User's username getter
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Used in HashMap objects, to identify the user
     * @return an hashcode of the user's username
     */
    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }

    /**
     * Reference and username equality. Two user instances are equal
     * when they have the same object reference or the same username.
     * @param obj a user object
     * @return true if the two users are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true; // reference equality

        if (obj instanceof User) {
            return ((User) obj).getUsername().equals(this.getUsername()); // User equality
        }
        return false;
    }
}