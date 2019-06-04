package it.polimi.ingsw.networking;

/**
 * Delegator interface used to send messages through
 * the network and implemented by ClientSocket and Server classes.
 *
 * @author Stefano Formicola
 */
public interface ConnectionHandlerSenderDelegate {
    /**
     * Sends a message through its connection
     * @param message the message to send
     */
    void send(String message);

    /**
     * @return true if the socket is connected and the connection is available
     */
    boolean isConnectionAvailable();
}
