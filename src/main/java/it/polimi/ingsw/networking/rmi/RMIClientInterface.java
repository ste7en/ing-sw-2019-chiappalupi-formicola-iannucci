package it.polimi.ingsw.networking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIClientInterface extends Remote {
    public void gameStarted() throws RemoteException;
}
