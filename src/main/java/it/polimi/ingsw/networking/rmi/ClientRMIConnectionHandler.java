package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.view.View;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class ClientRMIConnectionHandler {

    private Registry registry;
    private RMIInterface server;
    int port;
    private View viewObserver;

    public ClientRMIConnectionHandler(int port){
        try {
            this.port = port;
            ServerRMIConnectionHandler server = new ServerRMIConnectionHandler(port);
            server.launch();
        } catch (RemoteException e){
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        this.setUpConnection();
    }

    public void setUpConnection() {
        try{
            registry = LocateRegistry.getRegistry(port);
            System.out.println(registry);
            server = (RMIInterface) registry.lookup("rmiInterface");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }


    public void login(String username){
        try{
            server.newUser(username);
            server.registerClient(username, this);
        } catch (RemoteException e){
            System.err.println("Client exception: " + e.toString());
        }
    }

    public void registerObserver(View viewObserver) {
        this.viewObserver = viewObserver;
    }
}

