package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerRMIConnectionHandler {

    private ArrayList<RMIClientInterface> clientsRMI;

    public void registerClient(RMIClientInterface clientRMI){
        int i=0;
        while(clientsRMI.get(i)!=null) {
            i++;
        }
        this.clientsRMI.add(i, clientRMI);
    }


    public void chooseCharacter(Character character) throws RemoteException{

    }

    public void gameDidStart(){
        try{
            for(RMIClientInterface clientRMI : clientsRMI)
                clientRMI.gameStarted();
        }catch (RemoteException e){
            System.err.println("ClientSocket exception: " + e.toString());
            e.printStackTrace();
        }
    }

}