package it.polimi.ingsw;

import java.util.*;

public class User {

    private final UUID userID;
    private final String username;

    public User(UUID userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

}