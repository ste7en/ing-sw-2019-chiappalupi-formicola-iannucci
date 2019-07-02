package it.polimi.ingsw.networking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {

    String remoteReference = "ClientInterface";

    void gameStarted(String gameID) throws RemoteException;

    boolean ping() throws RemoteException;

    void willChooseCharacter(List<String> availableCharacters) throws RemoteException;

    /**
     * When the user has to choose a game map configuration
     * @throws RemoteException RMI Exception
     */
    void willChooseGameMap() throws RemoteException;

    /**
     * When a new turn begins
     * @throws RemoteException RMI Exception
     */
    void willStartTurn() throws RemoteException;

    /**
     * When a new game begins and the player hasn't still spawned
     * @throws RemoteException RMI Exception
     */
    void willStartFromRespawn() throws RemoteException;

    /**
     * When a player that is not this client has made a move and it should be displayed to the other players
     * @param change it's the change that should be displayed
     * @throws RemoteException RMI Exception
     */
    void willDisplayUpdate(String change) throws  RemoteException;

}
