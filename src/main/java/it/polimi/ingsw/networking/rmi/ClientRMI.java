package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.view.View;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;


public class ClientRMI extends Client implements RMIClientInterface{

    private Registry registry;
    private RMIInterface server;
    private Integer clientPort;

    public ClientRMI(String host, Integer port, Integer clientPort){
        super(host, port);
        this.clientPort = clientPort;
    }

    @Override
    protected void setupConnection() {
        try{
            registry = LocateRegistry.getRegistry(connectionPort);
            System.out.println(registry);
            server = (RMIInterface) registry.lookup("rmiInterface");
            launch();

        } catch (Exception e) {
            System.err.println("ClientRMI exception: " + e.toString());
            e.printStackTrace();
        }
    }


    public void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(clientPort);
        registry.rebind("rmiClientInterface", this);
        UnicastRemoteObject.exportObject(this, clientPort);
        Logger.getGlobal().info("rmi Client running correctly...");
    }

    public void registerOnServer(){
        try {
            server.registerClient(clientPort);
        }catch (RemoteException e){
            System.err.println("register exception: " + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void login(String username){
        try{
            server.newUser(username);
        } catch (RemoteException e){
            System.err.println("Client exception: " + e.toString());
        }
    }

    public void registerObserver(View viewObserver) {
        this.viewObserver = viewObserver;
    }
}

