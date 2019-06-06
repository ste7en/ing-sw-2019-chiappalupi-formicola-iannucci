package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.PotentiableWeapon;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.ConnectionHandlerReceiverDelegate;
import it.polimi.ingsw.networking.ConnectionHandlerSenderDelegate;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.utility.Loggable;

import java.io.*;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;

/**
 * Main class of the client. It will set up the networking and send/receive events and requests.
 *
 * @author Stefano Formicola
 */
public class ClientSocket extends Client implements Loggable, ConnectionHandlerReceiverDelegate {

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

    @Override
    public void login(String username) {
        var args = new HashMap<String, String>();
        args.put(User.username_key, username);
        this.send(CommunicationMessage.from(0, CREATE_USER, args));
    }

    /**
     * Receives a json message from a delegator
     * and parses its content
     * @param message json message
     * @param sender a the connection handler delegated to send messages
     */
    @Override
    public void receive(String message, ConnectionHandlerSenderDelegate sender) {
        new Thread(
            () -> {
                var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
                var id = CommunicationMessage.getConnectionIDFrom(message);
                Map<String, String> args = CommunicationMessage.getMessageArgsFrom(message);

                switch (communicationMessage) {
                    case PING:
                        sender.send(CommunicationMessage.from(id, PONG));
                        break;
                    case CREATE_USER_OK:
                        this.viewObserver.onLoginSuccess(args.get(User.username_key));
                        break;
                    case CREATE_USER_FAILED:
                        this.viewObserver.onLoginFailure();
                        break;
                    case USER_JOINED_WAITING_ROOM:
                        this.viewObserver.didJoinWaitingRoom();
                        break;
                    case USER_JOINED_GAME:
                        var gameUUID = UUID.fromString(args.get(GameLogic.gameID_key));
                        this.viewObserver.onStart(gameUUID);
                        break;
                    case DAMAGE_LIST:
                        this.viewObserver.willChooseDamage(args);
                        break;
                    case MODES_LIST:
                        this.viewObserver.willChooseMode(args);
                        break;
                    case EFFECT_TO_USE:
                        this.viewObserver.willChooseEffects(args);
                        break;
                    default:
                        break;
                }
            }
        ).start();
    }

    /**
     * Sends a json message through its sender delegate
     * @param message a json message to send
     */
    public void send(String message) {
        senderDelegate.send(message);
    }

    @Override
    public void useWeapon(String weaponSelected) {
        Map<String, String> args = new HashMap<>();
        args.put(Weapon.weapon_key, weaponSelected);
        this.send(CommunicationMessage.from(userID, WEAPON_TO_USE, args, gameID));
    }

    @Override
    public void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        Map<String, String> damageToDo = new HashMap<>();
        damageToDo.put(Damage.damage_key, damage);
        damageToDo.put(Weapon.weapon_key, weapon);
        damageToDo.put(Effect.effect_key, indexOfEffect);
        if(forPotentiableWeapon != null) damageToDo.put(PotentiableWeapon.forPotentiableWeapon_key, indexOfEffect);
        this.send(CommunicationMessage.from(userID, DAMAGE_TO_MAKE, damageToDo, gameID));
    }

    @Override
    public void useMode(String weapon, String effect) {
        Map<String, String> args = new HashMap<>();
        args.put(Weapon.weapon_key, weapon);
        args.put(Effect.effect_key, effect);
        this.send(CommunicationMessage.from(userID, EFFECT_TO_USE, args, gameID));
    }

}