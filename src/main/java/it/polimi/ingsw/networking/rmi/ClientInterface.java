package it.polimi.ingsw.networking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    String remoteReference = "ClientInterface";

    public void gameStarted(String gameID) throws RemoteException;

    public boolean ping() throws RemoteException;

}
