package it.polimi.ingsw.networking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientInterface extends Remote {

    String remoteReference = "ClientInterface";

    void gameStarted(String gameID) throws RemoteException;

    boolean ping() throws RemoteException;

    void willChooseCharacter(ArrayList<String> availableCharacters) throws RemoteException;

}
