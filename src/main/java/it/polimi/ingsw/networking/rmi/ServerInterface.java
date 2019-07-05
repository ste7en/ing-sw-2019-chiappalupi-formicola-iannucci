package it.polimi.ingsw.networking.rmi;

import java.rmi.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface ServerInterface extends Remote {

    String remoteReference = "RMIInterface";

    int registerClient(ClientInterface clientInterface, String username) throws RemoteException;

    void timeoutDidExpire(int userID) throws RemoteException;

    void joinWaitingRoom(String username) throws RemoteException;

    List<String> getAvailableCharacters(UUID gameID) throws RemoteException;

    boolean choseCharacter(UUID gameID, int userID, String characterColor) throws RemoteException;

    String getCharacterName(UUID gameID, int userID) throws RemoteException;

    void didChooseSkulls(String skulls, UUID gameID) throws RemoteException;

    String startActions(int userID, UUID gameID) throws RemoteException;

    void didChooseGameMap(UUID gameID, String configuration) throws RemoteException;

    List<String> getSpawnPowerups(int userID, UUID gameID) throws RemoteException;

    void choseSpawnPoint(int userID, UUID gameID, String spawnPoint, String otherPowerup) throws RemoteException;

    List<String> getAvailableMoves(int userID, UUID gameID) throws RemoteException;

    void move(int userID, UUID gameID, String movement) throws RemoteException;

    List<String> askPicks(int userID, UUID gameID, List<String> powerupToSell) throws RemoteException;

    Map<String, String> didChooseWhatToGrab(String pick, int userID, UUID gameID) throws RemoteException;

    void powerupToDiscard(int userID, UUID gameID, String powerup) throws RemoteException;

    void weaponToDiscard(int userID, UUID gameID, String weapon) throws RemoteException;

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

    String afterAction(int userID, UUID gameID) throws RemoteException;

    void checkDeathsBeforeEndTurn(UUID gameID) throws RemoteException;

    List<String> getSpawnAfterDeathPowerup(int userID, UUID gameID) throws RemoteException;

    void spawnAfterDeath(int userID, UUID gameID, String powerup) throws RemoteException;

    void turnEnded(int userID, UUID gameID) throws RemoteException;

    boolean canContinueAfterDeathsRespawn(int userID, UUID gameID) throws RemoteException;

    List<String> movementsBeforeShot(int userID, UUID gameID) throws RemoteException;

    void movesBefore(String movement, int userID, UUID gameID) throws RemoteException;
}
