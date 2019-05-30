package it.polimi.ingsw.networking;

import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.networking.rmi.ClientRMIConnectionHandler;
import it.polimi.ingsw.networking.socket.ClientSocketConnectionHandler;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.utility.Loggable;
import it.polimi.ingsw.view.View;

import java.io.*;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;

/**
 * Main class of the client. It will set up the networking and send/receive events and requests.
 *
 * @author Stefano Formicola
 */
public final class Client implements Loggable, ConnectionHandlerReceiverDelegate {

    /**
     * Instance attributes that describe the connection
     */
    private String  serverName;
    private Integer connectionPort;
    private View    viewObserver;
    private final ConnectionType connectionType;

    /**
     * Delegate class responsible to send messages
     */
    private ConnectionHandlerSenderDelegate senderDelegate;

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S3008")
    private static final String UNKNOWN_HOST       = "Can't find the hostname. Asking for user input...";
    private static final String IO_EXC             = "An IOException has been thrown. ";
    private static final String CONN_RETRY         = "Connection retrying...";
    private static final String ON_SUCCESS         = "Client successfully connected to the server.";
    private static final String ASK_SERVER_DETAILS = "Asking for server hostname and connection port.";
    private static final String INFO               = "Setting up connection...";
    private static final String CONNECTION_REFUSED = "Connection refused. Asking for user input...";
    private static final String OBS_REGISTERED     = "Observer successfully registered.";

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

    public void registerObserver(View viewObserver) {
        this.viewObserver = viewObserver;
        AdrenalineLogger.config(OBS_REGISTERED);
    }

    private void setupConnection() {
        AdrenalineLogger.info(INFO);
        AdrenalineLogger.config("A " + connectionType.toString() + " connection is setting up...");
        if (connectionType == ConnectionType.SOCKET) {
            try {
                senderDelegate = new ClientSocketConnectionHandler(serverName, connectionPort, this);
                logOnSuccess(ON_SUCCESS);
            } catch (UnknownHostException e) {
                logOnException(UNKNOWN_HOST, e);
                askForUserInput();
                setupConnection();
            } catch (ConnectException e) {
                logOnException(CONNECTION_REFUSED, e);
                askForUserInput();
                setupConnection();
            }
            catch (IOException e) {
                logOnException(IO_EXC+CONN_RETRY, e);
                setupConnection();
            }

        } else {

        }
    }

    /**
     * Helper strings for the following method
     */
    private static final String ASK_HOST_NAME   = "Please, insert server's address or hostname: ";
    private static final String ASK_PORT_NUMBER = "Please, insert server's port number: ";

    /**
     * When an UnknownHostException is thrown, the user is asked to
     * insert server details again.
     */
    private void askForUserInput() {
        AdrenalineLogger.info(ASK_SERVER_DETAILS);
        var in = new BufferedReader(new InputStreamReader(System.in));
        @SuppressWarnings("squid:S106")
        var out = System.out;

        try {
            out.print(ASK_HOST_NAME);
            this.serverName = in.readLine();
            out.print(ASK_PORT_NUMBER);
            this.connectionPort = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            logOnException(IO_EXC, e);
        }
    }

    /**
     * Receives a json message from a delegator
     * and parses its content
     * @param message json message
     * @param sender a the connection handler delegated to send messages
     */
    @Override
    public void receive(String message, ConnectionHandlerSenderDelegate sender) {
        new Thread(
            () -> {
                var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
                var id = CommunicationMessage.getConnectionIDFrom(message);
                var args = CommunicationMessage.getMessageArgsFrom(message);

                switch (communicationMessage) {
                    case PING:
                        sender.send(CommunicationMessage.from(id, PONG));
                        break;
                    case CREATE_USER_OK:
                        this.viewObserver.onLoginSuccess(args.get(User.username_key));
                        break;
                    case CREATE_USER_FAILED:
                        this.viewObserver.onLoginFailure();
                        break;
                    case DAMAGE_LIST: {
                        ArrayList<String> possibleDamages = new ArrayList<>();
                        possibleDamages.addAll(args.values());
                        this.viewObserver.willChooseDamage(possibleDamages);
                        break;
                    }
                    default:
                        break;
                }
            }
        ).start();
    }

    /**
     * Sends a json message through its sender delegate
     * @param message a json message to send
     */
    public void send(String message) {
        senderDelegate.send(message);
    }

}