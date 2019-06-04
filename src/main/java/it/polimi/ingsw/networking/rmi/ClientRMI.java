package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.view.View;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class ClientRMI extends Client {

    private Registry registry;
    private RMIInterface server;

    public ClientRMI(Integer port, String host){
        super(host, port);
        try {
            this.connectionPort = port;
            ServerRMIConnectionHandler server = new ServerRMIConnectionHandler(port);
            server.launch();
        } catch (RemoteException e){
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        this.setupConnection();
    }

    @Override
    protected void setupConnection() {
        try{
            registry = LocateRegistry.getRegistry(connectionPort);
            System.out.println(registry);
            server = (RMIInterface) registry.lookup("rmiInterface");
        } catch (Exception e) {
            System.err.println("ClientSocket exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void login(String username){
        try{
            server.newUser(username);
        } catch (RemoteException e){
            System.err.println("ClientSocket exception: " + e.toString());
        }
    }

    public void registerObserver(View viewObserver) {
        this.viewObserver = viewObserver;
    }
}

