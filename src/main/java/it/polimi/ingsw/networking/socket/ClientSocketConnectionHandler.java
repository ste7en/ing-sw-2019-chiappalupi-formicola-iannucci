package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.networking.ClientConnectionHandler;
import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.utility.Loggable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.Executors;

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
     * Log strings
     */
    private static String INIT_EXCEPTION = "An error has occurred while connecting to the server. ";
    private static String UNKNOWN_HOST = "Unable to find the hostname provided.";
    private static String IO_EXC = "An IOException has occurred while trying to get in/out streams.";
    private static String STREAM_SUCC = "Socket Input/Output streams successfully created.";

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

        try {
            this.socket = new Socket(serverName, portNumber);
            Executors.newCachedThreadPool()
                    .submit(this);
        } catch (UnknownHostException e) {
            logOnException(INIT_EXCEPTION+UNKNOWN_HOST, e);
            throw e;
        } catch (IOException e) {
            logOnException(INIT_EXCEPTION+IO_EXC, e);
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

        } catch (Exception e) {
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
