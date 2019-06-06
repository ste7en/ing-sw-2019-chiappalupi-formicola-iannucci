package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.utility.AmmoColor;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public class ServerRMI {

    private Registry registry;
    private Integer portNumber;
    private HashMap<UUID, GameLogic> gamesControllers;
    private Map<String, ClientRMI> users;

    public ServerRMI(Integer portNumber) throws RemoteException {
        this.portNumber = portNumber;
    }
/*
    public void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(portNumber);
        registry.rebind("rmiInterface", this);
        UnicastRemoteObject.exportObject(this, portNumber);
        Logger.getGlobal().info("rmi Server running correctly...");
    }


*/

}