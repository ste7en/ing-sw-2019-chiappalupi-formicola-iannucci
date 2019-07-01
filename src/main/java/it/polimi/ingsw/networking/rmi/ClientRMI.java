package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ClientRMI extends Client implements ClientInterface {

    private Registry registry;
    private ServerInterface server;

    public ClientRMI(String host, Integer port){
        super(host, port);
    }

    /**
     * Log strings
     */
    private static String CLIENT_RMI_EXCEPTION = "ClientRMI exception: ";

    @Override
    protected void setupConnection() {

        try{
            registry = LocateRegistry.getRegistry(serverName, connectionPort);
            System.out.println(registry);
            server = (ServerInterface) registry.lookup(ServerInterface.remoteReference);
        } catch (Exception e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    public void exportClient(String username) throws RemoteException{
        ClientInterface stub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
        registry.rebind(username, stub);
    }

    @Override
    public void createUser(String username){
        try{
            if(server.checkUsernameAvailability(username)==true){
                try {
                    exportClient(username);
                } catch (RemoteException e) {
                    logOnException(CLIENT_RMI_EXCEPTION, e);
                }
            }
            var userID = server.createUserRMIHelper(username);
            if(userID == -1) {
                viewObserver.onLoginFailure();
            }
            else{
                this.userID = userID;
                viewObserver.onLoginSuccess(username);
            }
        } catch (RemoteException e){
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void joinWaitingRoom(String username) {
        try {
            server.joinWaitingRoom(username);
        } catch (RemoteException e){
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void choseCharacter(String characterColor){
        try {
            server.choseCharacter(gameID, userID, characterColor);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        viewObserver.willChooseGameMap();
    }

    @Override
    public void willChooseGameMap() {
        this.viewObserver.willChooseGameMap();
    }

    @Override
    public void choseGameMap(String configuration){
        try {
            server.didChooseGameMap(gameID, configuration);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        viewObserver.willChooseSpawnPoint();
    }

    @Override
    public void askForPossibleSpawnPoints(){
        List<String> powerups = new ArrayList<>();
        try {
            powerups = server.getSpawnPowerups(userID, gameID);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        viewObserver.onChooseSpawnPoint(powerups);
    }

    @Override
    public void askPicks() {
        List<String> possiblePicks = new ArrayList<>();
        try {
            possiblePicks = this.server.askPicks(userID, gameID);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        viewObserver.willChooseWhatToGrab(possiblePicks);;
    }

    //toDO: check for powerup using to pay cost
    @Override
    public void didChooseWhatToGrab(String pick) {
        Map<String, String> ending = new HashMap<>();
        try {
            ending = server.didChooseWhatToGrab(pick, userID, gameID);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        if(ending.containsKey(Powerup.powerup_key)) {
            this.viewObserver.onGrabFailurePowerup(new ArrayList<>(ending.values()));
        } else if(ending.containsKey(Weapon.weapon_key)) {
            this.viewObserver.onGrabFailureWeapon(new ArrayList<>(ending.values()));
        } else viewObserver.onGrabSuccess(ending.get(GameMap.gameMap_key));
    }

    @Override
    public void powerupGrabToDiscard(String powerup) {
        String currentSituation = "";
        try {
            currentSituation = server.powerupToDiscard(userID, gameID, powerup);
        } catch (RemoteException e) {
            AdrenalineLogger.error(e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        this.viewObserver.onGrabSuccess(currentSituation);
    }

    @Override
    public void weaponGrabToDiscard(String weapon) {
        String currentSituation = "";
        try {
            currentSituation = server.weaponToDiscard(userID, gameID, weapon);
        } catch (RemoteException e) {
            AdrenalineLogger.error(e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        this.viewObserver.onGrabSuccess(currentSituation);
    }

    @Override
    public void choseSpawnPoint(String spawnPoint, String otherPowerup){
        String map = "";
        try {
            map = server.choseSpawnPoint(userID, gameID, spawnPoint, otherPowerup);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        viewObserver.onChooseAction(map);
    }

    @Override
    public void getAvailableMoves() {
        try {
            viewObserver.willChooseMovement(server.getAvailableMoves(userID, gameID));
        } catch (RemoteException e ) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void move(String movement) {
        try {
            server.move(userID, gameID, movement);
        } catch (RemoteException e) {
            logOnException(CLIENT_RMI_EXCEPTION, e);
        }
    }

    @Override
    public void askWeapons() {
        try {
            List<String> weapons = this.server.askWeapons(userID, gameID);
            if(weapons.isEmpty()) this.viewObserver.onShootPeopleFailure();
            else this.viewObserver.willChooseWeapon(this.server.askWeapons(userID, gameID));
        } catch(RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void useWeapon(String weaponSelected) {
        try{
            Map<String, String> weaponUsingProcess = this.server.useWeapon(userID, gameID, weaponSelected);
            CommunicationMessage weaponProcess = CommunicationMessage.valueOf(weaponUsingProcess.get(CommunicationMessage.communication_message_key));
            weaponUsingProcess.remove(CommunicationMessage.communication_message_key);
            switch (weaponProcess) {
                case DAMAGE_FAILURE:
                    this.viewObserver.onDamageFailure();
                    break;
                case DAMAGE_LIST:
                    this.viewObserver.willChooseDamage(weaponUsingProcess);
                    break;
                default:
                    this.viewObserver.willChoosePowerupSelling(weaponUsingProcess);
                    break;
            }
        } catch (RemoteException e){
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void useWeaponAfterPowerupAsking(String weaponSelected, List<String> powerups) {
        try {
            Map<String, String> weaponUsingContinued = this.server.useWeaponAfterPowerupSelling(userID, gameID, weaponSelected, powerups);
            CommunicationMessage weaponProcess = CommunicationMessage.valueOf(weaponUsingContinued.get(CommunicationMessage.communication_message_key));
            weaponUsingContinued.remove(CommunicationMessage.communication_message_key);
            if(weaponProcess == CommunicationMessage.MODES_LIST) {
                this.viewObserver.willChooseMode(weaponUsingContinued);
            } else this.viewObserver.willChooseEffects(weaponUsingContinued);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        try{
            boolean lastDamage = true;
            if(forPotentiableWeapon != null) lastDamage = Boolean.parseBoolean(forPotentiableWeapon);
            List<String> powerups = this.server.makeDamage(userID, forPotentiableWeapon, indexOfEffect, gameID, damage, weapon);
            if(lastDamage) {
                this.viewObserver.didUseWeapon();
                this.viewObserver.askPowerupAfterShot(powerups);
            }
        } catch (RemoteException e){
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void useMode(String weapon, String effect) {
        try {
            Map<String, String> damageList = this.server.useEffect(userID, gameID, null, effect, weapon);
            if(damageList.containsValue(Damage.no_damage)) this.viewObserver.onDamageFailure();
            else this.viewObserver.willChooseDamage(damageList);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void useEffect(String weapon, List<String> effectsToUse) {
        try {
            while (!effectsToUse.isEmpty()) {
                boolean forPotentiableWeapon;
                forPotentiableWeapon = effectsToUse.size() == 1;
                String potentiableBoolean = Boolean.toString(forPotentiableWeapon);
                String effect = effectsToUse.get(0);
                Map<String, String> damageMap = this.server.useEffect(userID, gameID, potentiableBoolean, effect, weapon);
                if(damageMap.containsValue(Damage.no_damage)) this.viewObserver.onDamageFailure();
                else this.viewObserver.willChooseDamage(damageMap);
                effectsToUse.remove(0);
            }
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void askWeaponToReload() {
        try {
            List<String> weaponInHand = this.server.getUnloadedWeaponInHand(userID, gameID);
            if(weaponInHand.isEmpty()) this.viewObserver.onWeaponUnloadedFailure();
            else this.viewObserver.willReload(this.server.getUnloadedWeaponInHand(userID, gameID));
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void reloadWeapons(List<String> weaponsToReload) {
        try {
            boolean didReload =  this.server.reload(weaponsToReload, userID, gameID);
            if(didReload) this.viewObserver.onReloadSuccess();
            else this.viewObserver.onReloadFailure();
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void askForPowerupsToReload() {
        try {
            List<String> powerups = server.getPowerupsInHand(userID, gameID);
            if(powerups.isEmpty()) this.viewObserver.onPowerupInHandFailure();
            else this.viewObserver.willSellPowerupToReload(powerups);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void sellPowerupToReload(List<String> powerups) {
        try {
            this.server.sellPowerupToReload(powerups, userID, gameID);
            this.askWeaponToReload();
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void askForUsablePowerups() {
        try {
            List<String> powerups = this.server.getUsablePowerups(userID, gameID);
            if(powerups.isEmpty()) this.viewObserver.onTurnPowerupFailure();
            else this.viewObserver.willChoosePowerup(powerups);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void askPowerupDamages(String powerup) {
        try {
            List<String> possibleDamages = this.server.getPowerupDamages(userID, gameID, powerup);
            Map<String, String> damagesMap = new HashMap<>();
            for(String damage : possibleDamages) damagesMap.put(Integer.toString(possibleDamages.indexOf(damage)), damage);
            damagesMap.put(Powerup.powerup_key, powerup);
            this.viewObserver.willChoosePowerupDamage(damagesMap);
        } catch (RemoteException e) {
            System.err.print(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void usePowerup(String powerup, String damage) {
        try {
            server.applyPowerupDamage(userID, gameID, powerup, damage);
        } catch (RemoteException e) {
            System.err.print(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void gameStarted(String gameID){
        this.gameID = UUID.fromString(gameID);
        viewObserver.onStart();
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public void willChooseCharacter(ArrayList<String> availableCharacters){
        viewObserver.willChooseCharacter(availableCharacters);
    }
}

