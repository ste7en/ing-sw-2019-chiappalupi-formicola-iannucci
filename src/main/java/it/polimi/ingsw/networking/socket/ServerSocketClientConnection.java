package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.utility.Loggable;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * This class is responsible for handling a single client socket connection,
 * it implements {@link Runnable} interface and runs into a single thread.
 *
 * @author Stefano Formicola
 */
public class ServerSocketClientConnection implements Runnable, Loggable {

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
    private LinkedList<String> outBuf;

    /**
     * Log strings
     */
    private static String IO_EXC = "An IOException has occurred while trying to get in/out streams.";
    private static String STREAM_SUCC = "Socket Input/Output streams successfully created.";

    /**
     * Basic class constructor
     * @param socket socket connection with the client
     * @param receiverDelegate
     */
    ServerSocketClientConnection(Socket socket, ConnectionHandlerReceiverDelegate receiverDelegate) {
        this.socket = socket;
        this.receiverDelegate = receiverDelegate;
        this.outBuf = new LinkedList<>();
    }

    /**
     * Method called in a new thread when the object is created.
     */
    @Override
    public void run() {
        try(var outStr = socket.getOutputStream();
            var inStr = socket.getInputStream();
            var bufferedReader = new BufferedReader(
                    new InputStreamReader(inStr)
            );
            var printWriter = new PrintWriter(outStr)) {

            logOnSuccess(STREAM_SUCC);
            while(!socket.isClosed()) {
                var inBuf = bufferedReader.readLine();
                if (inBuf.length() != 0) receiverDelegate.receive(inBuf);
                if (!outBuf.isEmpty()) printWriter.println(outBuf.pop());
            }

        } catch (IOException e) {
            logOnException(IO_EXC, e);
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
