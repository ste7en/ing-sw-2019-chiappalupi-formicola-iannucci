package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.view.View;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ClientRMI extends Client{

    private Registry registry;
    private RMIInterface server;

    public ClientRMI(String host, Integer port){
        super(host, port);
    }

    public void stampa(){
        try {
            System.out.println("NO VABBE GUARDA DANNIIIII");
        }
        catch (Exception e){
            System.err.println("ClientSocket exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void setupConnection() {
        try{
            registry = LocateRegistry.getRegistry(connectionPort);
            System.out.println(registry);
            server = (RMIInterface) registry.lookup("rmiInterface");
            try {
                exportClient();
                server.registerClient(this);

            } catch (RemoteException e) {
                System.err.println("ClientSocket exception: " + e.toString());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("ClientSocket exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void exportClient() throws RemoteException{
        UnicastRemoteObject.exportObject(this);
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

