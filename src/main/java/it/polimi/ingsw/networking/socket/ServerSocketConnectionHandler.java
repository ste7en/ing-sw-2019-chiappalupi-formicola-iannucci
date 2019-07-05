package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionState;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.utility.*;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.networking.utility.ConnectionState.*;
import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * This class is responsible for handling a single client socket connection,
 * it implements {@link Runnable} interface and runs into a single thread with
 * multiple thread implementing message handling.
 *
 * @author Stefano Formicola
 */
public class ServerSocketConnectionHandler extends ServerConnectionHandler implements Runnable {

    private static final String EMPTY = "empty";
    /**
     * The socket instance, the object that represents a connection
     * between client and server.
     */
    private transient Socket socket;

    /**
     * A buffered outbox for messages
     */
    private ConcurrentLinkedQueue<String> outBuf;

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S3008")
    private static final String IO_EXC                  = "An IOException has occurred during a socket operation";
    private static final String STREAM_SUCC             = "Socket Input/Output streams successfully created.";
    private static final String CONN_CLOSED             = "Connection closed with the client :: ";
    private static final String PING_TIMEOUT            = "Ping timeout. Closing connection socket :: ";
    private static final String INTERRUPTED_EXCEPTION   = "Thread interrupted exception.";

    /**
     * Basic class constructor
     * @param socket socket connection with the client
     * @param server the server instance
     */
    ServerSocketConnectionHandler(Socket socket, Server server, int clientOperationTimeoutInSeconds) {
        this.socket = socket;
        this.outBuf = new ConcurrentLinkedQueue<>();
        super.connectionState = ONLINE;
        super.server = server;
        super.executorService = Executors.newCachedThreadPool();
        super.clientOperationTimeoutInSeconds = clientOperationTimeoutInSeconds;
    }

    /**
     * Method called in a new thread when the object is created.
     */
    @Override
    public void run() {
        Ping.getInstance().addPing(this);

        try(var outStr = socket.getOutputStream();
            var inStr  = socket.getInputStream()) {

            var inScanner = new Scanner(inStr);
            var printWriter = new PrintWriter(outStr, true);
            logOnSuccess(STREAM_SUCC);

            while(connectionState == ONLINE) {
                if (inStr.available() != 0) receive(inScanner.nextLine());
                if (!outBuf.isEmpty()) outBuf.forEach(printWriter::println);
                outBuf.clear();
                Thread.sleep(100);
            }
            if (connectionState == CLOSED) socket.close();
            AdrenalineLogger.info(CONN_CLOSED + socket.toString());
        } catch (IOException e) {
            logOnException(IO_EXC, e);
        } catch (InterruptedException e) {
            logOnException(INTERRUPTED_EXCEPTION , e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sends a message through its connection
     * @param message the message to send
     */
    private void send(String message) {
        outBuf.add(message);
    }

    /**
     * The ServerSocketConnectionHandler calling this method will
     * send a PING message to the client when asked.
     */
    @Override
    public void ping() {
        var pingMessage = CommunicationMessage.from(getConnectionHashCode(), CommunicationMessage.PING);
        this.send(pingMessage);
    }

    /**
     * The Ping handler class will call this method if the
     * client does not respond to a PING message after a
     * determined delay.
     */
    @Override
    public synchronized void closeConnection() {
        this.connectionState = ConnectionState.CLOSED;
        this.server.didDisconnect(this);
        AdrenalineLogger.info(PING_TIMEOUT + socket.toString());
    }

    /**
     * Used to distinguish between connected pingable clients
     *
     * @return a Pingable instance identifier
     */
    @Override
    public int getConnectionHashCode() {
        return this.hashCode();
    }

    /**
     * @return true if the socket is connected and the connection is available
     */
    @Override
    public boolean isConnectionAvailable() {
        return connectionState == ONLINE;
    }

    /**
     * Receives a message from a delegator
     * @param message the message received
     */
    private void receive(String message) {
        executorService.submit( () -> {
            var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
            var connectionID         = CommunicationMessage.getConnectionIDFrom(message);
            var args                 = CommunicationMessage.getMessageArgsFrom(message);
            var gameID               = CommunicationMessage.getMessageGameIDFrom(message);

            switch (communicationMessage) {
                case PONG:
                    Ping.getInstance().didPong(connectionID);
                    break;
                case TIMEOUT_DID_EXPIRE:
                    server.timeoutDidExpire(connectionID);
                    break;
                case CREATE_USER:
                    createUser(connectionID, args);
                    break;
                case USER_JOIN_WAITING_ROOM:
                    joinWaitingRoom(connectionID, args);
                    break;
                case CHOOSE_CHARACTER:
                    didChooseCharacter(gameID, connectionID, args.get(Character.character));
                    break;
                case MAP_CHOSEN:
                    mapChosen(connectionID, args, gameID);
                    break;
                case SKULLS_CHOSEN:
                    skullsChosen(connectionID, args, gameID);
                    break;
                case NEW_ACTION:
                    newAction(connectionID, gameID);
                    break;
                case GET_AVAILABLE_MOVES:
                    chooseMovement(connectionID, gameID);
                    break;
                case MOVE:
                    server.move(connectionID, gameID, args.get(GameLogic.movement));
                    this.send(CommunicationMessage.from(connectionID, AFTER_ACTION));
                    break;
                case GRAB_SOMETHING:
                    grabSomething(connectionID, gameID, args);
                    break;
                case SELL_POWERUP_TO_GRAB:
                    sellPowerupToGrab(connectionID, gameID);
                    break;
                case DID_CHOOSE_WHAT_TO_GRAB:
                    choseWhatToGrab(connectionID, args, gameID);
                    break;
                case GRAB_DISCARD_POWERUP:
                    grabDiscardPowerup(connectionID, args, gameID);
                    break;
                case GRAB_DISCARD_WEAPON:
                    grabDiscardWeapon(connectionID, args, gameID);
                    break;
                case ASK_FOR_SPAWN_POINT:
                    spawnPointGenerator(connectionID, gameID);
                    break;
                case SPAWN_POINT_CHOSEN:
                    spawnPointChosen(connectionID, args, gameID);
                    break;
                case SHOOT_PEOPLE:
                    shootPeople(connectionID, gameID);
                    break;
                case WEAPON_TO_USE:
                    weaponToUse(connectionID, args, gameID);
                    break;
                case POWERUP_SELLING_DECIDED:
                    powerupSellingDecided(connectionID, args, gameID);
                    break;
                case LAST_DAMAGE:
                case DAMAGE_TO_MAKE:
                    damageToDo(communicationMessage, connectionID, args, gameID);
                    break;
                case EFFECT_TO_USE:
                    effectToUse(connectionID, args, gameID);
                    break;
                case ASK_POWERUP_TO_RELOAD:
                    askPowerupToReload(connectionID, gameID);
                    break;
                case WEAPON_TO_RELOAD:
                    weaponToReload(connectionID, args, gameID);
                    break;
                case ASK_WEAPONS:
                    askUnloadedWeapons(connectionID, gameID);
                    break;
                case ASK_POWERUPS:
                    askPowerups(connectionID, gameID);
                    break;
                case ASK_POWERUP_DAMAGES:
                    askPowerupDamages(connectionID, args, gameID);
                    break;
                case POWERUP_DAMAGE_TO_MAKE:
                    server.applyPowerupDamage(connectionID, gameID, args.get(Powerup.powerup_key), args.get(Damage.damage_key));
                    break;
                case SELL_POWERUP:
                    server.sellPowerupToReload(new ArrayList<>(args.values()), connectionID, gameID);
                    break;
                case END_ACTION:
                    endAction(connectionID, gameID);
                    break;
                case TURN_ENDED:
                    this.server.turnEnded(connectionID, gameID);
                    break;
                case CHECK_DEATHS_AFTER_TURN:
                    this.server.checkDeathsBeforeEndTurn(gameID);
                    break;
                case SPAWN_POINT_AFTER_DEATH_CHOSEN:
                    this.server.spawnAfterDeath(connectionID, gameID, args.get(Powerup.powerup_key));
                    break;
                case ASK_CAN_CONTINUE:
                    if(this.server.canContinueAfterDeathsRespawn(connectionID, gameID)) send(CommunicationMessage.from(connectionID, CAN_CONTINUE_TRUE));
                    break;
                case GET_MOVES_BEFORE_SHOOT:
                    movesBeforeTheShot(connectionID, gameID);
                    break;
                case TAGBACK_CHOSEN:
                    this.server.tagback(args.get(Powerup.powerup_key), connectionID, gameID);
                    break;
                case MOVE_CHOSEN_BEFORE_SHOT:
                    this.server.movesBefore(args.get(Cell.cell_key), connectionID, gameID);
                    break;
                default:
                    break;
            }
        });
    }

    private void movesBeforeTheShot(int connectionID, UUID gameID) {
        List<String> movements = this.server.movementsBeforeShot(connectionID, gameID);
        if(movements.isEmpty()) this.send(CommunicationMessage.from(connectionID, NO_MOVES_BEFORE_SHOT));
        else {
            Map<String, String> responseArgs = new HashMap<>();
            for(String string : movements) responseArgs.put(Integer.toString(movements.indexOf(string)), string);
            this.send(CommunicationMessage.from(connectionID, MOVES_BEFORE_SHOOT, responseArgs, clientOperationTimeoutInSeconds));
        }
    }

    /**
     * This method is called when the number of skulls has been chosen from the player.
     * @param connectionID it's the ID of the user
     * @param args it's the map containing the arguments needed to perform the action
     * @param gameID it's the ID of the game
     */
    private void skullsChosen(int connectionID, Map<String, String> args, UUID gameID) {
        this.server.didChooseSkulls(args.get(Board.skulls_key), gameID);
        this.send(CommunicationMessage.from(connectionID, CHOOSE_SPAWN_POINT, clientOperationTimeoutInSeconds));
    }

    /**
     * This method is called when an action has been successfully executed
     * @param connectionID it's the ID of the user
     * @param gameID it's the ID of the game
     */
    private void endAction(int connectionID, UUID gameID) {
        String curSituation = this.server.afterAction(connectionID, gameID);
        if(curSituation != null) {
            Map<String, String> responseArgs = new HashMap<>();
            responseArgs.put(GameMap.gameMap_key, curSituation);
            this.send(CommunicationMessage.from(connectionID, END_TURN, responseArgs, clientOperationTimeoutInSeconds));
        }
        else this.send(CommunicationMessage.from(connectionID, KEEP_ACTION));
    }

    /**
     * This method is called when a new action or turn is starting
     * @param connectionID it's the ID of the user
     * @param gameID it's the ID of the game
     */
    private void newAction(int connectionID, UUID gameID) {
        String s = this.server.startActions(connectionID, gameID);
        Map<String, String> responseArgs = new HashMap<>();
        responseArgs.put(GameMap.gameMap_key, s);
        this.send(CommunicationMessage.from(connectionID, CHOOSE_ACTION, responseArgs, clientOperationTimeoutInSeconds));
    }

    /**
     * This method is called when the player has decided that he wants to sell powerups to afford the cost of grabbing a weapon
     * @param connectionID it's the ID of the user
     * @param gameID it's the ID of the game
     */
    private void sellPowerupToGrab(int connectionID, UUID gameID) {
        List<String> powerups = server.getPowerupsInHand(connectionID, gameID);
        if(powerups.isEmpty()) this.send(CommunicationMessage.from(connectionID, GRAB_FAILURE, clientOperationTimeoutInSeconds));
        Map<String, String> responseArgs = new HashMap<>();
        for(String s : powerups)
            responseArgs.put(Integer.toString(powerups.indexOf(s)), s);
        this.send(CommunicationMessage.from(connectionID, AVAILABLE_POWERUP_TO_SELL_TO_GRAB, responseArgs, clientOperationTimeoutInSeconds));
    }

    /**
     * This method is called when the player has decided which weapon does he want to discard
     * @param connectionID it's the ID of the user
     * @param args it's the map containing the arguments needed to perform the action
     * @param gameID it's the ID of the game
     */
    private void grabDiscardWeapon(int connectionID, Map<String, String> args, UUID gameID) {
        this.server.weaponToDiscard(connectionID, gameID, args.get(Powerup.powerup_key));
        this.send(CommunicationMessage.from(connectionID, GRAB_SUCCESS));
    }

    /**
     * This method is called when the player has decided which powerup does he want to discard
     * @param connectionID it's the ID of the user
     * @param args it's the map containing the arguments needed to perform the action
     * @param gameID it's the ID of the game
     */
    private void grabDiscardPowerup(int connectionID, Map<String, String> args, UUID gameID) {
        this.server.powerupToDiscard(connectionID, gameID, args.get(Powerup.powerup_key));
        this.send(CommunicationMessage.from(connectionID, GRAB_SUCCESS));
    }

    /**
     * This method is called when the player has decided what does he want to grab
     * @param connectionID it's the ID of the user
     * @param args it's the map containing the arguments needed to perform the action
     * @param gameID it's the ID of the game
     */
    private void choseWhatToGrab(int connectionID, Map<String, String> args, UUID gameID) {
        Map<String, String> responseArgs = this.server.didChooseWhatToGrab(args.get(AmmoTile.ammoTile_key), connectionID, gameID);
        if(responseArgs.containsKey(Powerup.powerup_key)) this.send(CommunicationMessage.from(connectionID, GRAB_FAILURE_POWERUP, responseArgs, clientOperationTimeoutInSeconds));
        else if(responseArgs.containsKey(Weapon.weapon_key)) this.send(CommunicationMessage.from(connectionID, GRAB_FAILURE_WEAPON, responseArgs, clientOperationTimeoutInSeconds));
        else this.send(CommunicationMessage.from(connectionID, GRAB_SUCCESS));
    }

    /**
     * This method is called when the player has decided that he wants to grab something
     * @param connectionID it's the ID of the user
     * @param gameID it's the ID of the game
     * @param args it's the map containing the arguments needed to perform the action
     */
    private void grabSomething(int connectionID, UUID gameID, Map<String, String> args) {
        List<String> possiblePicks = server.askPicks(connectionID, gameID, new ArrayList<>(args.values()));
        if(possiblePicks.isEmpty()) this.send(CommunicationMessage.from(connectionID, GRAB_FAILURE));
        else {
            Map<String, String> responseArgs = new HashMap<>();
            for(String pick : possiblePicks) responseArgs.put(Integer.toString(possiblePicks.indexOf(pick)), pick);
            this.send(CommunicationMessage.from(connectionID, POSSIBLE_PICKS, responseArgs, gameID, clientOperationTimeoutInSeconds));
        }
    }

    /**
     * Called when the player has decided where to spawn.
     * @param connectionID it's the ID of the user
     * @param args it's the map containing the arguments needed to perform the action
     * @param gameID it's the ID of the game
     */
    private void spawnPointChosen(int connectionID, Map<String, String> args, UUID gameID) {
        this.server.choseSpawnPoint(connectionID, gameID, args.get(Powerup.spawnPowerup_key), args.get(Powerup.powerup_key));
        this.send(CommunicationMessage.from(connectionID, KEEP_ACTION));
    }

    /**
     * Called when the player has to spawn, draws two powerups and sends them to the client
     * @param connectionID it's the ID of the user who has to spawn
     * @param gameID it's the ID of the game
     */
    private void spawnPointGenerator(int connectionID, UUID gameID) {
        List<String> spawnPowerups = this.server.getSpawnPowerups(connectionID, gameID);
        Map<String, String> responseArgs = new HashMap<>();
        for(int i = 0; i < spawnPowerups.size(); i++) responseArgs.put(Integer.toString(i), spawnPowerups.get(i));
        this.send(CommunicationMessage.from(connectionID, DRAWN_SPAWN_POINT, responseArgs, gameID, clientOperationTimeoutInSeconds));
    }

    /**
     * Called when the map to use has been chosen
     * @param connectionID it's the ID of the user who has chosen the map
     * @param args it's the map containing the arguments of the message received
     * @param gameID it's the ID of the game
     */
    private void mapChosen(int connectionID, Map<String, String> args, UUID gameID) {
        this.server.didChooseGameMap(connectionID, gameID, args.get(GameMap.gameMap_key));
        this.send(CommunicationMessage.from(connectionID, CHOOSE_SKULLS));
    }

    /**
     * Called when the client asks for available moves, it sends a list of available moves to the client
     * @param connectionID connectionID
     * @param gameID gameID
     */
    private void chooseMovement(int connectionID, UUID gameID) {
        this.send(CommunicationMessage.from(
                connectionID,
                CHOOSE_MOVEMENT,
                argsFrom(GameLogic.available_moves, String.join(listDelimiter, server.getAvailableMoves(connectionID, gameID))),
                gameID,
                clientOperationTimeoutInSeconds));
    }

    private void askPowerupToReload(int connectionID, UUID gameID) {
        List<String> powerups = server.getPowerupsInHand(connectionID, gameID);
        Map<String, String> responseArgs = new HashMap<>();
        for(String powerup : powerups) responseArgs.put(Integer.toString(powerups.indexOf(powerup)), powerup);
        send(CommunicationMessage.from(connectionID, POWERUP_TO_RELOAD, responseArgs, clientOperationTimeoutInSeconds));
    }

    private void askPowerupDamages(int connectionID, Map<String, String> args, UUID gameID) {
        List<String> possibleDamages = server.getPowerupDamages(connectionID, gameID, args.get(Powerup.powerup_key));
        Map<String, String> responseArgs = new HashMap<>();
        responseArgs.put(Powerup.powerup_key, args.get(Powerup.powerup_key));
        for(String damage : possibleDamages) responseArgs.put(Integer.toString(possibleDamages.indexOf(damage)), damage);
        send(CommunicationMessage.from(connectionID, POWERUP_DAMAGES_LIST, responseArgs, clientOperationTimeoutInSeconds));
    }

    private void askPowerups(int connectionID, UUID gameID) {
        List<String> usablePowerups = server.getUsablePowerups(connectionID, gameID);
        if(usablePowerups.isEmpty()) {
            send(CommunicationMessage.from(connectionID, NO_TURN_POWERUP, clientOperationTimeoutInSeconds));
            return;
        }
        Map<String, String> responseArgs = new HashMap<>();
        for(String powerup: usablePowerups) responseArgs.put(Integer.toString(usablePowerups.indexOf(powerup)), powerup);
        send(CommunicationMessage.from(connectionID, POWERUP_LIST, responseArgs, clientOperationTimeoutInSeconds));
    }

    private void askUnloadedWeapons(int connectionID, UUID gameID) {
        List<String> weaponsInHand = server.getUnloadedWeaponInHand(connectionID, gameID);
        if(weaponsInHand.isEmpty()) {
            send(CommunicationMessage.from(connectionID, NO_WEAPON_UNLOADED_IN_HAND));
            return;
        }
        Map<String, String> responseArgs = new HashMap<>();
        for(String weapon : weaponsInHand) responseArgs.put(Integer.toString(weaponsInHand.indexOf(weapon)), weapon);
        send(CommunicationMessage.from(connectionID, WEAPON_LIST, responseArgs, clientOperationTimeoutInSeconds));
    }

    private void weaponToReload(int connectionID, Map<String, String> args, UUID gameID) {
        List<String> weaponsToReload = new ArrayList<>(args.values());
        boolean reloaded = server.reload(weaponsToReload, connectionID, gameID);
        if(reloaded) send(CommunicationMessage.from(connectionID, RELOAD_WEAPON_OK, clientOperationTimeoutInSeconds));
        else send(CommunicationMessage.from(connectionID, RELOAD_WEAPON_FAILED, clientOperationTimeoutInSeconds));
    }

    private void effectToUse(int connectionID, Map<String, String> args, UUID gameID) {
        String forPotentiableWeapon = null;
        if(args.containsKey(PotentiableWeapon.forPotentiableWeapon_key)) forPotentiableWeapon = args.get(PotentiableWeapon.forPotentiableWeapon_key);
        String effectSelected = args.get(Effect.effect_key);
        String weaponSelected = args.get(Weapon.weapon_key);
        Map<String, String> responseArgs = server.useEffect(connectionID, gameID, forPotentiableWeapon, effectSelected, weaponSelected);
        String responseMessage = CommunicationMessage.from(connectionID, DAMAGE_LIST, responseArgs, gameID, clientOperationTimeoutInSeconds);
        send(responseMessage);
    }

    private void damageToDo(CommunicationMessage communicationMessage, int connectionID, Map<String, String> args, UUID gameID) {
        String damage = args.get(Damage.damage_key);
        String weapon = args.get(Weapon.weapon_key);
        String potentiableBoolean = args.get(PotentiableWeapon.forPotentiableWeapon_key);
        String effectIndex = args.get(Effect.effect_key);
        List<String> pows = server.makeDamage(connectionID, potentiableBoolean, effectIndex, gameID, damage, weapon);
        if(communicationMessage == LAST_DAMAGE) {
            Map<String, String> responseArgs = new HashMap<>();
            for(String pow : pows)
                responseArgs.put(Integer.toString(pows.indexOf(pow)), pow);
            send(CommunicationMessage.from(connectionID, LAST_DAMAGE_DONE, responseArgs, clientOperationTimeoutInSeconds));
        } else send(CommunicationMessage.from(connectionID, DAMAGE_DONE));
    }

    private void powerupSellingDecided(int connectionID, Map<String, String> args, UUID gameID) {
        String weapon = args.get(Weapon.weapon_key);
        args.remove(Weapon.weapon_key);
        List<String> powerups = new ArrayList<>(args.values());
        Map<String, String> responseArgs = server.useWeaponAfterPowerupSelling(connectionID, gameID, weapon, powerups);
        CommunicationMessage format = CommunicationMessage.valueOf(responseArgs.get(communication_message_key));
        responseArgs.remove(communication_message_key);

        send(CommunicationMessage.from(connectionID, format, responseArgs, gameID, clientOperationTimeoutInSeconds));
    }

    private void weaponToUse(int connectionID, Map<String, String> args, UUID gameID) {
        String weaponSelected = args.get(Weapon.weapon_key);
        Map<String, String> responseArgs = server.useWeapon(connectionID, gameID, weaponSelected);
        CommunicationMessage format = CommunicationMessage.valueOf(responseArgs.get(CommunicationMessage.communication_message_key));
        responseArgs.remove(CommunicationMessage.communication_message_key);
        String responseMessage = CommunicationMessage.from(connectionID, format, responseArgs, gameID, clientOperationTimeoutInSeconds);
        send(responseMessage);
    }

    private void shootPeople(int connectionID, UUID gameID) {
        List<String> weapons = this.server.askWeapons(connectionID, gameID);
        Map<String, String> responseArgs = new HashMap<>();
        if(weapons.isEmpty()) {
            send(CommunicationMessage.from(connectionID, SHOOT_PEOPLE_FAILURE, clientOperationTimeoutInSeconds));
            return;
        }
        for(String weapon : weapons)
            responseArgs.put(Integer.toString(weapons.indexOf(weapon)), weapon);
        send(CommunicationMessage.from(connectionID, SHOOT_PEOPLE, responseArgs, gameID, clientOperationTimeoutInSeconds));
    }

    /**
     * Method called by the client to create a new user
     * @param connectionID message connection ID
     * @param args message arguments
     */
    private void createUser(int connectionID, Map<String, String> args) {
        var username = args.get(User.username_key);
        var userID   = server.createUser(username, this);
        String responseMessage;

        if (userID != -1) {
            responseMessage = CommunicationMessage.from(userID, CREATE_USER_OK, args);
        } else {
            responseMessage = CommunicationMessage.from(connectionID, CREATE_USER_FAILED, args);
        }
        send(responseMessage);
    }

    /**
     * Method called by the client to join a game or waiting room
     * @param connectionID message connection ID
     * @param args message arguments
     */
    private void joinWaitingRoom(int connectionID, Map<String, String> args) {
        var username = args.get(User.username_key);
        server.joinWaitingRoom(username);
        var responseMessage = CommunicationMessage.from(connectionID, USER_JOINED_WAITING_ROOM, args);
        send(responseMessage);
    }

    @Override
    protected void gameDidStart(String gameID) {
        var args = new HashMap<String, String>();
        args.put(GameLogic.gameID_key, gameID);
        send(CommunicationMessage.from(0, USER_JOINED_GAME, args));
    }

    @Override
    protected void willChooseCharacter(List<String> availableCharacters) {
        var args = new HashMap<String, String>();
        args.put(Character.character_list, String.join(listDelimiter, availableCharacters));
        send(CommunicationMessage.from(0, CHOOSE_CHARACTER, args));
    }

    @Override
    protected void didChooseCharacter(UUID gameID, int userID, String chosenCharacterColor) {
        var args = new HashMap<String, String>();
        args.put(Character.character, chosenCharacterColor);
        if (server.choseCharacter(gameID, userID, chosenCharacterColor)) send(CommunicationMessage.from(userID, CHARACTER_CHOSEN_OK, args, gameID));
        else {
            send(CommunicationMessage.from(userID, CHARACTER_NOT_AVAILABLE, args, gameID));
            willChooseCharacter(server.getAvailableCharacters(gameID));
        }
    }

    @Override
    protected void willChooseGameMap(UUID gameID) {
        this.send(CommunicationMessage.from(0, CHOOSE_MAP));
    }

    @Override
    protected void startNewTurn(int userID) {
        this.send(CommunicationMessage.from(userID, KEEP_ACTION));
    }

    @Override
    protected void startNewTurnFromRespawn(int userID) {
        this.send(CommunicationMessage.from(userID, CHOOSE_SPAWN_POINT, clientOperationTimeoutInSeconds));
    }

    @Override
    protected void displayChanges(int userID, String newBoardSituation) {
        Map<String, String> args = new HashMap<>();
        args.put(GameMap.gameMap_key, newBoardSituation);
        this.send(CommunicationMessage.from(userID, UPDATE_SITUATION, args));
    }

    @Override
    protected void spawnAfterDeath(int userID, List<String> powerupsToSpawn) {
        Map<String, String> args = new HashMap<>();
        for(String powerup : powerupsToSpawn) args.put(Integer.toString(powerupsToSpawn.indexOf(powerup)), powerup);
        this.send(CommunicationMessage.from(userID, SPAWN_AFTER_DEATH, args));
    }

    @Override
    protected void displayFinalFrenzy(int userID) {
        this.send(CommunicationMessage.from(userID, DISPLAY_FINAL_FRENZY));
    }

    @Override
    protected void endOfTheGame(int userID, String scoreboard) {
        this.send(CommunicationMessage.from(userID, GAME_ENDED, argsFrom(GameMap.gameMap_key, scoreboard)));
    }

    @Override
    protected void updateView(int userID, Map<String, List<String>> updates) {
        Map<String, String> args = new HashMap<>();
        List<String> keys = new ArrayList<>(updates.keySet());
        List<String> values = new ArrayList<>(updates.get(keys.get(0)));
        if(!values.isEmpty()) args.put(keys.get(0), values.get(0));
        else args.put(keys.get(0), EMPTY);
        for(int i = 1; i < values.size(); i++) args.put(keys.get(0), values.get(i));
        this.send(CommunicationMessage.from(userID, UPDATE_ALL, args));
    }

    @Override
    protected void useVenom(int userID, List<String> availablePowerups, String username) {
        Map<String, String> args = new HashMap<>();
        for(String string : availablePowerups)
            args.put(Integer.toString(availablePowerups.indexOf(string)), string);
        args.put(Player.playerKey_player, username);
        this.send(CommunicationMessage.from(userID, WILL_CHOOSE_TAGBACK, args, clientOperationTimeoutInSeconds));
    }
}