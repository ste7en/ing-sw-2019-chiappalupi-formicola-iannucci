package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.Loggable;

/**
 * Main class of the client. It will set up the networking and send/receive events and requests.
 *
 * @author Stefano Formicola
 */

public final class Client implements Loggable, ConnectionHandlerReceiverDelegate {

    private String serverName;
    private Integer connectionPort;
    private ConnectionType connectionType;
    private ConnectionHandlerSenderDelegate senderDelegate;

    public Client(ConnectionType connectionType, String host, Integer port) {
        this.serverName = host;
        this.connectionPort = port;
        this.connectionType = connectionType;

    }

    private void setupConnection() {
        switch (connectionType) {
            case SOCKET:
                break;
            case RMI:
                break;
        }
    }

    /**
     * Receives a message from a delegator
     * @param message
     */
    @Override
    public void receive(String message) {

    }
}
