package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.networking.ClientConnectionHandler;
import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.utility.ConnectionState;
import it.polimi.ingsw.utility.Loggable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.utility.ConnectionState.*;

/**
 * Class used by the client to handle a socket connection
 * with the server and send/receive messages or requests.
 *
 * @author Stefano Formicola
 */

public class ClientSocketConnectionHandler extends ClientConnectionHandler implements Runnable, Loggable {

    /**
     * The socket instance, the object that represents a connection
     * between client and server.
     */
    private Socket socket;

    /**
     * A buffered outbox for messages
     */
    private LinkedList<String> outBuf;

    /**
     * The state of the connection
     */
    private ConnectionState connectionState;

    /**
     * Log strings
     */
    private static String INIT_EXCEPTION = "An error has occurred while connecting to the server. ";
    private static String UNKNOWN_HOST = "Unable to find the hostname provided.";
    private static String IO_EXC_STREAMS = "An IOException has occurred while trying to get in/out streams.";
    private static String STREAM_SUCC = "Socket Input/Output streams successfully created.";
    private static String IO_EXC_CLOSING = "An IOException has occurred while trying to close the socket.";
    private static String CONN_CLOSED = "Connection closed with the server :: ";

    /**
     * Socket timeout
     */
    private static final int SOCKET_SO_TIMEOUT_MILLIS = 1000;

    /**
     * Public constructor of the socket connection between client and server
     * @param server IP address of the server
     * @param port socket port number of the listening server
     * @param receiverDelegate the Client instance, responsible to handle received messages
     */
    public ClientSocketConnectionHandler(String server, Integer port, ConnectionHandlerReceiverDelegate receiverDelegate) throws IOException {
        super.serverName = server;
        super.portNumber = port;
        super.receiverDelegate = receiverDelegate;
        this.outBuf = new LinkedList<>();
        this.connectionState = ONLINE;

        try {
            this.socket = new Socket(serverName, portNumber);
            Executors.newCachedThreadPool().submit(this);
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
                    receiverDelegate.receive(received);
                }
                if (!outBuf.isEmpty()) printWriter.println(outBuf.pop());
            }

            if (connectionState == CLOSED) socket.close();
            AdrenalineLogger.info(CONN_CLOSED+socket.toString());
        } catch (IOException e) {
            logOnException(IO_EXC_STREAMS, e);
            close();
        }
    }

    /**
     * Closes the connection and handles an IOException
     */
    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logOnException(IO_EXC_CLOSING, e);
        }
    }

    /**
     * Sends a message through its connection
     * @param message the message to send
     */
    public void send(String message) {
        outBuf.addLast(message);
    }
}
