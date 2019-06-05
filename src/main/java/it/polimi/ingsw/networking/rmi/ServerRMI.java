package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.utility.AmmoColor;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public class ServerRMI implements RMIInterface {

    private Registry registry;
    private Integer portNumber;
    private ClientRMI clientRMI;
    private HashMap<UUID, GameLogic> gamesControllers;
    private Map<String, ClientRMI> users;

    public ServerRMI(Integer portNumber) throws RemoteException {
        this.portNumber = portNumber;
    }

    public void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(portNumber);
        registry.rebind("rmiInterface", this);
        UnicastRemoteObject.exportObject(this);
        Logger.getGlobal().info("rmi Server running correctly...");
    }


    @Override
    public void newUser(String username) throws RemoteException {
        if (checkUsernameAvailability(username)==true) System.out.print(username + ", you are logged in");
    }

    @Override
    public void registerClient(ClientRMI clientRMI){
        this.clientRMI = clientRMI;
        clientRMI.stampa();
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
        return null;
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

    @Override
    public Map<String, String> getAvailableModes() throws RemoteException{
        return null;
    }

    @Override
    public void chooseMode(Map<String, String> modalityChosen) throws RemoteException{

    }

    @Override
    public void getAvailableEffect() throws RemoteException{

    }

    @Override
    public void chooseEffects() throws RemoteException{

    }

    @Override
    public void getAvailablePowerups() throws RemoteException{

    }

    @Override
    public void getAvailablePowerupsEffects() throws RemoteException{

    }

    @Override
    public void choosePowerup() throws RemoteException{

    }

    @Override
    public void choosePowerupEffects() throws RemoteException{

    }

    @Override
    public ArrayList<String> canReload() throws RemoteException{
        return null;
    }

    @Override
    public void reload(String weaponSelected) throws RemoteException{

    }

}