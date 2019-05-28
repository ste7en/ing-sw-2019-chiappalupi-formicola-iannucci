package it.polimi.ingsw.networking.rmi;

import java.rmi.*;



public interface RMIInterface extends Remote {

    void newUser(String username) throws RemoteException;

    void checkUsernameAvailability(String username) throws RemoteException;


}
