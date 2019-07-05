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

public class ClientRMI extends Client implements ClientInterface, RMIAsyncHelper {

    private Registry serverRegistry;
    private ClientInterface clientStub;
    private ServerInterface server;
    private ExecutorService executorService;
    private int clientOperationTimeoutInSeconds;

    public ClientRMI(String host, Integer port){
        super(host, port);
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    protected void setupConnection() {
        try{
            serverRegistry = LocateRegistry.getRegistry(serverName, connectionPort);
            logDescription(serverRegistry);
            server = (ServerInterface) serverRegistry.lookup(ServerInterface.remoteReference);
            exportClient();
        } catch (Exception e) {
            AdrenalineLogger.error(RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    protected void notifyServerTimeoutExpired() {
        submitRemoteMethodInvocation(executorService, () -> {
            this.server.timeoutDidExpire(userID);
            return null;
        });
    }

    private void exportClient() throws RemoteException {
        clientStub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
    }

    private int registerClient(String username) {
        try {
            return server.registerClient(clientStub, username);
        } catch (RemoteException e){
            logOnException(RMI_EXCEPTION +e.getCause(), e);
        }
        return -1;
    }

    @Override
    public void setOperationTimeout(int timeout) {
        clientOperationTimeoutInSeconds = timeout;
    }

    @Override
    public void willSpawnAfterDeath(List<String> powerupsInHand) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    this.viewObserver.willSpawnAfterDeath(powerupsInHand);
                    return null;
                })
        );
    }

    @Override
    public void finalFrenzy() {
        this.viewObserver.displayFinalFrenzy();
    }

    @Override
    public void endOfTheGame(String scoreBoard) {
        this.viewObserver.endOfTheGame(scoreBoard);
    }

    @Override
    public void update(Map<String, List<String>> updates) {
        this.viewObserver.update(updates);
    }

    @Override
    public void willUseTagback(List<String> powerups, String nickname) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    this.viewObserver.tagback(powerups, nickname);
                    return null;
                })
        );
    }

    @Override
    public void createUser(String username){

            var userID = registerClient(username);

            if(userID == -1) {
                viewObserver.onLoginFailure();
            }
            else{
                this.userID = userID;
                viewObserver.onLoginSuccess(username);
            }
    }

    @Override
    public void joinWaitingRoom(String username) {
        submitRemoteMethodInvocation(executorService, () -> {
            server.joinWaitingRoom(username);
            return null;
        });
    }

    @Override
    public void didChooseSkulls(String choice) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                server.didChooseSkulls(choice, gameID);
                viewObserver.willChooseSpawnPoint();
                return null;
                })
        );
    }

    @Override
    public void newAction() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var situation = server.startActions(userID, gameID);
                    this.viewObserver.onChooseAction(situation);
                    return null;
                })
        );
    }

    @Override
    public void choseCharacter(String characterColor) {
        submitRemoteMethodInvocation(executorService, () -> {
            var availableCharacters = server.getAvailableCharacters(gameID);
            if (!server.choseCharacter(gameID, userID, characterColor)) this.viewObserver.willChooseCharacter(availableCharacters);
            else {
                this.viewObserver.onChooseCharacterSuccess(server.getCharacterName(gameID, userID));
            }
            return null;
        });
    }

    @Override
    public void willChooseGameMap() {
        submitRemoteMethodInvocation(executorService, () -> {
            this.viewObserver.willChooseGameMap();
            return null;
        });
    }

    @Override
    public void willStartTurn() {
        submitRemoteMethodInvocation(executorService, () -> {
            this.viewObserver.newAction();
            return null;
        });
    }

    @Override
    public void willStartFromRespawn() {
        submitRemoteMethodInvocation(executorService, () -> {
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
        submitRemoteMethodInvocation(executorService, () -> {
            server.didChooseGameMap(userID, gameID, configuration);
            return null;
        });
        viewObserver.willChooseSkulls();
    }

    @Override
    public void askForPossibleSpawnPoints() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    List<String> powerups;
                    powerups = server.getSpawnPowerups(userID, gameID);
                    viewObserver.onChooseSpawnPoint(powerups);
                    return null;
                })
        );
    }

    @Override
    public void askPicks(List<String> powerupToSell) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var possiblePicks = this.server.askPicks(userID, gameID, powerupToSell);
                    if(possiblePicks.isEmpty()) this.viewObserver.onGrabFailure();
                    else viewObserver.willChooseWhatToGrab(possiblePicks);
                    return null;
                })
        );
    }

    @Override
    public void didChooseWhatToGrab(String pick) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var ending = server.didChooseWhatToGrab(pick, userID, gameID);
                    if(ending.containsKey(Powerup.powerup_key)) {
                        this.viewObserver.onGrabFailurePowerup(new ArrayList<>(ending.values()));
                    } else if(ending.containsKey(Weapon.weapon_key)) {
                        this.viewObserver.onGrabFailureWeapon(new ArrayList<>(ending.values()));
                    } else viewObserver.onGrabSuccess();
                    return null;
                })
        );
    }

    @Override
    public void powerupGrabToDiscard(String powerup) {
        submitRemoteMethodInvocation(executorService, () -> {
            server.powerupToDiscard(userID, gameID, powerup);
            this.viewObserver.onGrabSuccess();
            return null;
        });
    }

    @Override
    public void weaponGrabToDiscard(String weapon) {
        submitRemoteMethodInvocation(executorService, () -> {
            server.weaponToDiscard(userID, gameID, weapon);
            this.viewObserver.onGrabSuccess();
            return null;
        });
    }

    @Override
    public void powerupSellingToGrabWeapon() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var powerups = server.getPowerupsInHand(userID, gameID);
                    this.viewObserver.sellPowerupToGrabWeapon(powerups);
                    return null;
                })
        );
    }

    @Override
    public void choseSpawnPoint(String spawnPoint, String otherPowerup){
        submitRemoteMethodInvocation(executorService, () -> {
            server.choseSpawnPoint(userID, gameID, spawnPoint, otherPowerup);
            viewObserver.newAction();
            return null;
        });
    }

    @Override
    public void getAvailableMoves() {
        submitRemoteMethodInvocation(executorService, () -> {
            viewObserver.willChooseMovement(server.getAvailableMoves(userID, gameID));
            return null;
        });
    }

    @Override
    public void move(String movement) {
        submitRemoteMethodInvocation(executorService, () -> {
            server.move(userID, gameID, movement);
            this.viewObserver.afterAction();
            return null;
        });
    }

    @Override
    public void askWeapons() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var weapons = this.server.askWeapons(userID, gameID);
                    if(weapons.isEmpty()) this.viewObserver.onShootPeopleFailure();
                    else this.viewObserver.willChooseWeapon(this.server.askWeapons(userID, gameID));
                    return null;
                })
        );
    }

    @Override
    public void useWeapon(String weaponSelected) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var weaponUsingProcess = this.server.useWeapon(userID, gameID, weaponSelected);
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
                    return null;
                })
        );
    }

    @Override
    public void useWeaponAfterPowerupAsking(String weaponSelected, List<String> powerups) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var weaponUsingContinued = this.server.useWeaponAfterPowerupSelling(userID, gameID, weaponSelected, powerups);
                    CommunicationMessage weaponProcess = CommunicationMessage.valueOf(weaponUsingContinued.get(CommunicationMessage.communication_message_key));
                    weaponUsingContinued.remove(CommunicationMessage.communication_message_key);
                    if(weaponProcess == CommunicationMessage.MODES_LIST) {
                        this.viewObserver.willChooseMode(weaponUsingContinued);
                    } else this.viewObserver.willChooseEffects(weaponUsingContinued);
                    return null;
                })
        );
    }

    @Override
    public void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    boolean lastDamage = true;
                    if(forPotentiableWeapon != null) lastDamage = Boolean.parseBoolean(forPotentiableWeapon);
                    List<String> powerups = this.server.makeDamage(userID, forPotentiableWeapon, indexOfEffect, gameID, damage, weapon);
                    if(lastDamage) {
                        this.viewObserver.didUseWeapon(powerups);
                    }
                    return null;
                })
        );
    }

    @Override
    public void useMode(String weapon, String effect) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var damageList = this.server.useEffect(userID, gameID, null, effect, weapon);
                    if(damageList.containsValue(Damage.no_damage)) this.viewObserver.onDamageFailure();
                    else this.viewObserver.willChooseDamage(damageList);
                    return null;
                })
        );
    }

    @Override
    public void useEffect(String weapon, List<String> effectsToUse) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
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
                    return null;
                })
        );
    }

    @Override
    public void askWeaponToReload() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var weaponInHand = this.server.getUnloadedWeaponInHand(userID, gameID);
                    if(weaponInHand.isEmpty()) this.viewObserver.onWeaponUnloadedFailure();
                    else this.viewObserver.willReload(this.server.getUnloadedWeaponInHand(userID, gameID));
                    return null;
                })
        );
    }

    @Override
    public void reloadWeapons(List<String> weaponsToReload) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    boolean didReload =  this.server.reload(weaponsToReload, userID, gameID);
                    if(didReload) this.viewObserver.onReloadSuccess();
                    else this.viewObserver.onReloadFailure();
                    return null;
                })
        );
    }

    @Override
    public void askForPowerupsToReload() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var powerups = server.getPowerupsInHand(userID, gameID);
                    if(powerups.isEmpty()) this.viewObserver.onPowerupInHandFailure();
                    else this.viewObserver.willSellPowerupToReload(powerups);
                    return null;
                })
        );
    }

    @Override
    public void sellPowerupToReload(List<String> powerups) {
        submitRemoteMethodInvocation(executorService, () -> {
            this.server.sellPowerupToReload(powerups, userID, gameID);
            this.askWeaponToReload();
            return null;
        });
    }

    @Override
    public void askForUsablePowerups() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var powerups = this.server.getUsablePowerups(userID, gameID);
                    if(powerups.isEmpty()) this.viewObserver.onTurnPowerupFailure();
                    else this.viewObserver.willChoosePowerup(powerups);
                    return null;
                })
        );
    }

    @Override
    public void askPowerupDamages(String powerup) {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var possibleDamages = this.server.getPowerupDamages(userID, gameID, powerup);
                    Map<String, String> damagesMap = new HashMap<>();
                    for(String damage : possibleDamages) damagesMap.put(Integer.toString(possibleDamages.indexOf(damage)), damage);
                    damagesMap.put(Powerup.powerup_key, powerup);
                    this.viewObserver.willChoosePowerupDamage(damagesMap);
                    return null;
                })
        );
    }

    @Override
    public void usePowerup(String powerup, String damage) {
        submitRemoteMethodInvocation(executorService, () -> {
            server.applyPowerupDamage(userID, gameID, powerup, damage);
            return null;
        });
    }

    @Override
    public void afterAction() {
        timeoutOperation(3*clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    var curSituation = server.afterAction(userID, gameID);
                    if(curSituation != null) this.viewObserver.onEndTurn(curSituation);
                    else this.viewObserver.newAction();
                    return null;
                })
        );
    }

    @Override
    public void checkDeaths() {
        submitRemoteMethodInvocation(executorService, () -> {
            this.server.checkDeathsBeforeEndTurn(userID, gameID);
            return null;
        });
    }

    @Override
    public void turnEnded() {
        submitRemoteMethodInvocation(executorService, () -> {
            this.server.turnEnded(userID, gameID);
            return null;
        });
    }

    @Override
    public void spawnAfterDeathChosen(String powerupChosen) {
        submitRemoteMethodInvocation(executorService, () -> {
            this.server.spawnAfterDeath(userID, gameID, powerupChosen);
            return null;
        });
    }

    @Override
    public void canMoveBeforeShoot() {
        timeoutOperation(clientOperationTimeoutInSeconds, () ->
                submitRemoteMethodInvocation(executorService, () -> {
                    List<String> movements = this.server.movementsBeforeShot(userID, gameID);
                    if(movements.isEmpty()) this.askWeapons();
                    else this.viewObserver.moveBeforeShot(movements);
                    return null;
                })
        );
    }

    @Override
    public void movesBeforeShoot(String movement) {
        submitRemoteMethodInvocation(executorService, () -> {
            this.server.movesBefore(movement, userID, gameID);
            this.askWeapons();
            return null;
        });
    }

    @Override
    public void tagback(String tagback) {
        submitRemoteMethodInvocation(executorService, () -> {
            server.tagback(tagback, userID, gameID);
            return null;
        });
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
        submitRemoteMethodInvocation(executorService, () -> {
            viewObserver.willChooseCharacter(availableCharacters);
            return null;
        });
    }
}