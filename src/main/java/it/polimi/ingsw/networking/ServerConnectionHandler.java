package it.polimi.ingsw.networking;

/**
 * Abstract superclass of Server's connectionHandlers for both
 * RMI and Socket connections.
 *
 * @author Stefano Formicola
 */

public abstract class ServerConnectionHandler extends ConnectionHandler {
    /**
     * Starts the listening server connection
     */
    public abstract void start();
}
