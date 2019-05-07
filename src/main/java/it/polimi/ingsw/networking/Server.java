package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.*;
import it.polimi.ingsw.networking.rmi.ServerRMIConnectionHandler;
import it.polimi.ingsw.networking.socket.*;
import it.polimi.ingsw.utility.Loggable;

import java.util.LinkedList;
import java.util.List;
//import it.polimi.ingsw.networking.rmi.*;

/**
 * Main class of the game server. It will set up the networking and create controllers and games.
 * It is designed to support multiple games and players in a waiting room before starting a game,
 * plus it manages loss of connections and users re-connections.
 *
 * @author Stefano Formicola feat. Elena Iannucci
 */
public class Server implements Loggable, ConnectionHandlerReceiverDelegate {

    private Integer portNumberSocket;
    private Integer portNumberRMI;
    private List<ConnectionHandlerSenderDelegate> senderDelegate = new LinkedList<>();

    private String EXC_SETUP = "Error while setting up a ServerSocketConnectionHandler :: ";

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
     * Receives a message from a delegator
     */
    @Override
    public void receive() {

    }

    /**
     * Helper method used to setup server connections (Socket and RMI)
     */
    private void setupConnections() {
        ServerSocketConnectionHandler socketConnectionHandler;
        ServerRMIConnectionHandler RMIConnectionHandler;
        try {
            socketConnectionHandler = new ServerSocketConnectionHandler(portNumberSocket);
            senderDelegate.add(socketConnectionHandler);
            socketConnectionHandler.startSocketServer();
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }
        try {
            RMIConnectionHandler = new ServerRMIConnectionHandler(portNumberRMI);
            RMIConnectionHandler.launch();
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }


    }
}