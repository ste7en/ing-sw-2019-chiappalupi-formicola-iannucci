package it.polimi.ingsw.networking.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMIConnectionHandler{

    private Registry registry;
    private RMIInterface server;

    public ClientRMIConnectionHandler() {
        try {
            registry = LocateRegistry.getRegistry(null);
            server = (RMIInterface) registry.lookup("rmiInterface");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }


    public void getMessage(){
        try{
            server.newUser("Elena");
        } catch (RemoteException e){
            System.err.println("Client exception: " + e.toString());
        }

    }
}
