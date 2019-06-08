package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;

import java.rmi.RemoteException;

public class ServerRMIConnectionHandler extends ServerConnectionHandler {

    private ClientInterface clientRMI;

    private Server server;

    public ServerRMIConnectionHandler(Server server, ClientInterface clientRMI){
        this.server = server;
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