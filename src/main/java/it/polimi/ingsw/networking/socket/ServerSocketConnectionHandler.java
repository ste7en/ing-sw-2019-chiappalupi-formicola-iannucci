package it.polimi.ingsw.networking.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.*;
import it.polimi.ingsw.utility.AdrenalineLogger;


/**
 * Class used by the server to handle and accept
 * socket connections from clients.
 *
 * @author Stefano Formicola
 */

public final class ServerSocketConnectionHandler extends ServerConnectionHandler {

    /**
     * Port number for socket connections
     */
    private int portNumber;

    /**
     * Log strings
     */
    private String EXC_ON_CLIENT_CONNECTION = "Error while trying to accept a client :: ";
    private String EXC_ON_SOCKET = "Error while creating a SocketServer :: ";
    private String SERVER_SOCKET_CONFIG = "ServerSocket configured on port ";
    private String SERVER_SOCKET_SUCCESS = "ServerSocket is running...";
    private String SOCKET_SUCCESS = "A new socket connection with a client has been created :: ";
    private String SERVER_SOCKET_SHUTDOWN = "SocketServer is shutting down and closing its connections";

    /**
     * Class constructor
     * @param portNumber port number on which the connection has to be opened
     */
    public ServerSocketConnectionHandler(int portNumber) throws IllegalPortNumber {
        if (portNumber > 65535 || portNumber < 1024) throw new IllegalPortNumber();
        this.portNumber = portNumber;
    }

    /**
     * Instance method used to start the socket server and accept incoming connections,
     * that are handled by the {@link ServerSocketClientConnection}.
     */
    private void startSocketServer() {
        ExecutorService executor = Executors.newCachedThreadPool();
        AdrenalineLogger.config(SERVER_SOCKET_CONFIG + portNumber);
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            logOnSuccess(SERVER_SOCKET_SUCCESS);
            logDescription();
            while(true) {
                try (Socket socket = serverSocket.accept()) {
                    logOnSuccess(SOCKET_SUCCESS + socket.toString());
                    executor.submit(new ServerSocketClientConnection(socket));
                } catch (IOException e) {
                    logOnException(EXC_ON_CLIENT_CONNECTION, e);
                    break;
                }
            }
        } catch (IOException e) {
            logOnException(EXC_ON_SOCKET, e);
            return;
        }
        AdrenalineLogger.info(SERVER_SOCKET_SHUTDOWN);
        executor.shutdown();
    }

    private void logDescription() {
        AdrenalineLogger.info(this.toString());
    }

    /**
     * Starts the listening server connection
     */
    @Override
    public void start() {
        startSocketServer();
    }

    /**
     * Sends a message through its connection
     */
    @Override
    public void send() {

    }
}
