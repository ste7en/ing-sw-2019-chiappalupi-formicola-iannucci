package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.utility.Pingable;
import it.polimi.ingsw.utility.Loggable;

import java.util.List;
import java.util.UUID;

/**
 * Abstract superclass of Server's connectionHandlers for both
 * RMI and Socket connections.
 *
 * @author Stefano Formicola
 */

public abstract class ServerConnectionHandler implements Loggable, Pingable {
    /**
     * The server instance which exposes communication methods
     */
    protected Server server;

    /**
     * @return true if the connection is available
     */
    public abstract boolean isConnectionAvailable();

    /**
     * Notifies the client about a game that is going to start
     * @param gameID game identifier
     */
    protected abstract void gameDidStart(String gameID);

    /**
     * Asks the client to choose a character to start the game
     * @param availableCharacters a list of character colors
     */
    protected abstract void willChooseCharacter(List<String> availableCharacters);

    /**
     * When the user chooses a character, this method verifies its availability
     * and notifies the user if the character can be selected or not.
     * @param gameID gameID
     * @param userID userID
     * @param chosenCharacterColor character's color
     */
    protected abstract void didChooseCharacter(UUID gameID, int userID, String chosenCharacterColor);

    /**
     * When the user has to choose a game map configuration
     * @param gameID gameID
     */
    protected abstract void willChooseGameMap(UUID gameID);

    /**
     * Starts the turn of a new player.
     * @param userID it's the ID of the user.
     */
    protected abstract void startNewTurn(int userID);

    /**
     * Starts the game of a new player that isn't the first one.
     * @param userID it's the ID of the user.
     */
    protected abstract void startNewTurnFromRespawn(int userID);

    /**
     * Displays any change that occurred during other players actions
     * @param userID it's the ID of the user.
     * @param newBoardSituation it's the updated situation of the board
     */
    protected abstract void displayChanges(int userID, String newBoardSituation);
}