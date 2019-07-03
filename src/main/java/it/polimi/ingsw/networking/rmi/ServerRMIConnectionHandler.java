package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.networking.utility.ConnectionState.*;

/**
 * This class is responsible for handling a single client rmi connection,
 * it runs into a single thread with multiple thread implementing message handling.
 *
 * @author Elena Iannucci
 */
public class ServerRMIConnectionHandler extends ServerConnectionHandler implements RMIAsyncHelper {

    private static final String PING_TIMEOUT = "Ping timeout. Closing RMI connection :: ";

    private ClientInterface clientRMI;

    public ServerRMIConnectionHandler(Server server, ClientInterface clientRMI) {
        super.server = server;
        super.connectionState = ONLINE;
        super.executorService = Executors.newCachedThreadPool();
        this.clientRMI = clientRMI;
    }

    public void gameDidStart(String gameID) {
        submitRemoteMethodInvocation(executorService, () -> {
            clientRMI.gameStarted(gameID);
            return null;
        });
    }

    public boolean isConnectionAvailable() {
        return connectionState == ONLINE;
    }

    @Override
    protected void willChooseCharacter(List<String> availableCharacters) {
        submitRemoteMethodInvocation(executorService, () -> {
            clientRMI.willChooseCharacter(availableCharacters);
            return null;
        });
    }

    @Override
    protected void didChooseCharacter(UUID gameID, int userID, String chosenCharacterColor) {

    }

    @Override
    protected void willChooseGameMap(UUID gameID) {
        submitRemoteMethodInvocation(executorService, () -> {
            clientRMI.willChooseGameMap();
            return null;
        });
    }

    @Override
    protected void startNewTurn(int userID) {
        submitRemoteMethodInvocation(executorService, () -> {
            clientRMI.willStartTurn();
            return null;
        });
    }

    @Override
    protected void startNewTurnFromRespawn(int userID) {
        submitRemoteMethodInvocation(executorService, () -> {
            clientRMI.willStartFromRespawn();
            return null;
        });
    }

    @Override
    protected void displayChanges(int userID, String newBoardSituation) {
        submitRemoteMethodInvocation(executorService, () -> {
            clientRMI.willDisplayUpdate(newBoardSituation);
            return null;
        });
    }

    @Override
    public void ping() {
        try {
            if (isConnectionAvailable() && clientRMI.ping()) Ping.getInstance().didPong(getConnectionHashCode());
        } catch (RemoteException e) {
            logOnException(RMI_EXCEPTION + PING_TIMEOUT, e);
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