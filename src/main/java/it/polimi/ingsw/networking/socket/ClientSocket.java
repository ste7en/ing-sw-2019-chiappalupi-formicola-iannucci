package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.Player;
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
    private static final String SOCKET_CONFIG_STRING          = "A SOCKET connection is setting up...";
    private static final String EMPTY = "empty";

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
        AdrenalineLogger.config(SOCKET_CONFIG_STRING);
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

    @Override
    protected void notifyServerTimeoutExpired() {
        this.send(CommunicationMessage.from(userID, TIMEOUT_DID_EXPIRE, gameID));
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
                var id                   = CommunicationMessage.getConnectionIDFrom(message);
                Map<String, String> args = CommunicationMessage.getMessageArgsFrom(message);
                var timeout              = CommunicationMessage.getTimeoutFrom(message);

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
                        var availableCharacters = Arrays.asList(args.get(Character.character_list).split(listDelimiter));
                        this.viewObserver.willChooseCharacter(availableCharacters);
                        break;
                    case CHARACTER_CHOSEN_OK:
                        this.viewObserver.onChooseCharacterSuccess(args.get(Character.character));
                        break;
                    case CHARACTER_NOT_AVAILABLE:
                        this.viewObserver.onFailure(View.CHARACTER_NOT_AVAILABLE);
                        break;
                    case CHOOSE_MAP:
                        this.viewObserver.willChooseGameMap();
                        break;
                    case CHOOSE_SKULLS:
                        this.viewObserver.willChooseSkulls();
                        break;
                    case CHOOSE_SPAWN_POINT:
                        timeoutOperation(timeout, this.viewObserver::willChooseSpawnPoint);
                        break;
                    case DRAWN_SPAWN_POINT:
                        timeoutOperation(timeout, () -> this.viewObserver.onChooseSpawnPoint(new ArrayList<>(args.values())));
                        break;
                    case KEEP_ACTION:
                        this.viewObserver.newAction();
                        break;
                    case CHOOSE_ACTION:
                        timeoutOperation(timeout, () -> this.viewObserver.onChooseAction(args.get(GameMap.gameMap_key)));
                        break;
                    case CHOOSE_MOVEMENT:
                        timeoutOperation(timeout, () -> this.viewObserver.willChooseMovement(Arrays.asList(args.get(GameLogic.available_moves).split(listDelimiter))));
                        break;
                    case AVAILABLE_POWERUP_TO_SELL_TO_GRAB:
                        timeoutOperation(timeout, () -> this.viewObserver.sellPowerupToGrabWeapon(new ArrayList<>(args.values())));
                        break;
                    case POSSIBLE_PICKS:
                        timeoutOperation(timeout, () -> this.viewObserver.willChooseWhatToGrab(new ArrayList<>(args.values())));
                        break;
                    case GRAB_SUCCESS:
                        this.viewObserver.onGrabSuccess();
                        break;
                    case GRAB_FAILURE_POWERUP:
                        timeoutOperation(timeout, () -> this.viewObserver.onGrabFailurePowerup(new ArrayList<>(args.values())));
                        break;
                    case GRAB_FAILURE_WEAPON:
                        timeoutOperation(timeout, () -> this.viewObserver.onGrabFailureWeapon(new ArrayList<>(args.values())));
                        break;
                    case GRAB_FAILURE:
                        timeoutOperation(timeout, this.viewObserver::onGrabFailure);
                        break;
                    case SHOOT_PEOPLE:
                        timeoutOperation(timeout, () -> this.viewObserver.willChooseWeapon(new ArrayList<>(args.values())));
                        break;
                    case SHOOT_PEOPLE_FAILURE:
                        timeoutOperation(timeout, this.viewObserver::onShootPeopleFailure);
                        break;
                    case EFFECT_TO_USE:
                    case EFFECTS_LIST:
                        timeoutOperation(timeout, () -> this.viewObserver.willChooseEffects(args));
                        break;
                    case DAMAGE_FAILURE:
                        timeoutOperation(timeout, this.viewObserver::onDamageFailure);
                        break;
                    case POWERUP_SELLING_LIST:
                        timeoutOperation(timeout, () -> this.viewObserver.willChoosePowerupSelling(args));
                        break;
                    case DAMAGE_LIST:
                        timeoutOperation(timeout, () -> {
                            if(args.containsValue(Damage.no_damage)) this.viewObserver.onDamageFailure();
                            else this.viewObserver.willChooseDamage(args);
                        });
                        break;
                    case MODES_LIST:
                        timeoutOperation(timeout, () -> this.viewObserver.willChooseMode(args));
                        break;
                    case DAMAGE_DONE:
                        synchronized (this) {
                            this.notifyAll();
                        }
                        break;
                    case LAST_DAMAGE_DONE:
                        synchronized (this) {
                            this.notifyAll();
                        }
                        timeoutOperation(timeout, () -> this.viewObserver.didUseWeapon(new ArrayList<>(args.values())));
                        break;
                    case NO_WEAPON_UNLOADED_IN_HAND:
                        this.viewObserver.onWeaponUnloadedFailure();
                        break;
                    case POWERUP_TO_RELOAD:
                        timeoutOperation(timeout, () -> {
                            if (args.values().isEmpty()) this.viewObserver.onPowerupInHandFailure();
                            else this.viewObserver.willSellPowerupToReload(new ArrayList<>(args.values()));
                        });
                        break;
                    case WEAPON_LIST:
                        timeoutOperation(timeout, () -> this.viewObserver.willReload(new ArrayList<>(args.values())));
                        break;
                    case RELOAD_WEAPON_FAILED:
                        timeoutOperation(timeout, this.viewObserver::onReloadFailure);
                        break;
                    case RELOAD_WEAPON_OK:
                        timeoutOperation(timeout, this.viewObserver::onReloadSuccess);
                        break;
                    case NO_TURN_POWERUP:
                        timeoutOperation(timeout, this.viewObserver::onTurnPowerupFailure);
                        break;
                    case POWERUP_LIST:
                        timeoutOperation(timeout, () -> this.viewObserver.willChoosePowerup(new ArrayList<>(args.values())));
                        break;
                    case POWERUP_DAMAGES_LIST:
                        timeoutOperation(timeout, () -> this.viewObserver.willChoosePowerupDamage(args));
                        break;
                    case AFTER_ACTION:
                        this.viewObserver.afterAction();
                        break;
                    case END_TURN:
                        timeoutOperation(3*timeout, () -> this.viewObserver.onEndTurn(args.get(GameMap.gameMap_key)));
                        break;
                    case UPDATE_SITUATION:
                        this.viewObserver.displayChange(args.get(GameMap.gameMap_key));
                        break;
                    case SPAWN_AFTER_DEATH:
                        this.viewObserver.willSpawnAfterDeath(new ArrayList<>(args.values()));
                        break;
                    case DISPLAY_FINAL_FRENZY:
                        this.viewObserver.displayFinalFrenzy();
                        break;
                    case GAME_ENDED:
                        this.viewObserver.endOfTheGame(args.get(GameMap.gameMap_key));
                        break;
                    case NO_MOVES_BEFORE_SHOT:
                        this.askWeapons();
                        break;
                    case MOVES_BEFORE_SHOOT:
                        this.viewObserver.moveBeforeShot(new ArrayList<>(args.values()));
                        break;
                    case UPDATE_ALL:
                        updateAll(args);
                        break;
                    case WILL_CHOOSE_TAGBACK:
                        willChooseTagback(args);
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
     * Used when the player should choose if using a tagback or not
     * @param args it's the map sent by the server
     */
    private void willChooseTagback(Map<String, String> args) {
        String nickname = args.get(Player.playerKey_player);
        args.remove(Player.playerKey_player);
        this.viewObserver.tagback(new ArrayList<>(args.values()), nickname);
    }

    /**
     * sends all the updates to the view.
     * @param args it's a map containing the update.
     */
    private void updateAll(Map<String, String> args) {
        List<String> updates = new ArrayList<>(args.values());
        String key = "";
        for(String s : args.keySet()) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                key = s;
            }
        }
        Map<String, List<String>> updateMap = new HashMap<>();
        if(updates.size() != 1 || !updates.get(0).equals(EMPTY)) updateMap.put(key, updates);
        else updateMap.put(key, new ArrayList<>());
        this.viewObserver.update(updateMap);
    }

    /**
     * Sends a json message through its sender delegate
     * @param message a json message to send
     */
    private void send(String message) {
        senderDelegate.send(message);
    }

    @Override
    public void createUser(String username) {
        this.send(CommunicationMessage.from(0, CREATE_USER, argsFrom(User.username_key, username)));
    }

    @Override
    public void joinWaitingRoom(String username) {
        this.send(CommunicationMessage.from(userID, USER_JOIN_WAITING_ROOM, argsFrom(User.username_key, username)));
    }

    @Override
    public void didChooseSkulls(String choice) {
        Map<String, String> args = new HashMap<>();
        args.put(Board.skulls_key, choice);
        this.send(CommunicationMessage.from(userID, SKULLS_CHOSEN, args, gameID));
    }

    @Override
    public void newAction() {
        this.send(CommunicationMessage.from(userID, NEW_ACTION, gameID));
    }

    @Override
    public void choseCharacter(String characterColor) {
        this.send(CommunicationMessage.from(userID, CHOOSE_CHARACTER, argsFrom(Character.character, characterColor, GameLogic.gameID_key, gameID.toString()), gameID));
    }

    @Override
    public void choseGameMap(String configuration) {
        var args = new HashMap<String, String>();
        args.put(GameMap.gameMap_key, configuration);
        this.send(CommunicationMessage.from(userID, MAP_CHOSEN, args, gameID));
    }

    @Override
    public void askForPossibleSpawnPoints(){
        this.send(CommunicationMessage.from(userID, ASK_FOR_SPAWN_POINT, gameID));
    }

    @Override
    public void askPicks(List<String> powerupToSell) {
        Map<String, String> args = new HashMap<>();
        for(String s : powerupToSell)
            args.put(Integer.toString(powerupToSell.indexOf(s)), s);
        this.send(CommunicationMessage.from(userID, GRAB_SOMETHING, args, gameID));
    }

    @Override
    public void didChooseWhatToGrab(String pick) {
        Map<String, String> args = new HashMap<>();
        args.put(AmmoTile.ammoTile_key, pick);
        this.send(CommunicationMessage.from(userID, DID_CHOOSE_WHAT_TO_GRAB, args, gameID));
    }

    @Override
    public void powerupGrabToDiscard(String powerup) {
        Map<String, String> args = new HashMap<>();
        args.put(Powerup.powerup_key, powerup);
        this.send(CommunicationMessage.from(userID, GRAB_DISCARD_POWERUP, args, gameID));
    }

    @Override
    public void weaponGrabToDiscard(String weapon) {
        Map<String, String> args = new HashMap<>();
        args.put(Weapon.weapon_key, weapon);
        this.send(CommunicationMessage.from(userID, GRAB_DISCARD_WEAPON, args, gameID));
    }

    @Override
    public void powerupSellingToGrabWeapon() {
        this.send(CommunicationMessage.from(userID, SELL_POWERUP_TO_GRAB, gameID));
    }

    @Override
    public void choseSpawnPoint(String spawnPoint, String otherPowerup) {
        var args = new HashMap<String, String>();
        args.put(Powerup.powerup_key, otherPowerup);
        args.put(Powerup.spawnPowerup_key, spawnPoint);
        this.send(CommunicationMessage.from(userID, SPAWN_POINT_CHOSEN, args, gameID));
    }

    @Override
    public void getAvailableMoves() {
        this.send(CommunicationMessage.from(userID, GET_AVAILABLE_MOVES, gameID));
    }

    @Override
    public void move(String movement) {
        this.send(CommunicationMessage.from(userID, MOVE, argsFrom(GameLogic.movement, movement), gameID));
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
        if(forPotentiableWeapon == null) lastDamage = true;
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
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    AdrenalineLogger.error(e.toString());
                    AdrenalineLogger.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
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

    @Override
    public void afterAction() {
        this.send(CommunicationMessage.from(userID, END_ACTION, gameID));
    }

    @Override
    public void checkDeaths() {
        this.send(CommunicationMessage.from(userID, CHECK_DEATHS_AFTER_TURN, gameID));
    }

    @Override
    public void turnEnded() {
        this.send(CommunicationMessage.from(userID, TURN_ENDED, gameID));
    }

    @Override
    public void spawnAfterDeathChosen(String powerupChosen) {
        this.send(CommunicationMessage.from(userID, SPAWN_POINT_AFTER_DEATH_CHOSEN, argsFrom(Powerup.powerup_key, powerupChosen), gameID));
    }

    @Override
    public void canMoveBeforeShoot() {
        this.send(CommunicationMessage.from(userID, GET_MOVES_BEFORE_SHOOT, gameID));
    }

    @Override
    public void movesBeforeShoot(String movement) {
        this.send(CommunicationMessage.from(userID, MOVE_CHOSEN_BEFORE_SHOT, argsFrom(Cell.cell_key, movement)));
        this.askWeapons();
    }

    @Override
    public void tagback(String tagback) {
        this.send(CommunicationMessage.from(userID, TAGBACK_CHOSEN, argsFrom(Powerup.powerup_key, tagback), gameID));
    }
}