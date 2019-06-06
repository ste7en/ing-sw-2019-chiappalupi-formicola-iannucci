package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.model.utility.AmmoColor;

import java.rmi.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;


public interface RMIInterface extends Remote {

    void registerClient() throws RemoteException;

    void newUser(String username) throws RemoteException;

    void joinWaitingRoom() throws RemoteException;

    ArrayList<Character> getAvailableCharacters() throws RemoteException;

    void chooseCharacter(Character character) throws RemoteException;

    void chooseGameSettings() throws RemoteException;

    ArrayList<AmmoColor> displaySpawnPoints() throws RemoteException;

    void chooseSpawnPoint() throws RemoteException;

    void chooseMove() throws RemoteException;

    void chooseMovement() throws RemoteException;

    void chooseWhatToGrab() throws RemoteException;

    ArrayList<String> getAvailableWeapons() throws RemoteException;

    void chooseWeapon(String weaponSelected) throws RemoteException;

    Map<String, String> useWeapon(int userID, UUID gameID, String weaponSelected) throws RemoteException;

    void makeDamage(int userID, String potentiableBoolean, String effectIndex, UUID gameID, String damage, String weapon) throws RemoteException;

    Map<String, String> useEffect(int userID, UUID gameID, String forPotentiableWeapon, String effectSelected, String weaponSelected) throws RemoteException;

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
