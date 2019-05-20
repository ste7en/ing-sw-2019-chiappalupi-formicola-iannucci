package it.polimi.ingsw.networking;

//import it.polimi.ingsw.networking.*;
//import it.polimi.ingsw.networking.rmi.ServerRMIConnectionHandler;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.socket.*;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.utility.Loggable;
import it.polimi.ingsw.networking.utility.Ping;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
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
    private List<ConnectionHandlerSenderDelegate> senderDelegate;

    private String EXC_SETUP = "Error while setting up a ServerSocketConnectionHandler :: ";

    /**
     * Entry point of the server application
     * @param args arguments
     */
    public static void main(String[] args) {
        AdrenalineLogger.setLogName("Server");
        new Server(3334, 4444);

    }

    /**
     * Server constructor responsible for setting up networking parameters and creates
     * the game and its controller.
     */
    Server(Integer portNumberSocket, Integer portNumberRMI) {
        this.portNumberSocket = portNumberSocket;
        this.portNumberRMI = portNumberRMI;
        this.senderDelegate = new LinkedList<>();
        setupConnections();
    }

    /**
     * Helper method used to setup server connections (Socket and RMI)
     */
    private void setupConnections() {
        ServerSocketConnectionHandler socketConnectionHandler;
        //ServerRMIConnectionHandler RMIConnectionHandler;
        try {
            socketConnectionHandler = new ServerSocketConnectionHandler(portNumberSocket, this);
            senderDelegate.add(socketConnectionHandler);
            Executors.newCachedThreadPool().submit(socketConnectionHandler);
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }
        try {
            //RMIConnectionHandler = new ServerRMIConnectionHandler(portNumberRMI);
            //RMIConnectionHandler.launch();
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }

    }

    /**
     * Receives a message from a delegator
     * @param message received message
     */
    @Override
    public void receive(String message) {
        var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
        var connectionID = CommunicationMessage.getConnectionIDFrom(message);
        var args = CommunicationMessage.getMessageArgsFrom(message);

        switch (communicationMessage) {
            case PONG:
                Ping.getInstance().didPong(id);
                break;
            default:
                break;
        }
        logDescription(communicationMessage);
        System.out.println(message);
    }
}
