package it.polimi.ingsw.networking.rmi;

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
import java.util.concurrent.*;

public class ClientRMI extends Client implements ClientInterface {

    private Registry serverRegistry;
    private Registry clientRegistry;
    private ServerInterface server;
    private ExecutorService executorService;

    public ClientRMI(String host, Integer port){
        super(host, port);
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Log strings
     */
    private static final String CLIENT_RMI_EXCEPTION  = "ClientRMI exception: ";
    private static final String INTERRUPTED_EXCEPTION = "Interrupted exception: ";

    /**
     * @param task a callable task executed in parallel
     */
    private void submitRemoteMethodInvocation(Callable<Void> task) {
        try {
            executorService.submit(task).get();
        } catch (InterruptedException e) {
            logOnException(INTERRUPTED_EXCEPTION +e.getCause(), e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logOnException(CLIENT_RMI_EXCEPTION + e.getCause(), e);
        }
    }

    @Override
    protected void setupConnection() {
        try{
            serverRegistry = LocateRegistry.getRegistry(serverName, connectionPort);
            logDescription(serverRegistry);
            server = (ServerInterface) serverRegistry.lookup(ServerInterface.remoteReference);
        } catch (Exception e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    protected void notifyServerTimeoutExpired() {
        submitRemoteMethodInvocation( () -> {
            this.server.timeoutDidExpire(userID);
            return null;
        });
    }

    public void exportClient(String username) throws RemoteException {
       clientRegistry = LocateRegistry.createRegistry(connectionPort);
       ClientInterface stub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
       clientRegistry.rebind(username, stub);
    }

    public int registerClient(String username) {
        try {
            ClientInterface stub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
            return server.registerClient(stub, username);
        } catch (RemoteException e){
            logOnException(CLIENT_RMI_EXCEPTION+e.getCause(), e);
        }
        return -1;
    }


    @Override
    public void createUser(String username){
       // try{

            var userID = registerClient(username);
            /*
            if(server.checkUsernameAvailability(username)==true){
                try {
                    userID = registerClient(username);
                } catch (Exception e) {
                    logOnException(CLIENT_RMI_EXCEPTION, e);
                }
            }
            var userID = server.createUserRMIHelper(username,"localhost");*/
            if(userID == -1) {
                viewObserver.onLoginFailure();
            }
            else{
                this.userID = userID;
                viewObserver.onLoginSuccess(username);
            }
            /*
        } catch (RemoteException e){
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }*/
    }

    @Override
    public void joinWaitingRoom(String username) {
        submitRemoteMethodInvocation(() -> {
            server.joinWaitingRoom(username);
            return null;
        });
    }

    @Override
    public void newAction() {
        String situation = "";
        try {
            situation = server.startActions(userID, gameID);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        this.viewObserver.onChooseAction(situation);
    }

    @Override
    public void choseCharacter(String characterColor){
        try {
            var availableCharacters = server.getAvailableCharacters(gameID);
            if (!server.choseCharacter(gameID, userID, characterColor))
                executorService.submit(() -> this.viewObserver.willChooseCharacter(availableCharacters));
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void willChooseGameMap() {
        submitRemoteMethodInvocation( () -> {
            this.viewObserver.willChooseGameMap();
            return null;
        });
    }

    @Override
    public void willStartTurn() {
        submitRemoteMethodInvocation( () -> {
            this.viewObserver.newAction();
            return null;
        });
    }

    @Override
    public void willStartFromRespawn() {
        submitRemoteMethodInvocation( () -> {
            this.viewObserver.willChooseSpawnPoint();
            return null;
        });
    }

    @Override
    public void willDisplayUpdate(String change) {
        this.viewObserver.displayChange(change);
    }

    @Override
    public void choseGameMap(String configuration) {
        submitRemoteMethodInvocation(() -> {
            server.didChooseGameMap(gameID, configuration);
            return null;
        });

        viewObserver.willChooseSpawnPoint();
    }

    @Override
    public void askForPossibleSpawnPoints() {
        submitRemoteMethodInvocation( () -> {
            List<String> powerups;
            powerups = server.getSpawnPowerups(userID, gameID);
            viewObserver.onChooseSpawnPoint(powerups);
            return null;
        });
    }

    @Override
    public void askPicks(List<String> powerupToSell) {
        List<String> possiblePicks = new ArrayList<>();
        try {
            possiblePicks = this.server.askPicks(userID, gameID, powerupToSell);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        if(possiblePicks.isEmpty()) this.viewObserver.onGrabFailure();
        viewObserver.willChooseWhatToGrab(possiblePicks);
    }

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
        } else viewObserver.onGrabSuccess();
    }

    @Override
    public void powerupGrabToDiscard(String powerup) {
        try {
            server.powerupToDiscard(userID, gameID, powerup);
        } catch (RemoteException e) {
            AdrenalineLogger.error(e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        this.viewObserver.onGrabSuccess();
    }

    @Override
    public void weaponGrabToDiscard(String weapon) {
        try {
            server.weaponToDiscard(userID, gameID, weapon);
        } catch (RemoteException e) {
            AdrenalineLogger.error(e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        this.viewObserver.onGrabSuccess();
    }

    @Override
    public void powerupSellingToGrabWeapon() {
        List<String> powerups = new ArrayList<>();
        try {
            powerups = server.getPowerupsInHand(userID, gameID);
        } catch (RemoteException e) {
            AdrenalineLogger.error(e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        this.viewObserver.sellPowerupToGrabWeapon(powerups);
    }

    @Override
    public void choseSpawnPoint(String spawnPoint, String otherPowerup){
        try {
            server.choseSpawnPoint(userID, gameID, spawnPoint, otherPowerup);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        viewObserver.newAction();
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
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        this.viewObserver.afterAction();
    }

    @Override
    public void askWeapons() {
        try {
            List<String> weapons = this.server.askWeapons(userID, gameID);
            if(weapons.isEmpty()) this.viewObserver.onShootPeopleFailure();
            else this.viewObserver.willChooseWeapon(this.server.askWeapons(userID, gameID));
        } catch(RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        try{
            boolean lastDamage = true;
            if(forPotentiableWeapon != null) lastDamage = Boolean.parseBoolean(forPotentiableWeapon);
            List<String> powerups = this.server.makeDamage(userID, forPotentiableWeapon, indexOfEffect, gameID, damage, weapon);
            if(lastDamage) {
                this.viewObserver.didUseWeapon(powerups);
            }
        } catch (RemoteException e){
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void sellPowerupToReload(List<String> powerups) {
        try {
            this.server.sellPowerupToReload(powerups, userID, gameID);
            this.askWeaponToReload();
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(e.getMessage());
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
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void usePowerup(String powerup, String damage) {
        try {
            server.applyPowerupDamage(userID, gameID, powerup, damage);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
    }

    @Override
    public void afterAction() {
        String curSituation = null;
        try {
            curSituation = server.afterAction(userID, gameID);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
        }
        if(curSituation != null) this.viewObserver.onEndTurn(curSituation);
        else this.viewObserver.newAction();
    }

    @Override
    public void turnEnded() {
        try {
            this.server.turnEnded(userID, gameID);
        } catch (RemoteException e) {
            AdrenalineLogger.error(CLIENT_RMI_EXCEPTION + e.toString());
            AdrenalineLogger.error(e.getMessage());
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
    public void willChooseCharacter(List<String> availableCharacters){
        viewObserver.willChooseCharacter(availableCharacters);
    }


}

