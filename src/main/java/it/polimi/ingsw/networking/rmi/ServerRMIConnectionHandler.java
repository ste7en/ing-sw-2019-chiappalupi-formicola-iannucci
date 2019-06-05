package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMIConnectionHandler extends ServerConnectionHandler {

    private Registry registry;
    private RMIClientInterface client;
    private Integer clientPort;

    public ServerRMIConnectionHandler(Server server, Integer clientPort){
        this.server=server;
        this.clientPort=clientPort;
        setupConnection();
    }

    protected void setupConnection() {
        try{
            registry = LocateRegistry.getRegistry(clientPort);
            System.out.println(registry);
            client = (RMIClientInterface) registry.lookup("rmiClientInterface");
        } catch (Exception e) {
            System.err.println("ServerRMI exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnectionAvailable(){
        return true;
    }


    protected void gameDidStart(String gameID){

    }
}
