package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.*;
import it.polimi.ingsw.networking.socket.*;
import it.polimi.ingsw.networking.rmi.*;

/**
 * Main class of the game server. It will set up the networking and create controllers and games.
 * It is designed to support multiple games and players in a waiting room before starting a game,
 * plus it manages loss of connections and users re-connections.
 *
 * @author Stefano Formicola
 */
public class Server {

    private Integer portNumberSocket;
    private Integer portNumberRMI;

    public static void main(String args[]) {


    }

    /**
     * Server constructor responsible for setting up networking parameters and creates
     * the game and its controller.
     */
    Server(Integer portNumberSocket, Integer portNumberRMI) {
        this.portNumberSocket = portNumberSocket;
        this.portNumberRMI = portNumberRMI;
        setupConnections();
    }

    /**
     * Helper method used to setup server connections (Socket and RMI)
     */
    private void setupConnections() {
        ServerSocketConnectionHandler socketConnectionHandler;
        try {
            socketConnectionHandler = new ServerSocketConnectionHandler(portNumberSocket);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        socketConnectionHandler.startSocketServer();
    }
}
