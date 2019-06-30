package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.networking.utility.Pingable;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerRMIConnectionHandler extends ServerConnectionHandler {

    private static final String REMOTE_EXC   = "Remote exception :: ";
    private static final String PING_TIMEOUT = "Ping timeout. Closing RMI connection :: ";

    private ClientInterface clientRMI;

    public ServerRMIConnectionHandler(Server server, ClientInterface clientRMI) {
        super.server = server;
        this.clientRMI = clientRMI;
    }

    public void gameDidStart(String gameID) {
        try {
            clientRMI.gameStarted(gameID);
        } catch (RemoteException e){
            logOnException(REMOTE_EXC, e);
        }
    }

    public boolean isConnectionAvailable() {
        try {
            return clientRMI.ping();
        } catch (RemoteException e) {
            logOnException(REMOTE_EXC+"ping failed.", e);
        }
        return false;
    }

    @Override
    protected void willChooseCharacter(List<String> availableCharacters) {
        try {
            clientRMI.willChooseCharacter((ArrayList)availableCharacters);
        } catch (Exception e){
            System.err.println("ClientRMI exception: " + e.toString());
        }
    }

    @Override
    protected void didChooseCharacter(UUID gameID, int userID, String chosenCharacterColor) {

    }

    @Override
    public void ping() {
        if (this.isConnectionAvailable()) Ping.getInstance().didPong(getConnectionHashCode());
    }

    @Override
    public void closeConnection() {
        //TODO: - Mark this connection handler as not connected
        server.didDisconnect(this);
        AdrenalineLogger.info(PING_TIMEOUT+clientRMI.toString());
    }
}