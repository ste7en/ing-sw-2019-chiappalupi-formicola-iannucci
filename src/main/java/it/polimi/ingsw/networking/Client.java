package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.utility.Loggable;
import it.polimi.ingsw.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Abstract class that will be reimplemented in ClientSocket and ClientRMI.
 *
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 * @author Elena Iannucci
 */
public abstract class Client implements Loggable {

    /**
     * Instance attributes that describe the connection
     */
    protected String serverName;
    protected Integer connectionPort;
    protected View viewObserver;
    protected ConnectionType connectionType;
    //toDO: DELETE CONNECTION TYPE

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S3008")
    protected static final String UNKNOWN_HOST = "Can't find the hostname. Asking for user input...";
    protected static final String IO_EXC = "An IOException has been thrown. ";
    protected static final String CONN_RETRY = "Connection retrying...";
    protected static final String ON_SUCCESS = "ClientSocket successfully connected to the server.";
    protected static final String ASK_SERVER_DETAILS = "Asking for server hostname and connection port.";
    protected static final String INFO = "Setting up connection...";
    protected static final String CONNECTION_REFUSED = "Connection refused. Asking for user input...";
    protected static final String OBS_REGISTERED = "Observer successfully registered.";

    /**
     * Helper strings for the following method
     */
    protected static final String ASK_HOST_NAME = "Please, insert server's address or hostname: ";
    protected static final String ASK_PORT_NUMBER = "Please, insert server's port number: ";

    /**
     * Class constructor
     *
     * @param connectionType to distinguish between RMI or SOCKET connections
     * @param host           hostname (IP address)
     * @param port           port number of the listening server
     */
    public Client(ConnectionType connectionType, String host, Integer port) {
        this.serverName = host;
        this.connectionPort = port;
        this.connectionType = connectionType;

        setupConnection();
    }

    /**
     * Method that sets up the connection.
     * To be reimplemented in the sub-classes.
     */
    protected abstract void setupConnection();

    public void registerObserver(View viewObserver) {
        this.viewObserver = viewObserver;
        AdrenalineLogger.config(OBS_REGISTERED);
    }

    /**
     * When an UnknownHostException is thrown, the user is asked to
     * insert server details again.
     */
    protected void askForUserInput() {
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
     * Abstract method implemented by subclasses and used to create a new user
     * logged to the server.
     */
    public abstract void login(String username);

}
