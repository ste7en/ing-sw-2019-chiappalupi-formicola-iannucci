package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.socket.ClientSocketConnectionHandler;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.utility.Loggable;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Main class of the client. It will set up the networking and send/receive events and requests.
 *
 * @author Stefano Formicola
 */

public final class Client implements Loggable, ConnectionHandlerReceiverDelegate {
    /**
     * Instance attributes that describe the connection
     */
    private String serverName;
    private Integer connectionPort;
    private final ConnectionType connectionType;

    /**
     * Delegate class responsible to send messages
     */
    private ConnectionHandlerSenderDelegate senderDelegate;

    /**
     * Log strings
     */
    private static String UNKNOWN_HOST = "Can't find the hostname. Asking for user input...";
    private static String IO_EXC = "An IOException has been thrown. Connection retrying...";
    private static String ON_SUCCESS = "Client successfully connected to the server.";
    private static String ASK_SERVER_DETAILS = "Asking for server hostname and connection port.";

    /**
     * Class constructor
     *
     * @param connectionType to distinguish between RMI or SOCKET connections
     * @param host hostname (IP address)
     * @param port port number of the listening server
     */
    public Client(ConnectionType connectionType, String host, Integer port) {
        this.serverName = host;
        this.connectionPort = port;
        this.connectionType = connectionType;

        setupConnection();
    }

    private void setupConnection() {
        AdrenalineLogger.info("Setting up connection...");
        AdrenalineLogger.config("A " + connectionType.toString() + " connection is setting up...");
        if (connectionType == ConnectionType.SOCKET) {
            try {
                senderDelegate = new ClientSocketConnectionHandler(serverName, connectionPort, this);
                logOnSuccess(ON_SUCCESS);
            } catch (UnknownHostException e) {
                logOnException(UNKNOWN_HOST, e);
                askForUserInput();
                setupConnection();
            } catch (IOException e) {
                logOnException(IO_EXC, e);
                setupConnection();
            }

        } else {

        }
    }

    /**
     * Helper strings for the following method
     */
    private static String ASK_HOSTNAME = "Please, insert server's address or hostname: ";
    private static String ASK_PORTNUMBER = "Please, insert server's port number";

    /**
     * When an UnknownHostException is thrown, the user is asked to
     * insert server details again.
     */
    private void askForUserInput() {
        AdrenalineLogger.info(ASK_SERVER_DETAILS);
        var console = System.console();
        console.flush();
        this.serverName = console.readLine(ASK_HOSTNAME);
        this.connectionPort = Integer.parseInt(console.readLine(ASK_PORTNUMBER));
    }

    /**
     * Receives a message from a delegator
     * @param message
     */
    @Override
    public void receive(String message) {
        System.out.println(message);
    }

    public void send(String message) {
        senderDelegate.send(message);
    }
}
