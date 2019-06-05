package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionState;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.networking.utility.Pingable;
import it.polimi.ingsw.utility.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import static it.polimi.ingsw.networking.utility.ConnectionState.*;
import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * This class is responsible for handling a single client socket connection,
 * it implements {@link Runnable} interface and runs into a single thread.
 *
 * @author Stefano Formicola
 */
public class ServerSocketConnectionHandler extends ServerConnectionHandler implements Runnable, Pingable {

    /**
     * The socket instance, the object that represents a connection
     * between client and server.
     */
    private Socket socket;

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
     * @param server the server instance
     */
    ServerSocketConnectionHandler(Socket socket, Server server) {
        this.socket = socket;
        this.outBuf = new ConcurrentLinkedQueue<>();
        this.connectionState = ONLINE;
        super.server = server;
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
                if (inStr.available() != 0) receive(inScanner.nextLine());
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
    public void send(String message) {
        outBuf.add(message);
    }

    /**
     * The ServerSocketConnectionHandler calling this method will
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

    /**
     * Receives a message from a delegator
     * @param message the message received
     */
    private void receive(String message) {
        new Thread( () -> {
            var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
            var connectionID         = CommunicationMessage.getConnectionIDFrom(message);
            var args                 = CommunicationMessage.getMessageArgsFrom(message);
            var gameID               = CommunicationMessage.getMessageGameIDFrom(message);

            switch (communicationMessage) {
                case PONG:
                    Ping.getInstance().didPong(connectionID);
                    break;
                case CREATE_USER: {
                    createUser(connectionID, args);
                    break;
                }
                case USER_LOGIN: {
                    userLogin(connectionID, args);
                    break;
                }
                default:
                    break;
            }

        }).start();
    }

    /**
     * Method called by the client to create a new user
     * @param connectionID message connection ID
     * @param args message arguments
     */
    private void createUser(int connectionID, Map<String, String> args) {
        var username = args.get(User.username_key);
        var user = new User(username);
        var responseArgs = new HashMap<String, String>();
        String responseMessage;

        if (server.createUser(user, this)) {
            responseMessage = CommunicationMessage.from(connectionID, CREATE_USER_OK, responseArgs);
        } else {
            responseMessage = CommunicationMessage.from(connectionID, CREATE_USER_FAILED, responseArgs);
        }
        send(responseMessage);
    }

    /**
     * Method called by the client to join a game or waiting room
     * @param connectionID message connection ID
     * @param args message arguments
     */
    private void userLogin(int connectionID, Map<String, String> args) {
        var username = args.get(User.username_key);
        var user = new User(username);
        server.userLogin(user);
        var responseArgs = new HashMap<String, String>();
        responseArgs.put(User.username_key, username);
        var responseMessage = CommunicationMessage.from(connectionID, USER_JOINED_WAITING_ROOM, responseArgs);
        send(responseMessage);
    }
}