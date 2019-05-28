package it.polimi.ingsw.networking.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.*;
import it.polimi.ingsw.utility.AdrenalineLogger;


/**
 * Class used by the server to handle and accept
 * socket connections from clients.
 *
 * @author Stefano Formicola
 */

public final class ServerSocketConnectionHandler extends ServerConnectionHandler implements Runnable {
    /**
     * List of connections, used in order to send messages to connected clients.
     */
    private List<ServerSocketClientConnection> senders = new LinkedList<>();

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S008")
    private static final String EXC_ON_CLIENT_CONNECTION = "Error while trying to accept a client :: ";
    private static final String EXC_ON_SOCKET = "Error while creating a SocketServer :: ";
    private static final String SERVER_SOCKET_CONFIG = "ServerSocket configured on port ";
    private static final String SERVER_SOCKET_SUCCESS = "ServerSocket is running...";
    private static final String SOCKET_SUCCESS = "A new socket connection with a client has been created :: ";
    private static final String SERVER_SOCKET_SHUTDOWN = "SocketServer is shutting down and closing its connections";
    private static final String INTERRUPTED_EXCEPTION = "Thread interrupted exception.";

    /**
     * Socket timeout
     */
    private static final int SOCKET_SO_TIMEOUT = 250;

    /**
     * Class constructor
     * @param portNumber port number on which the connection has to be opened
     * @param receiverDelegate the server object
     */
    public ServerSocketConnectionHandler(int portNumber, ConnectionHandlerReceiverDelegate receiverDelegate) {
        if (portNumber > 65535 || portNumber < 1024) throw new IllegalPortNumber();
        super.portNumber = portNumber;
        super.receiverDelegate = receiverDelegate;
    }

    /**
     * Instance method used to start the socket server and accept incoming connections,
     * that are handled by the {@link ServerSocketClientConnection}.
     */
    private void startSocketServer() {
        AdrenalineLogger.config(SERVER_SOCKET_CONFIG + portNumber);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

            logOnSuccess(SERVER_SOCKET_SUCCESS);
            logDescription(serverSocket);

            while(!serverSocket.isClosed()) {
                acceptConnection(serverSocket);
                Thread.sleep(500);
            }

        } catch (IOException e) {
            logOnException(EXC_ON_SOCKET + e.getLocalizedMessage(), e);
            return;
        } catch (InterruptedException e) {
            logOnException(INTERRUPTED_EXCEPTION, e);
            Thread.currentThread().interrupt();
        }

        AdrenalineLogger.info(SERVER_SOCKET_SHUTDOWN);
    }

    /**
     * Private convenience method used to accept incoming connections
     * @param serverSocket a serverSocket object on which connection requests are sent
     * @throws IOException exception thrown when a connection is accepted/opened
     */
    private void acceptConnection(ServerSocket serverSocket) throws IOException {
        try {
            var socket = serverSocket.accept();
            socket.setSoTimeout(SOCKET_SO_TIMEOUT);
            logOnSuccess(SOCKET_SUCCESS + socket.toString());

            var connection = new ServerSocketClientConnection(socket, receiverDelegate);

            senders.add(connection);
            var connectionThread = new Thread(connection);
            connectionThread.setDaemon(true);
            connectionThread.setPriority(Thread.MIN_PRIORITY);
            connectionThread.start();
        } catch (IOException e) {
            logOnException(EXC_ON_CLIENT_CONNECTION, e);
            throw e;
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     *
     * Starts the listening server connection
     */
    @Override
    public void run() {
        startSocketServer();
    }

    /**
     * Sends a message through its connection
     * @param message message
     */
    @Override
    public void send(String message) {
        senders.forEach( s->s.send(message));
    }

    /**
     * @return true if the socket is connected and the connection is available
     */
    @Override
    public boolean isConnectionAvailable() {
        return true;
    }
}
