package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;

import java.rmi.RemoteException;

public class ServerRMIConnectionHandler extends ServerConnectionHandler {

    private RMIClientInterface clientRMI;

    private Server server;

    public void registerClient(RMIClientInterface clientRMI){
        this.clientRMI = clientRMI;
    }

    public void gameDidStart(String gameID){
        try{
            clientRMI.gameStarted(gameID);
        }catch (RemoteException e){
            System.err.println("ClientSocket exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public boolean isConnectionAvailable(){
        return true;
    }
}