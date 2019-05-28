package it.polimi.ingsw.networking.utility;

/**
 * Interface used to make a ServerSocketClientConnection
 * Ping methods compliant, in order to ping client connections.
 *
 * @author Stefano Formicola
 */
public interface Pingable {

    /**
     * The ServerSocketClientConnection calling this method will
     * send a PING message to the client when asked.
     */
    void ping();

    /**
     * The Ping handler class will call this method if the
     * client does not respond to a PING message after a
     * determined delay.
     */
    void closeConnection();

    /**
     * Used to distinguish between connected pingable clients
     * @return a Pingable instance identifier
     */
    int getConnectionHashCode();
}