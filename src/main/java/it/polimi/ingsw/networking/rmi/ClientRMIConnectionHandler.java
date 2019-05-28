package it.polimi.ingsw.networking.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class ClientRMIConnectionHandler{

    private Registry registry;
    private RMIInterface server;

    public static void main( String[] s){
        try {
            ServerRMIConnectionHandler server = new ServerRMIConnectionHandler(1080);
            server.launch();
        } catch (RemoteException e){
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        ClientRMIConnectionHandler connection = new ClientRMIConnectionHandler();
        connection.getMessage();
    }

    public ClientRMIConnectionHandler() {
        try{
            registry = LocateRegistry.getRegistry(1080);
            System.out.println(registry);
            server = (RMIInterface) registry.lookup("rmiInterface");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }


    public void getMessage(){
        try{
            server.newUser("Daniele");
        } catch (RemoteException e){
            System.err.println("Client exception: " + e.toString());
        }

    }
}

