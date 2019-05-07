package it.polimi.ingsw.networking;

/**
 * Delegator interface used to send messages through
 * the network and implemented by Client and Server classes.
 *
 * @author Stefano Formicola
 */
public interface ConnectionHandlerSenderDelegate {
    /**
     * Sends a message through its connection
     */
    public void send();
}
