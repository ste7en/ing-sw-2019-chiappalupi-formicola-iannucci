package it.polimi.ingsw.networking.rmi;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;


public class ServerRMIConnectionHandler implements RMIInterface {

    private Registry registry;
    private Integer portNumber;

    public ServerRMIConnectionHandler(Integer portNumber) throws RemoteException {
        this.portNumber = portNumber;
    }

    public void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(portNumber);
        registry.rebind("rmiInterface", this);
        UnicastRemoteObject.exportObject(this, 0);
        Logger.getGlobal().info("rmi Server running correctly...");
    }

    @Override
    public void newUser(String username) throws RemoteException {
        System.out.print(username + ", you are logged");
    }

    @Override
    public void checkUsernameAvailability(String username) {

    }

}