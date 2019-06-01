package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.networking.ConnectionHandlerSenderDelegate;
import jdk.jfr.Percentage;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public class ServerRMIConnectionHandler implements RMIInterface {

    private Registry registry;
    private Integer portNumber;
    private HashMap<UUID, GameLogic> gamesControllers;
    private Map<String, ClientRMIConnectionHandler> users;

    public ServerRMIConnectionHandler(Integer portNumber) throws RemoteException {
        this.portNumber = portNumber;
    }

    public void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(portNumber);
        registry.rebind("rmiInterface", this);
        UnicastRemoteObject.exportObject(this, 0);
        Logger.getGlobal().info("rmi Server running correctly...");
    }


    @Override
    public void newUser(String username) throws RemoteException {
        if (checkUsernameAvailability(username)==true) System.out.print(username + ", you are logged in");
    }

    @Override
    public void registerClient(String username, ClientRMIConnectionHandler clientRMI){
        users.put(username, clientRMI);
    }

    public boolean checkUsernameAvailability(String username) {
        return true;
    }

    @Override
    public void joinWaitingRoom() throws RemoteException {

    }

    @Override
    public ArrayList<Character> getAvailableCharacters() throws RemoteException{
        return null;
    }

    @Override
    public void chooseCharacter(Character character) throws RemoteException{

    }

    @Override
    public void chooseGameSettings() throws RemoteException{

    }

    @Override
    public ArrayList<AmmoColor> displaySpawnPoints() throws RemoteException{
        return null;
    }

    @Override
    public void chooseSpawnPoint() throws RemoteException{

    }

    @Override
    public void chooseMove() throws RemoteException{

    }

    @Override
    public void chooseMovement() throws RemoteException{

    }

    @Override
    public void chooseWhatToGrab() throws RemoteException{

    }

    @Override
    public ArrayList<String> getAvailableWeapons() throws RemoteException{

    }

    @Override
    public void chooseWeapon(String weaponSelected) throws RemoteException{

    }

    @Override
    public Map<String, String> getAvailableDamages() throws RemoteException{
        return null;
    }

    @Override
    public void chooseDamage(Map<String, String> damageToDo) throws RemoteException{

    }

    Map<String, String> getAvailableModes() throws RemoteException;

    void chooseMode(Map<String, String> modalityChosen) throws RemoteException;

    void getAvailableEffect() throws RemoteException;

    void chooseEffects() throws RemoteException;

    void getAvailablePowerups() throws RemoteException;

    void getAvailablePowerupsEffects() throws RemoteException;

    void choosePowerup() throws RemoteException;

    void choosePowerupEffects() throws RemoteException;

    ArrayList<String> canReload() throws RemoteException;

    void reload(String weaponSelected) throws RemoteException;

}