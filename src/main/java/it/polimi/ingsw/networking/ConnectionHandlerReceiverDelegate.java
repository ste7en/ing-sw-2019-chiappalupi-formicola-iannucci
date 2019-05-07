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
     */
    public void receive();
}
