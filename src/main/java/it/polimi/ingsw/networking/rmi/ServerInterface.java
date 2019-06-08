package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.model.utility.AmmoColor;

import java.rmi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface ServerInterface extends Remote {

    boolean createUserRMIHelper(String username) throws RemoteException;

    void joinWaitingRoom(String username) throws RemoteException;

    ArrayList<String> getAvailableCharacters(UUID gameID) throws RemoteException;

    void choseCharacter(int userID, UUID gameID, String character) throws RemoteException;

    void choseGameMap(UUID gameID, String configuration) throws RemoteException;

    ArrayList<String> getPowerups(int userID, UUID gameID) throws RemoteException;

    void chooseSpawnPoint() throws RemoteException;

    void chooseMove() throws RemoteException;

    void chooseMovement() throws RemoteException;

    void chooseWhatToGrab() throws RemoteException;

    ArrayList<String> getAvailableWeapons() throws RemoteException;

    void chooseWeapon(String weaponSelected) throws RemoteException;

    Map<String, String> useWeapon(int userID, UUID gameID, String weaponSelected) throws RemoteException;

    void makeDamage(int userID, String potentiableBoolean, String effectIndex, UUID gameID, String damage, String weapon) throws RemoteException;

    Map<String, String> useEffect(int userID, UUID gameID, String forPotentiableWeapon, String effectSelected, String weaponSelected) throws RemoteException;

    boolean reload(List<String> weaponsSelected, int userID, UUID gameID) throws RemoteException;

    List<String> getWeaponInHand(int userID, UUID gameID) throws RemoteException;

    List<String> getUsablePowerups(int userID, UUID gameID) throws RemoteException;

    List<String> getPowerupDamages(int userID, UUID gameID, String powerup) throws RemoteException;

    void applyPowerupDamage(int userID, UUID gameID, String powerup, String damage) throws RemoteException;
}
