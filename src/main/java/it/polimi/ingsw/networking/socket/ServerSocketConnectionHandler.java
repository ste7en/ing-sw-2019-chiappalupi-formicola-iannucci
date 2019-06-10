package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.networking.Server;
import it.polimi.ingsw.networking.ServerConnectionHandler;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionState;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.networking.utility.Pingable;
import it.polimi.ingsw.utility.*;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static it.polimi.ingsw.networking.utility.ConnectionState.*;
import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * This class is responsible for handling a single client socket connection,
 * it implements {@link Runnable} interface and runs into a single thread.
 *
 * @author Stefano Formicola
 */
public class ServerSocketConnectionHandler extends ServerConnectionHandler implements Runnable, Pingable {

    /**
     * The socket instance, the object that represents a connection
     * between client and server.
     */
    private Socket socket;

    /**
     * A buffered outbox for messages
     */
    private ConcurrentLinkedQueue<String> outBuf;

    /**
     * Log strings
     */
    @SuppressWarnings("squid:S3008")
    private static final String IO_EXC = "An IOException has occurred during a socket operation";
    private static final String STREAM_SUCC = "Socket Input/Output streams successfully created.";
    private static final String CONN_CLOSED = "Connection closed with the client :: ";
    private static final String PING_TIMEOUT = "Ping timeout. Closing connection socket :: ";
    private static final String INTERRUPTED_EXCEPTION = "Thread interrupted exception.";

    /**
     * The state of the connection
     */
    private ConnectionState connectionState;

    /**
     * Basic class constructor
     * @param socket socket connection with the client
     * @param server the server instance
     */
    ServerSocketConnectionHandler(Socket socket, Server server) {
        this.socket = socket;
        this.outBuf = new ConcurrentLinkedQueue<>();
        this.connectionState = ONLINE;
        super.server = server;
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
    public void send(String message) {
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
        new Thread( () -> {
            var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
            var connectionID         = CommunicationMessage.getConnectionIDFrom(message);
            var args                 = CommunicationMessage.getMessageArgsFrom(message);
            var gameID               = CommunicationMessage.getMessageGameIDFrom(message);

            switch (communicationMessage) {
                case PONG:
                    Ping.getInstance().didPong(connectionID);
                    break;
                case CREATE_USER:
                    createUser(connectionID, args);
                    break;
                case USER_JOIN_WAITING_ROOM:
                    joinWaitingRoom(connectionID, args);
                    break;
                case GET_AVAILABLE_CHARACTERS:
                    getAvailableCharacters(gameID);
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
                default:
                    break;
            }

        }).start();
    }

    private void askPowerupToReload(int connectionID, UUID gameID) {
        List<String> powerups = server.getPowerupsInHand(connectionID, gameID);
        Map<String, String> responseArgs = new HashMap<>();
        for(String powerup : powerups) responseArgs.put(Integer.toString(powerups.indexOf(powerup)), powerup);
        send(CommunicationMessage.from(connectionID, POWERUP_TO_RELOAD, responseArgs));
    }

    private void askPowerupDamages(int connectionID, Map<String, String> args, UUID gameID) {
        List<String> possibleDamages = server.getPowerupDamages(connectionID, gameID, args.get(Powerup.powerup_key));
        Map<String, String> responseArgs = new HashMap<>();
        responseArgs.put(Powerup.powerup_key, args.get(Powerup.powerup_key));
        for(String damage : possibleDamages) responseArgs.put(Integer.toString(possibleDamages.indexOf(damage)), damage);
        send(CommunicationMessage.from(connectionID, POWERUP_DAMAGES_LIST, responseArgs));
    }

    private void askPowerups(int connectionID, UUID gameID) {
        List<String> usablePowerups = server.getUsablePowerups(connectionID, gameID);
        if(usablePowerups.isEmpty()) {
            send(CommunicationMessage.from(connectionID, NO_TURN_POWERUP));
            return;
        }
        Map<String, String> responseArgs = new HashMap<>();
        for(String powerup: usablePowerups) responseArgs.put(Integer.toString(usablePowerups.indexOf(powerup)), powerup);
        send(CommunicationMessage.from(connectionID, POWERUP_LIST, responseArgs));
    }

    private void askUnloadedWeapons(int connectionID, UUID gameID) {
        List<String> weaponsInHand = server.getUnloadedWeaponInHand(connectionID, gameID);
        if(weaponsInHand.isEmpty()) {
            send(CommunicationMessage.from(connectionID, NO_WEAPON_UNLOADED_IN_HAND));
            return;
        }
        Map<String, String> responseArgs = new HashMap<>();
        for(String weapon : weaponsInHand) responseArgs.put(Integer.toString(weaponsInHand.indexOf(weapon)), weapon);
        send(CommunicationMessage.from(connectionID, WEAPON_LIST, responseArgs));
    }

    private void weaponToReload(int connectionID, Map<String, String> args, UUID gameID) {
        List<String> weaponsToReload = new ArrayList<>(args.values());
        boolean reloaded = server.reload(weaponsToReload, connectionID, gameID);
        if(reloaded) send(CommunicationMessage.from(connectionID, RELOAD_WEAPON_OK));
        else send(CommunicationMessage.from(connectionID, RELOAD_WEAPON_FAILED));
    }

    private void effectToUse(int connectionID, Map<String, String> args, UUID gameID) {
        String forPotentiableWeapon = null;
        if(args.containsKey(PotentiableWeapon.forPotentiableWeapon_key)) forPotentiableWeapon = args.get(PotentiableWeapon.forPotentiableWeapon_key);
        String effectSelected = args.get(Effect.effect_key);
        String weaponSelected = args.get(Weapon.weapon_key);
        Map<String, String> responseArgs = server.useEffect(connectionID, gameID, forPotentiableWeapon, effectSelected, weaponSelected);
        String responseMessage = CommunicationMessage.from(connectionID, DAMAGE_LIST, responseArgs, gameID);
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
            send(CommunicationMessage.from(connectionID, LAST_DAMAGE_DONE, responseArgs));
        }
    }

    private void powerupSellingDecided(int connectionID, Map<String, String> args, UUID gameID) {
        String weapon = args.get(Weapon.weapon_key);
        args.remove(Weapon.weapon_key);
        List<String> powerups = new ArrayList<>(args.values());
        Map<String, String> responseArgs = server.useWeaponAfterPowerupSelling(connectionID, gameID, weapon, powerups);
        CommunicationMessage format = CommunicationMessage.valueOf(responseArgs.get(communication_message_key));
        responseArgs.remove(communication_message_key);
        send(CommunicationMessage.from(connectionID, format, responseArgs, gameID));
    }

    private void weaponToUse(int connectionID, Map<String, String> args, UUID gameID) {
        String weaponSelected = args.get(Weapon.weapon_key);
        Map<String, String> responseArgs = server.useWeapon(connectionID, gameID, weaponSelected);
        CommunicationMessage format = CommunicationMessage.valueOf(responseArgs.get(CommunicationMessage.communication_message_key));
        responseArgs.remove(CommunicationMessage.communication_message_key);
        String responseMessage = CommunicationMessage.from(connectionID, format, responseArgs, gameID);
        send(responseMessage);
    }

    private void shootPeople(int connectionID, UUID gameID) {
        List<String> weapons = this.server.askWeapons(connectionID, gameID);
        Map<String, String> responseArgs = new HashMap<>();
        if(weapons.isEmpty()) {
            send(CommunicationMessage.from(connectionID, SHOOT_PEOPLE_FAILURE));
            return;
        }
        for(String weapon : weapons)
            responseArgs.put(Integer.toString(weapons.indexOf(weapon)), weapon);
        send(CommunicationMessage.from(connectionID, SHOOT_PEOPLE, responseArgs, gameID));
    }

    /**
     * Method called by the client to create a new user
     * @param connectionID message connection ID
     * @param args message arguments
     */
    private void createUser(int connectionID, Map<String, String> args) {
        var username = args.get(User.username_key);
        String responseMessage;

        if (server.createUser(username, this)) {
            responseMessage = CommunicationMessage.from(connectionID, CREATE_USER_OK, args);
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

    private void getAvailableCharacters(UUID gameID) {
        server.getAvailableCharacters(gameID);
    }
}