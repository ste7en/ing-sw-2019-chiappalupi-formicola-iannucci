package it.polimi.ingsw.networking;

import it.polimi.ingsw.model.User;
import java.util.List;

/**
 * Class implemented by a server to interact with a WaitingRoom
 * @author Stefano Formicola
 */
public interface WaitingRoomObserver {

    /**
     * Method called by a WaitingRoom instance on the implementing server
     * to start a new game when the minimum number of logged users has
     * reached and after a timeout expiration.
     *
     * @param userList a collection of the logged users ready to start a game
     */
    void startNewGame(List<User> userList);

}
