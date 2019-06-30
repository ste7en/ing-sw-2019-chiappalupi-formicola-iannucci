package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.networking.ConnectionHandlerSenderDelegate;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.view.View;

import java.io.*;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.*;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;

/**
 * Main class of the client. It will set up the networking and send/receive events and requests.
 *
 * @author Stefano Formicola
 * @author Daniele Chiappalupi
 */
public class ClientSocket extends Client implements ConnectionHandlerReceiverDelegate {

    /**
     * Log strings
     */
    private static final String UNKNOWN_COMMUNICATION_MESSAGE = "Unsupported communication message: ";

    /**
     * Delegate class responsible to send messages
     */
    private ConnectionHandlerSenderDelegate senderDelegate;

    public ClientSocket(String host, Integer port) {
        super(host, port);
    }

    @Override
    protected void setupConnection() {
        AdrenalineLogger.info(INFO);
        AdrenalineLogger.config("A SOCKET connection is setting up...");
        try {
                senderDelegate = new ClientSocketConnectionHandler(serverName, connectionPort, this);
                logOnSuccess(ON_SUCCESS);
            } catch (UnknownHostException e) {
                logOnException(UNKNOWN_HOST, e);
                askForUserInput();
                setupConnection();
            } catch (ConnectException e) {
                logOnException(CONNECTION_REFUSED, e);
                askForUserInput();
                setupConnection();
            }
            catch (IOException e) {
                logOnException(IO_EXC+CONN_RETRY, e);
                setupConnection();
            }
    }

    /**
     * Receives a json message from a delegator
     * and parses its content
     * @param message json message
     * @param sender a the connection handler delegated to send messages
     */
    @Override
    public void receive(String message, ConnectionHandlerSenderDelegate sender) {
        var task = new Thread(
            () -> {
                var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
                var id = CommunicationMessage.getConnectionIDFrom(message);
                Map<String, String> args = CommunicationMessage.getMessageArgsFrom(message);

                switch (communicationMessage) {
                    case PING:
                        sender.send(CommunicationMessage.from(id, PONG));
                        break;
                    case CREATE_USER_OK:
                        super.userID = id;
                        this.viewObserver.onLoginSuccess(args.get(User.username_key));
                        break;
                    case CREATE_USER_FAILED:
                        this.viewObserver.onLoginFailure();
                        break;
                    case USER_JOINED_WAITING_ROOM:
                        /* Basically, if the gameID is not null the client won't be notified
                           he entered the waitingRoom because he actually didn't. Instead,
                           the client joined a game he already started previously. */
                        if (this.gameID == null) this.viewObserver.didJoinWaitingRoom();
                        break;
                    case USER_JOINED_GAME:
                        this.gameID = UUID.fromString(args.get(GameLogic.gameID_key));
                        this.viewObserver.onStart();
                        break;
                    case CHOOSE_CHARACTER:
                        var availableCharacters = Arrays.asList(args.get(Character.character_list).split(", "));
                        this.viewObserver.willChooseCharacter(availableCharacters);
                        break;
                    case CHARACTER_CHOSEN_OK:
                        //TODO: - send an ACK??
                        break;
                    case CHARACTER_NOT_AVAILABLE:
                        this.viewObserver.onFailure(View.CHARACTER_NOT_AVAILABLE);
                        break;
                    case CHOOSE_MAP:
                        this.viewObserver.willChooseGameMap();
                        break;
                    case CHOOSE_SPAWN_POINT:
                        this.viewObserver.willChooseSpawnPoint();
                        break;
                    case DRAWN_SPAWN_POINT:
                        this.viewObserver.onChooseSpawnPoint(new ArrayList<>(args.values()));
                        break;
                    case CHOOSE_ACTION:
                        this.viewObserver.onChooseAction();
                    case SHOOT_PEOPLE:
                        this.viewObserver.willChooseWeapon(new ArrayList<>(args.values()));
                        break;
                    case SHOOT_PEOPLE_FAILURE:
                        this.viewObserver.onShootPeopleFailure();
                        break;
                    case DAMAGE_FAILURE:
                        this.viewObserver.onDamageFailure();
                        break;
                    case POWERUP_SELLING_LIST:
                        this.viewObserver.willChoosePowerupSelling(args);
                        break;
                    case DAMAGE_LIST:
                        if(args.containsValue(Damage.no_damage)) this.viewObserver.onDamageFailure();
                        else this.viewObserver.willChooseDamage(args);
                        break;
                    case MODES_LIST:
                        this.viewObserver.willChooseMode(args);
                        break;
                    case EFFECT_TO_USE:
                        this.viewObserver.willChooseEffects(args);
                        break;
                    case LAST_DAMAGE_DONE:
                        this.viewObserver.didUseWeapon();
                        this.viewObserver.askPowerupAfterShot(new ArrayList<>(args.values()));
                        break;
                    case NO_WEAPON_UNLOADED_IN_HAND:
                        this.viewObserver.onWeaponUnloadedFailure();
                        break;
                    case POWERUP_TO_RELOAD:
                        if (args.values().isEmpty()) this.viewObserver.onPowerupInHandFailure();
                        else this.viewObserver.willSellPowerupToReload(new ArrayList<>(args.values()));
                        break;
                    case WEAPON_LIST:
                        this.viewObserver.willReload(new ArrayList<>(args.values()));
                        break;
                    case RELOAD_WEAPON_FAILED:
                        this.viewObserver.onReloadFailure();
                        break;
                    case RELOAD_WEAPON_OK:
                        this.viewObserver.onReloadSuccess();
                        break;
                    case NO_TURN_POWERUP:
                        this.viewObserver.onTurnPowerupFailure();
                        break;
                    case POWERUP_LIST:
                        this.viewObserver.willChoosePowerup(new ArrayList<>(args.values()));
                        break;
                    case POWERUP_DAMAGES_LIST:
                        this.viewObserver.willChoosePowerupDamage(args);
                        break;
                    default:
                        logOnFailure(UNKNOWN_COMMUNICATION_MESSAGE+communicationMessage);
                        break;
                }
            }
        );
        task.setDaemon(false);
        task.setPriority(Thread.MAX_PRIORITY);
        task.start();
    }

    /**
     * Sends a json message through its sender delegate
     * @param message a json message to send
     */
    public void send(String message) {
        senderDelegate.send(message);
    }

    @Override
    public void createUser(String username) {
        var args = new HashMap<String, String>();
        args.put(User.username_key, username);
        this.send(CommunicationMessage.from(0, CREATE_USER, args));
    }

    @Override
    public void joinWaitingRoom(String username) {
        var args = new HashMap<String, String>();
        args.put(User.username_key, username);
        this.send(CommunicationMessage.from(userID, USER_JOIN_WAITING_ROOM, args));
    }

    @Override
    public void choseCharacter(String characterColor){
        var args = new HashMap<String, String>();
        args.put(Character.character, characterColor);
        args.put(GameLogic.gameID_key, gameID.toString());
        this.send(CommunicationMessage.from(userID, CHOOSE_CHARACTER, args, gameID));
    }

    @Override
    public void choseGameMap(String configuration){
        var args = new HashMap<String, String>();
        args.put(GameMap.gameMap_key, configuration);
        this.send(CommunicationMessage.from(userID, MAP_CHOSEN, args, gameID));
    }

    @Override
    public void askForPossibleSpawnPoints(){
        this.send(CommunicationMessage.from(userID, ASK_FOR_SPAWN_POINT, gameID));
    }

    @Override
    public void choseSpawnPoint(String spawnPoint, String otherPowerup) {
        var args = new HashMap<String, String>();
        args.put(Powerup.powerup_key, otherPowerup);
        args.put(Powerup.spawnPowerup_key, otherPowerup);
        this.send(CommunicationMessage.from(userID, SPAWN_POINT_CHOSEN, args, gameID));
    }

    @Override
    public void askWeapons() {
        this.send(CommunicationMessage.from(userID, SHOOT_PEOPLE, gameID));
    }

    @Override
    public void useWeapon(String weaponSelected) {
        Map<String, String> args = new HashMap<>();
        args.put(Weapon.weapon_key, weaponSelected);
        this.send(CommunicationMessage.from(userID, WEAPON_TO_USE, args, gameID));
    }

    @Override
    public void useWeaponAfterPowerupAsking(String weaponSelected, List<String> powerups) {
        Map<String, String> args = new HashMap<>();
        args.put(Weapon.weapon_key, weaponSelected);
        for(String powerup : powerups) args.put(Integer.toString(powerups.indexOf(powerup)), powerup);
        this.send(CommunicationMessage.from(userID, POWERUP_SELLING_DECIDED, args, gameID));
    }

    @Override
    public void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        Map<String, String> damageToDo = new HashMap<>();
        damageToDo.put(Damage.damage_key, damage);
        damageToDo.put(Weapon.weapon_key, weapon);
        damageToDo.put(Effect.effect_key, indexOfEffect);
        if(forPotentiableWeapon != null) damageToDo.put(PotentiableWeapon.forPotentiableWeapon_key, forPotentiableWeapon);
        boolean lastDamage = Boolean.parseBoolean(forPotentiableWeapon);
        if(lastDamage) this.send(CommunicationMessage.from(userID, LAST_DAMAGE, damageToDo, gameID));
        else this.send(CommunicationMessage.from(userID, DAMAGE_TO_MAKE, damageToDo, gameID));
    }

    @Override
    public void useMode(String weapon, String effect) {
        Map<String, String> args = new HashMap<>();
        args.put(Weapon.weapon_key, weapon);
        args.put(Effect.effect_key, effect);
        this.send(CommunicationMessage.from(userID, EFFECT_TO_USE, args, gameID));
    }

    @Override
    public void useEffect(String weapon, List<String> effectsToUse) {
        Map<String, String> args = new HashMap<>();
        args.put(Weapon.weapon_key, weapon);
        while(!effectsToUse.isEmpty()) {
            boolean forPotentiableWeapon;
            forPotentiableWeapon = effectsToUse.size() == 1;
            String potentiableBoolean = Boolean.toString(forPotentiableWeapon);
            args.put(PotentiableWeapon.forPotentiableWeapon_key, potentiableBoolean);
            args.put(Effect.effect_key, effectsToUse.get(0));
            effectsToUse.remove(0);
            Map<String, String> argsBox = new HashMap<>(args);
            this.send(CommunicationMessage.from(userID, EFFECT_TO_USE, argsBox, gameID));
            args.remove(PotentiableWeapon.forPotentiableWeapon_key);
            args.remove(Effect.effect_key);
        }
    }

    @Override
    public void askWeaponToReload() {
        this.send(CommunicationMessage.from(userID, ASK_WEAPONS, gameID));
    }

    @Override
    public void reloadWeapons(List<String> weaponsToReload) {
        Map<String, String> args = new HashMap<>();
        for(String weapon : weaponsToReload)
            args.put(Integer.toString(weaponsToReload.indexOf(weapon)), weapon);
        this.send(CommunicationMessage.from(userID, WEAPON_TO_RELOAD, args, gameID));
    }

    @Override
    public void askForPowerupsToReload() {
        this.send(CommunicationMessage.from(userID, ASK_POWERUP_TO_RELOAD, gameID));
    }

    @Override
    public void sellPowerupToReload(List<String> powerups) {
        Map<String, String> args = new HashMap<>();
        for(String powerup : powerups)
            args.put(Integer.toString(powerups.indexOf(powerup)), powerup);
        this.send(CommunicationMessage.from(userID, SELL_POWERUP, args, gameID));
        this.askWeaponToReload();
    }

    @Override
    public void askForUsablePowerups() {
        this.send(CommunicationMessage.from(userID, ASK_POWERUPS, gameID));
    }

    @Override
    public void askPowerupDamages(String powerup) {
        Map<String, String> args = new HashMap<>();
        args.put(Powerup.powerup_key, powerup);
        this.send(CommunicationMessage.from(userID, ASK_POWERUP_DAMAGES, args, gameID));
    }

    @Override
    public void usePowerup(String powerup, String damage) {
        Map<String, String> args = new HashMap<>();
        args.put(Powerup.powerup_key, powerup);
        args.put(Damage.damage_key, damage);
        this.send(CommunicationMessage.from(userID, POWERUP_DAMAGE_TO_MAKE, args, gameID));
    }

}