package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import static it.polimi.ingsw.networking.utility.ConnectionState.CLOSED;
import static it.polimi.ingsw.networking.utility.ConnectionState.ONLINE;

public class ServerRMIConnectionHandler extends ServerConnectionHandler {

    private static final String REMOTE_EXC   = "Remote exception :: ";
    private static final String PING_TIMEOUT = "Ping timeout. Closing RMI connection :: ";

    private ClientInterface clientRMI;

    public ServerRMIConnectionHandler(Server server, ClientInterface clientRMI) {
        super.server = server;
        super.connectionState = ONLINE;
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
        return connectionState == ONLINE;
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
        try {
            if (isConnectionAvailable() && clientRMI.ping()) Ping.getInstance().didPong(getConnectionHashCode());
        } catch (RemoteException e) {
            logOnException(REMOTE_EXC+PING_TIMEOUT, e);
            closeConnection();
        }
    }

    @Override
    public void closeConnection() {
        server.didDisconnect(this);
        super.connectionState = CLOSED;
        AdrenalineLogger.info(PING_TIMEOUT+clientRMI.toString());
    }
}