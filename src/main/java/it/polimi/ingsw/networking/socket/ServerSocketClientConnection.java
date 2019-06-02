package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.networking.ConnectionHandlerSenderDelegate;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionState;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.networking.utility.Pingable;
import it.polimi.ingsw.utility.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static it.polimi.ingsw.networking.utility.ConnectionState.*;

/**
 * This class is responsible for handling a single client socket connection,
 * it implements {@link Runnable} interface and runs into a single thread.
 *
 * @author Stefano Formicola
 */
public class ServerSocketClientConnection implements Runnable, Loggable, Pingable, ConnectionHandlerSenderDelegate {

    /**
     * The socket instance, the object that represents a connection
     * between client and server.
     */
    private Socket socket;

    /**
     * The server that will handle a received message
     */
    private ConnectionHandlerReceiverDelegate receiverDelegate;

    /**
     * A buffered outbox for messages
     */
    private ConcurrentLinkedQueue<String> outBuf;

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S3008")
    private static final String IO_EXC = "An IOException has occurred during a socket operation";
    private static final String STREAM_SUCC = "Socket Input/Output streams successfully created.";
    private static final String CONN_CLOSED = "Connection closed with the client :: ";
    private static final String PING_TIMEOUT = "Ping timeout. Closing connection socket :: ";
    private static final String INTERRUPTED_EXCEPTION = "Thread interrupted exception.";

    /**
     * The state of the connection
     */
    private ConnectionState connectionState;

    /**
     * Basic class constructor
     * @param socket socket connection with the client
     * @param receiverDelegate the server who will handle the received message
     */
    ServerSocketClientConnection(Socket socket, ConnectionHandlerReceiverDelegate receiverDelegate) {
        this.socket = socket;
        this.receiverDelegate = receiverDelegate;
        this.outBuf = new ConcurrentLinkedQueue<>();
        this.connectionState = ONLINE;
    }

    /**
     * Method called in a new thread when the object is created.
     */
    @Override
    public void run() {
        Ping.getInstance().addPing(this);

        try(var outStr = socket.getOutputStream();
            var inStr  = socket.getInputStream()) {

            var inScanner = new Scanner(inStr);
            var printWriter = new PrintWriter(outStr, true);
            logOnSuccess(STREAM_SUCC);

            while(connectionState == ONLINE) {
                if (inStr.available() != 0) receiverDelegate.receive(inScanner.nextLine(), this);
                if (!outBuf.isEmpty()) outBuf.forEach(printWriter::println);
                outBuf.clear();
                Thread.sleep(100);
            }
            if (connectionState == CLOSED) socket.close();
            AdrenalineLogger.info(CONN_CLOSED + socket.toString());
        } catch (IOException e) {
            logOnException(IO_EXC, e);
        } catch (InterruptedException e) {
            logOnException(INTERRUPTED_EXCEPTION , e);
            Thread.currentThread().interrupt();
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
     * The ServerSocketClientConnection calling this method will
     * send a PING message to the client when asked.
     */
    @Override
    public void ping() {
        var pingMessage = CommunicationMessage.from(getConnectionHashCode(), CommunicationMessage.PING);
        this.send(pingMessage);
    }

    /**
     * The Ping handler class will call this method if the
     * client does not respond to a PING message after a
     * determined delay.
     */
    @Override
    public synchronized void closeConnection() {
        this.connectionState = ConnectionState.CLOSED;
        AdrenalineLogger.info(PING_TIMEOUT + socket.toString());
    }

    /**
     * Used to distinguish between connected pingable clients
     *
     * @return a Pingable instance identifier
     */
    @Override
    public int getConnectionHashCode() {
        return this.hashCode();
    }

    /**
     * @return true if the socket is connected and the connection is available
     */
    @Override
    public boolean isConnectionAvailable() {
        return connectionState == ONLINE;
    }
}