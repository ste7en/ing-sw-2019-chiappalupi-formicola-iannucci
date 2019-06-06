package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.view.View;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.UUID;


public class ClientRMI extends Client implements ClientInterface {

    private Registry registry;
    private ServerInterface server;

    public ClientRMI(String host, Integer port){
        super(host, port);
    }

    @Override
    protected void setupConnection() {

        try{
            registry = LocateRegistry.getRegistry(connectionPort);
            System.out.println(registry);
            server = (ServerInterface) registry.lookup("rmiInterface");
            try {
                exportClient();
                server.registerClient();
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
        ClientInterface stub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
        registry.rebind("ClientInterface", stub);
    }

    public void registerObserver(View viewObserver) {
        this.viewObserver = viewObserver;
    }


    @Override
    public void login(String username){
        try{
            server.newUser(username);
        } catch (RemoteException e){
            System.err.println("ClientRMI exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void useWeapon(String weaponSelected) {
        try{
            Map<String, String> weaponUsingProcess = server.useWeapon(userID, gameID, weaponSelected);
            switch(CommunicationMessage.valueOf(weaponUsingProcess.get(CommunicationMessage.communication_message_key))) {
                case DAMAGE_LIST: {
                    weaponUsingProcess.remove(CommunicationMessage.communication_message_key);
                    this.viewObserver.willChooseDamage(weaponUsingProcess);
                    break;
                }
                case MODES_LIST: {
                    weaponUsingProcess.remove(CommunicationMessage.communication_message_key);
                    this.viewObserver.willChooseMode(weaponUsingProcess);
                    break;
                }
                case EFFECTS_LIST: {
                    weaponUsingProcess.remove(CommunicationMessage.communication_message_key);
                    this.viewObserver.willChooseEffects(weaponUsingProcess);
                    break;
                }
            }
        } catch (RemoteException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }
    }

    @Override
    public void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        try{
            server.makeDamage(userID, forPotentiableWeapon, indexOfEffect, gameID, damage, weapon);
        } catch (RemoteException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }
    }

    @Override
    public void useMode(String weapon, String effect) {
        try {
            Map<String, String> damageList = server.useEffect(userID, gameID, null, effect, weapon);
            this.viewObserver.willChooseDamage(damageList);
        } catch (RemoteException e) {
            System.err.println("ClientRMI exception: " + e.toString());
        }
    }

    @Override
    public void gameStarted(String gameID){
        this.viewObserver.onStart(UUID.fromString(gameID));
        this.gameID = UUID.fromString(gameID);
    }
}

