package it.polimi.ingsw.networking.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class is responsible for handling a single client socket connection,
 * it implements {@link Runnable} interface and runs into a single thread.
 *
 * @author Stefano Formicola
 */
public class ServerSocketClientHandler implements Runnable {

    private Socket socket;

    /**
     * Basic class constructor
     * @param socket socket connection with the client
     */
    ServerSocketClientHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Method called in a new thread when the object is created.
     */
    @Override
    public void run() {
        try(ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());) {

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
