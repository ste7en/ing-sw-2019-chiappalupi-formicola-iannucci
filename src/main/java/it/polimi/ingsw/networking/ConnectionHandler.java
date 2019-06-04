package it.polimi.ingsw.networking;

import it.polimi.ingsw.utility.Loggable;

/**
 * Abstract superclass of Server and ClientSocket's connectionHandlers.
 * This implements ConnectionHandlerSenderDelegate's methods in order to implement
 * sending methods through the network with a delegator pattern and a protected
 * ConnectionHandlerReceiverDelegate instance attribute to process received messages.
 *
 * @author Stefano Formicola
 */

public abstract class ConnectionHandler implements ConnectionHandlerSenderDelegate, Loggable {
    /**
     * The port number used for the handled connection
     */
    protected Integer portNumber;

    /**
     * A delegate class used to handle received messages
     */
    protected ConnectionHandlerReceiverDelegate receiverDelegate;
}
