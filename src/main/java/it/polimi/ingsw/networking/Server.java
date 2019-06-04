package it.polimi.ingsw.networking;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.socket.*;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.utility.Loggable;
import it.polimi.ingsw.networking.utility.Ping;

import java.util.*;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * Main class of the game server. It will set up the networking and create controllers and games.
 * It is designed to support multiple games and players in a waiting room before starting a game,
 * plus it manages loss of connections and users re-connections.
 *
 * @author Stefano Formicola feat. Elena Iannucci
 */
public class Server implements Loggable, ConnectionHandlerReceiverDelegate, WaitingRoomObserver {
    /**
     * The port on which the server socket is listening
     */
    private Integer portNumberSocket;
    /**
     * The port on which the server rmi is listening
     */
    private Integer portNumberRMI;
    /**
     * The delegates to send messages via socket/rmi
     */
    private List<ConnectionHandlerSenderDelegate> senderDelegate;
    /**
     * The game waiting room, when new users log in
     */
    private WaitingRoom waitingRoom;
    /**
     * Instances of the game that are running, connected with their unique identifier.
     */
    private HashMap<UUID, GameLogic> gamesControllers;
    /**
     * Collection of the connected users, useful to implement unicast
     * or to check username availability, but also users availability
     */
    private Map<User, ConnectionHandlerSenderDelegate> users;

    /**
     * Log strings
     */
    private static final String EXC_SETUP = "Error while setting up a ServerSocketConnectionHandler :: ";

    /**
     * Entry point of the server application
     * @param args arguments
     */
    public static void main(String[] args) {
        AdrenalineLogger.setLogName("Server");
        AdrenalineLogger.LOG_TYPE = "SERVER";
        new Server(3334, 4444);

    }

    /**
     * Server constructor responsible for setting up networking parameters and creates
     * the game and its controller.
     */
    private Server(Integer portNumberSocket, Integer portNumberRMI) {
        this.portNumberSocket = portNumberSocket;
        this.portNumberRMI = portNumberRMI;
        this.senderDelegate = new LinkedList<>();
        this.users = new HashMap<>();

        // TODO: - The following is a test with test parameters, the real waiting room settings must be read from a file
        this.waitingRoom = new WaitingRoom(3, 5, 30, this);

        setupConnections();
    }

    /**
     * Helper method used to setup server connections (Socket and RMI)
     */
    private void setupConnections() {
        ServerSocketConnectionHandler socketConnectionHandler;
        //ServerRMIConnectionHandler RMIConnectionHandler;
        try {
            socketConnectionHandler = new ServerSocketConnectionHandler(portNumberSocket, this);
            senderDelegate.add(socketConnectionHandler);
            var socketConnectionHandlerThread = new Thread(socketConnectionHandler);
            socketConnectionHandlerThread.setPriority(Thread.MIN_PRIORITY);
            socketConnectionHandlerThread.start();
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }
        try {
            //RMIConnectionHandler = new ServerRMIConnectionHandler(portNumberRMI);
            //RMIConnectionHandler.launch();
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }

    }

    /**
     * Method called by a WaitingRoom instance on the implementing server
     * to start a new game when the minimum number of logged users has
     * reached and after a timeout expiration.
     *
     * @param userList a collection of the logged users ready to start a game
     */
    @Override
    public void startNewGame(List<User> userList) {
        var gameID = UUID.randomUUID();
        // TODO: - Game Logic hasn't a constructor
        var gameLogic = new GameLogic();
        gamesControllers.put(gameID, gameLogic);
        userList.forEach(user -> {
            var connection = users.get(user);
            var args = new HashMap<String, String>();
            args.put(User.username_key, user.getUsername());
            args.put(GameLogic.gameID_key, gameID.toString());
            connection.send(CommunicationMessage.from(0, USER_JOINED_GAME, args));
        });
    }

    /**
     * @param name user name
     * @return true if the user doesn't exist or isn't connected, false otherwise
     */
    @SuppressWarnings("all")
    private boolean checkUserAvailability(User user) {
        var connectionHandler = users.get(user);
        if ( connectionHandler != null ) return !connectionHandler.isConnectionAvailable();
        else return true;
    }

    /**
     * Receives a message from a delegator
     * @param message received message
     * @param sender a the connection handler delegated to send messages
     */
    @Override
    public void receive(String message, ConnectionHandlerSenderDelegate sender) {
        new Thread( () -> {
            var communicationMessage = CommunicationMessage.getCommunicationMessageFrom(message);
            var connectionID = CommunicationMessage.getConnectionIDFrom(message);
            var args = CommunicationMessage.getMessageArgsFrom(message);
            var gameID = CommunicationMessage.getMessageGameIDFrom(message);
            switch (communicationMessage) {

                case PONG:
                    Ping.getInstance().didPong(connectionID);
                    break;

                case CREATE_USER:
                    createUser(sender, connectionID, args);
                    break;

                case USER_LOGIN:
                    userLogin(sender, connectionID, args);
                    break;

                case WEAPON_TO_USE: {
                    String weaponSelected = args.get(Weapon.weapon_key);
                    PlayerColor playerColor = PlayerColor.valueOf(args.get(PlayerColor.playerColor_key));
                    Weapon weapon = lookForWeapon(weaponSelected);
                    CommunicationMessage weaponMessage = weapon.communicationMessageGenerator();
                    Map<String, String> responseArgs = new HashMap<>();
                    responseArgs.put(Weapon.weapon_key, weaponSelected);
                    String responseMessage = new String();

                    switch (weaponMessage) {
                        case DAMAGE_LIST: {
                            responseArgs.put(Effect.effect_key, "0");
                            Player shooter = gamesControllers.get(gameID).getPlayer(playerColor);
                            ArrayList<ArrayList<Damage>> possibleDamages = gamesControllers.get(gameID).useEffect(weapon, weapon.getEffects().get(0), shooter, null);
                            stringifyDamages(possibleDamages, responseArgs);
                            responseMessage = CommunicationMessage.from(connectionID, weaponMessage, responseArgs, gameID);
                            sender.send(responseMessage);
                            break;
                        }
                        case MODES_LIST: {
                            int i = 0;
                            for (Effect effect : weapon.getEffects()) {
                                responseArgs.put(Integer.toString(i), effect.getName());
                                i++;
                            }
                            responseMessage = CommunicationMessage.from(connectionID, weaponMessage, responseArgs, gameID);
                            break;
                        }
                        case EFFECTS_LIST: {
                            ArrayList<ArrayList<Integer>> effectsCombinations = weapon.effectsCombinations();
                            for (ArrayList<Integer> combination : effectsCombinations)
                                responseArgs.put(Integer.toString(effectsCombinations.indexOf(combination)), combination.toString());
                            responseMessage = CommunicationMessage.from(connectionID, weaponMessage, responseArgs, gameID);
                            break;
                        }
                        default:
                            break;
                    }
                    sender.send(responseMessage);
                    break;
                }

                case DAMAGE_TO_MAKE: {
                    String damage = args.get(Damage.damage_key);
                    String weapon = args.get(Weapon.weapon_key);
                    Weapon weaponToUse = lookForWeapon(weapon);
                    PlayerColor playerColor = PlayerColor.valueOf(args.get(PlayerColor.playerColor_key));
                    Player shooter = gamesControllers.get(gameID).getPlayer(playerColor);
                    int indexOfEffect = Integer.parseInt(args.get(Effect.effect_key));
                    boolean potentiable = false;
                    if (weaponToUse.communicationMessageGenerator() == EFFECTS_LIST) potentiable = true;
                    ArrayList<ArrayList<Damage>> possibleDamages;
                    boolean toApply = true;
                    if (!potentiable)
                        possibleDamages = gamesControllers.get(gameID).useEffect(weaponToUse, weaponToUse.getEffects().get(indexOfEffect), shooter, null);
                    else {
                        toApply = false;
                        Effect effect = weaponToUse.getEffects().get(indexOfEffect);
                        ArrayList<Damage> forPotentiableWeapon = gamesControllers.get(gameID).getForPotentiableWeapon();
                        if (forPotentiableWeapon.isEmpty() || effect.getProperties().containsKey(EffectProperty.MoveMe))
                            forPotentiableWeapon = null;
                        if (effect.getProperties().containsKey(EffectProperty.MoveMe)) toApply = true;
                        possibleDamages = gamesControllers.get(gameID).useEffect(weaponToUse, effect, shooter, forPotentiableWeapon);
                    }
                    ArrayList<Damage> damageToMake = new ArrayList<>();
                    for (ArrayList<Damage> damages : possibleDamages)
                        if (damages.toString().equals(damage)) damageToMake = damages;
                    if (damageToMake.isEmpty()) throw new IllegalArgumentException("This damage doesn't exist!");
                    if (toApply) {
                        for (Damage d : damageToMake)
                            gamesControllers.get(gameID).applyDamage(d, playerColor);
                    } else {
                        boolean applyPotentiableDamage = Boolean.parseBoolean(args.get(PotentiableWeapon.forPotentiableWeapon_key));
                        if (applyPotentiableDamage) {
                            damageToMake.addAll(gamesControllers.get(gameID).getForPotentiableWeapon());
                            for (Damage d : damageToMake)
                                gamesControllers.get(gameID).applyDamage(d, playerColor);
                            gamesControllers.get(gameID).wipePotentiableWeapon();
                        } else gamesControllers.get(gameID).appendPotentiableWeapon(damageToMake);
                    }
                    break;
                }

                case EFFECT_TO_USE: {
                    Map<String, String> responseArgs = new HashMap<>();
                    String effectSelected = args.get(Effect.effect_key);
                    String weaponSelected = args.get(Weapon.weapon_key);
                    responseArgs.put(Weapon.weapon_key, weaponSelected);
                    PlayerColor playerColor = PlayerColor.valueOf(args.get(PlayerColor.playerColor_key));
                    Weapon weapon = lookForWeapon(weaponSelected);
                    Player shooter = gamesControllers.get(gameID).getPlayer(playerColor);
                    Effect effect = null;
                    if (weapon.communicationMessageGenerator().equals(MODES_LIST)) {
                        for (Effect e : weapon.getEffects())
                            if (e.getName().equals(effectSelected)) effect = e;
                    } else {
                        String forPotentiableWeapon = args.get(PotentiableWeapon.forPotentiableWeapon_key);
                        responseArgs.put(PotentiableWeapon.forPotentiableWeapon_key, forPotentiableWeapon);
                        int index = Integer.parseInt(effectSelected);
                        effect = weapon.getEffects().get(index - 1);
                    }
                    int indexOfEffect = weapon.getEffects().indexOf(effect);
                    responseArgs.put(Effect.effect_key, Integer.toString(indexOfEffect));
                    ArrayList<Damage> forPotentiableWeapon = gamesControllers.get(gameID).getForPotentiableWeapon();
                    if (forPotentiableWeapon.isEmpty()) forPotentiableWeapon = null;
                    ArrayList<ArrayList<Damage>> possibleDamages = gamesControllers.get(gameID).useEffect(weapon, effect, shooter, forPotentiableWeapon);
                    stringifyDamages(possibleDamages, responseArgs);
                    String responseMessage = CommunicationMessage.from(connectionID, DAMAGE_LIST, responseArgs, gameID);
                    sender.send(responseMessage);
                    break;
                }

                default:
                    break;
            }
        }).start();
    }

    /**
     * Helper method to avoid code repetition.
     * @param possibleDamages it's an arrayList containing all of the possible damages.
     * @param responseArgs it's the HashMap where all the damages has to be stored.
     */
    private void stringifyDamages(ArrayList<ArrayList<Damage>> possibleDamages, Map<String, String> responseArgs) {
        for (ArrayList<Damage> damages : possibleDamages)
            responseArgs.put(Integer.toString(possibleDamages.indexOf(damages)), damages.toString());
    }

    private void createUser(ConnectionHandlerSenderDelegate sender, int connectionID, Map<String, String> args) {
        var username     = args.get(User.username_key);
        var user         = new User(username);
        var responseArgs = new HashMap<String, String>();

        responseArgs.put(User.username_key, username);
        String responseMessage;

        if (checkUserAvailability(user)) {
            users.put(user, sender);
            responseMessage = CommunicationMessage.from(connectionID, CREATE_USER_OK, responseArgs);
        } else {
            responseMessage = CommunicationMessage.from(connectionID, CREATE_USER_FAILED, responseArgs);
        }

        sender.send(responseMessage);
    }

    private void userLogin(ConnectionHandlerSenderDelegate sender, int connectionID, Map<String, String> args) {
        var username = args.get(User.username_key);
        waitingRoom.addUser(new User(username));
        var responseArgs = new HashMap<String, String>();
        responseArgs.put(User.username_key, username);
        var responseMessage = CommunicationMessage.from(connectionID, USER_JOINED_WAITING_ROOM, responseArgs);
        sender.send(responseMessage);
    }

    private Weapon lookForWeapon(String weapon) {
        DecksHandler deck = new DecksHandler();
        Weapon weaponToUse = deck.drawWeapon();
        while(!weaponToUse.getName().equals(weapon)) weaponToUse = deck.drawWeapon();
        return weaponToUse;
    }
}
