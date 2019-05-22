package it.polimi.ingsw.networking.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMIConnectionHandler{

    private Registry registry;
    private RMIInterface server;

    /*public static void main( String[] s){
        try {
            ServerRMIConnectionHandler server = new ServerRMIConnectionHandler(1080);
            server.launch();
        } catch (RemoteException e){

        }
        ClientRMIConnectionHandler connection = new ClientRMIConnectionHandler();
    }*/

    public ClientRMIConnectionHandler() {
        try {
            registry = LocateRegistry.getRegistry(" ");
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
