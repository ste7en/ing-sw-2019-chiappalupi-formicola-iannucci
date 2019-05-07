package it.polimi.ingsw.networking;

/**
 * Abstract superclass of Client's connectionHandlers for both
 * Socket and RMI connections.
 *
 * @author Stefano Formicola
 */

public abstract class ClientConnectionHandler extends ConnectionHandler {
    /**
     * Server name used in the connection, basically it's the
     * IPv4 address of the server.
     */
    protected String serverName;
}
