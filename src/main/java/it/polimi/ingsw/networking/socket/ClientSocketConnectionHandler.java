package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.networking.ConnectionHandlerSenderDelegate;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.ConnectionState;
import it.polimi.ingsw.utility.Loggable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.*;

import static it.polimi.ingsw.networking.utility.ConnectionState.*;

/**
 * Class used by the client to handle a socket connection
 * with the server and send/receive messages or requests.
 *
 * @author Stefano Formicola
 */

public class ClientSocketConnectionHandler implements ConnectionHandlerSenderDelegate, Runnable, Loggable {

    /**
     * The socket instance, the object that represents a connection
     * between client and server.
     */
    private Socket socket;

    /**
     * Server name used in the connection, basically it's the
     * IPv4 address of the server.
     */
    private String serverName;

    /**
     * The port number used for the handled connection
     */
    private Integer portNumber;

    /**
     * A delegate class used to handle received messages
     */
    private ConnectionHandlerReceiverDelegate receiverDelegate;

    /**
     * A buffered outbox for messages
     */
    private ConcurrentLinkedQueue<String> outBuf;

    /**
     * The state of the connection
     */
    private ConnectionState connectionState;

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S008")
    private static final String INIT_EXCEPTION = "An error has occurred while connecting to the server. ";
    private static final String UNKNOWN_HOST = "Unable to find the hostname provided.";
    private static final String IO_EXC_STREAMS = "An IOException has occurred while trying to get in/out streams.";
    private static final String STREAM_SUCC = "Socket Input/Output streams successfully created.";
    private static final String IO_EXC_CLOSING = "An IOException has occurred while trying to close the socket.";
    private static final String CONN_CLOSED = "Connection closed with the server :: ";
    private static final String INTERRUPTED_EXCEPTION = "Thread interrupted exception.";

    /**
     * Socket timeout
     */
    private static final int SOCKET_SO_TIMEOUT_MILLIS = 500;

    /**
     * Public constructor of the socket connection between client and server
     * @param server IP address of the server
     * @param port socket port number of the listening server
     * @param receiverDelegate the ClientSocket instance, responsible to handle received messages
     */
    public ClientSocketConnectionHandler(String server, Integer port, ConnectionHandlerReceiverDelegate receiverDelegate) throws IOException {
        this.serverName = server;
        this.portNumber = port;
        this.receiverDelegate = receiverDelegate;
        this.outBuf = new ConcurrentLinkedQueue<>();
        this.connectionState = ONLINE;

        try {
            this.socket = new Socket(serverName, portNumber);
            var clientSocketConnectionHandlerThread = new Thread(this);
            clientSocketConnectionHandlerThread.setPriority(Thread.MAX_PRIORITY);
            clientSocketConnectionHandlerThread.start();
        } catch (UnknownHostException e) {
            logOnException(INIT_EXCEPTION+UNKNOWN_HOST, e);
            throw e;
        } catch (IOException e) {
            logOnException(INIT_EXCEPTION+ IO_EXC_STREAMS, e);
            throw e;
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try (var outStr = socket.getOutputStream();
             var inStr = socket.getInputStream()) {

            var inScanner = new Scanner(inStr);
            var printWriter = new PrintWriter(outStr, true);
            logOnSuccess(STREAM_SUCC);

            this.socket.setSoTimeout(SOCKET_SO_TIMEOUT_MILLIS);

            while(connectionState == ONLINE) {
                if (inStr.available() != 0) {
                    var received = inScanner.nextLine();
                    receiverDelegate.receive(received, this);
                }
                if (!outBuf.isEmpty()) outBuf.forEach(printWriter::println);
                outBuf.clear();
                Thread.sleep(100);
            }
            if (connectionState == CLOSED) socket.close();
            AdrenalineLogger.info(CONN_CLOSED+socket.toString());
        } catch (IOException e) {
            logOnException(IO_EXC_STREAMS, e);
            close();
        } catch (InterruptedException e) {
            logOnException(INTERRUPTED_EXCEPTION , e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Closes the connection and handles an IOException
     */
    private void close() {
        try {
            connectionState = CLOSED;
            socket.close();
        } catch (IOException e) {
            logOnException(IO_EXC_CLOSING, e);
        }
    }

    /**
     * Sends a message through its connection
     * @param message the message to send
     */
    @Override
    public void send(String message) {
        outBuf.add(message);
    }

    /**
     * @return true if the socket is connected and the connection is available
     */
    @Override
    public boolean isConnectionAvailable() {
        return connectionState == ONLINE;
    }
}
