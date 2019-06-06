package it.polimi.ingsw.networking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    public void gameStarted(String gameID) throws RemoteException;

}
