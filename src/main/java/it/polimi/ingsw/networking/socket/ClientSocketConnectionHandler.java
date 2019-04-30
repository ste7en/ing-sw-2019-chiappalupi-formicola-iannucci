package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.networking.utility.ClientConnectionHandler;
import it.polimi.ingsw.utility.Loggable;

/**
 * Class used by the client to handle a socket connection
 * with the server and send/receive messages or requests.
 *
 * @author Stefano Formicola
 */

public class ClientSocketConnectionHandler implements Loggable, Runnable, ClientConnectionHandler {

    private String serverName;
    private Integer socketPortNumber;

    public ClientSocketConnectionHandler(String server, Integer port) {
        this.serverName = server;
        this.socketPortNumber = port;

        this.run();
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

    }
}
