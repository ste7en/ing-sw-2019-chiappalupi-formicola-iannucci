package it.polimi.ingsw.networking.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.networking.utility.*;


/**
 * Class used by the server to handle and accept
 * socket connections from clients.
 *
 * @author Stefano Formicola
 */

public final class ServerSocketConnectionHandler {

    /**
     * Port number for socket connections
     */
    private int portNumber;

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
     * that are handled by the {@link ServerSocketClientHandler}.
     */
    public void startSocketServer() {
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while(true) {
                try (Socket socket = serverSocket.accept()) {
                    executor.submit(new ServerSocketClientHandler(socket));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        executor.shutdown();
    }
}
