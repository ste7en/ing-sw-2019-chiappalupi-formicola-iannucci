package it.polimi.ingsw.networking.rmi;

import java.rmi.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface ServerInterface extends Remote {

    String remoteReference = "RMIInterface";

    boolean checkUsernameAvailability(String username) throws RemoteException;

    int createUserRMIHelper(String username) throws RemoteException;

    /**
     * When a client decides to join a game.
     * This method also handles the case when a user disconnected from a game and reconnects.
     * @param username username of the user who will play the game
     */
    void joinWaitingRoom(String username) throws RemoteException;

    List<String> getAvailableCharacters(UUID gameID) throws RemoteException;

    /**
     * When the user chooses a character, the server is asked for its availability
     * @param gameID a gameID
     * @param userID a userID
     * @param characterColor character's color
     * @return true if the character is available, false otherwise
     * @throws RemoteException RMI exception
     */
    boolean choseCharacter(UUID gameID, int userID, String characterColor) throws RemoteException;

    void choseGameMap(UUID gameID, String configuration) throws RemoteException;

    List<String> getSpawnPowerups(int userID, UUID gameID) throws RemoteException;

    String choseSpawnPoint(int userID, UUID gameID, String spawnPoint, String otherPowerup) throws RemoteException;

    List<String> getAvailableMoves(int userID, UUID gameID) throws RemoteException;

    /**
     * When the user chooses a movement on the board
     * @param userID userID
     * @param gameID gameID
     * @param movement string description of the movement
     * @throws RemoteException RMI exception
     */
    void move(int userID, UUID gameID, String movement) throws RemoteException;

    List<String> askPicks(int userID, UUID gameID, List<String> powerupToSell) throws RemoteException;

    Map<String, String> didChooseWhatToGrab(String pick, int userID, UUID gameID) throws RemoteException;

    String powerupToDiscard(int userID, UUID gameID, String powerup) throws RemoteException;

    String weaponToDiscard(int userID, UUID gameID, String weapon) throws RemoteException;

    List<String> askWeapons(int userID, UUID gameID) throws RemoteException;

    Map<String, String> useWeapon(int userID, UUID gameID, String weaponSelected) throws RemoteException;

    Map<String, String> useWeaponAfterPowerupSelling(int userID, UUID gameID, String weaponSelected, List<String> powerups) throws RemoteException;

    List<String> makeDamage(int userID, String potentiableBoolean, String effectIndex, UUID gameID, String damage, String weapon) throws RemoteException;

    Map<String, String> useEffect(int userID, UUID gameID, String forPotentiableWeapon, String effectSelected, String weaponSelected) throws RemoteException;

    boolean reload(List<String> weaponsSelected, int userID, UUID gameID) throws RemoteException;

    List<String> getUnloadedWeaponInHand(int userID, UUID gameID) throws RemoteException;

    List<String> getUsablePowerups(int userID, UUID gameID) throws RemoteException;

    List<String> getPowerupDamages(int userID, UUID gameID, String powerup) throws RemoteException;

    void applyPowerupDamage(int userID, UUID gameID, String powerup, String damage) throws RemoteException;

    List<String> getPowerupsInHand(int userID, UUID gameID) throws RemoteException;

    void sellPowerupToReload(List<String> powerups, int userID, UUID gameID) throws RemoteException;
}
