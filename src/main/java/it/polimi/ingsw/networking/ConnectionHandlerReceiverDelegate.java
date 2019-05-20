package it.polimi.ingsw.networking;

/**
 * Delegator interface used to receive messages through
 * the network and implemented by Client and Server classes.
 *
 * @author Stefano Formicola
 */
public interface ConnectionHandlerReceiverDelegate {
    /**
     * Receives a message from a delegator
     * @param message the message received
     * @param sender a the connection handler delegated to send messages
     */
    void receive(String message, ConnectionHandlerSenderDelegate sender);
}
