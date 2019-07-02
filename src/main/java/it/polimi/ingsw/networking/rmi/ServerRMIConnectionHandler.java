package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.rmi.RemoteException;
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
            logOnException(REMOTE_EXC+PING_TIMEOUT, e);
        }
        return false;
    }

    @Override
    protected void willChooseCharacter(List<String> availableCharacters) {
        try {
            clientRMI.willChooseCharacter(availableCharacters);
        } catch (Exception e){
            logOnException(REMOTE_EXC, e);
        }
    }

    @Override
    protected void didChooseCharacter(UUID gameID, int userID, String chosenCharacterColor) {

    }

    @Override
    protected void willChooseGameMap(UUID gameID) {
        try {
            clientRMI.willChooseGameMap();
        } catch (Exception e) {
            logOnException(REMOTE_EXC, e);
        }
    }

    @Override
    protected void startNewTurn(int userID) {
        try {
            clientRMI.willStartTurn();
        } catch (RemoteException e) {
            logOnException(REMOTE_EXC, e);
        }
    }

    @Override
    protected void startNewTurnFromRespawn(int userID) {
        try {
            clientRMI.willStartFromRespawn();
        } catch (RemoteException e) {
            logOnException(REMOTE_EXC, e);
        }
    }

    @Override
    protected void displayChanges(int userID, String newBoardSituation) {
        try {
            clientRMI.willDisplayUpdate(newBoardSituation);
        } catch (RemoteException e) {
            logOnException(REMOTE_EXC, e);
        }
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