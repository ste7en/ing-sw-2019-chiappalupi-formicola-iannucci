package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Server;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMIConnectionHandler{
    private Registry registry;

    private ClientRMIConnectionHandler() {}
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(null);
            Server server = (Server) registry.lookup("RemoteObject");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}