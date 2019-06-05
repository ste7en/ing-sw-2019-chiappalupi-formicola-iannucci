package it.polimi.ingsw.networking;

import it.polimi.ingsw.utility.Loggable;

/**
 * Abstract superclass of Server's connectionHandlers for both
 * RMI and Socket connections.
 *
 * @author Stefano Formicola
 */

public abstract class ServerConnectionHandler implements Loggable {
    /**
     * The server instance which exposes communication methods
     */
    protected Server server;

    /**
     * @return true if the connection is available
     */
    public abstract boolean isConnectionAvailable();

}