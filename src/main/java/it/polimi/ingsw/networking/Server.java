package it.polimi.ingsw.networking;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.rmi.ClientRMI;
import it.polimi.ingsw.networking.rmi.RMIInterface;
import it.polimi.ingsw.networking.socket.*;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.utility.Loggable;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * Main class of the game server. It will set up the networking and create controllers and games.
 * It is designed to support multiple games and players in a waiting room before starting a game,
 * plus it manages loss of connections and users re-connections.
 *
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 * @author Elena Iannucci
 */
@SuppressWarnings("all")
public class Server implements Loggable, ConnectionHandlerReceiverDelegate, WaitingRoomObserver, RMIInterface {




    private Registry registry;
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
    private ConcurrentMap<UUID, GameLogic> gameControllers;
    /**
     * Collection of the connected users, useful to implement unicast
     * or to check username availability, but also users availability
     */
    private ConcurrentMap<User, ServerConnectionHandler> users;

    /**
     * Log strings
     */
    private static final String EXC_SETUP = "Error while setting up a ServerSocketHandler :: ";

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
        this.portNumberRMI    = portNumberRMI;
        this.senderDelegate   = new LinkedList<>();
        this.users            = new ConcurrentHashMap<>();
        this.gameControllers  = new ConcurrentHashMap<>();

        // TODO: - The following is a test with test parameters, the real waiting room settings must be read from a file
        this.waitingRoom = new WaitingRoom(3, 5, 30, this);

        setupConnections();
    }

    /**
     * Helper method used to setup server connections (Socket and RMI)
     */
    private void setupConnections() {
        ServerSocketHandler socketConnectionHandler;
        try {
            socketConnectionHandler = new ServerSocketHandler(portNumberSocket);
//            senderDelegate.add(socketConnectionHandler);
            var socketConnectionHandlerThread = new Thread(socketConnectionHandler);
            socketConnectionHandlerThread.setPriority(Thread.MIN_PRIORITY);
            socketConnectionHandlerThread.start();
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }
        try {
            launch();
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
        gameControllers.put(gameID, gameLogic);
        userList.forEach(user -> {
            var connection = users.get(user);
            connection.gameDidStart(gameID.toString());
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
                            Player shooter = gameControllers.get(gameID).getPlayer(playerColor);
                            ArrayList<ArrayList<Damage>> possibleDamages = gameControllers.get(gameID).useEffect(weapon, weapon.getEffects().get(0), shooter, null);
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
                    Player shooter = gameControllers.get(gameID).getPlayer(playerColor);
                    int indexOfEffect = Integer.parseInt(args.get(Effect.effect_key));
                    boolean potentiable = false;
                    if (weaponToUse.communicationMessageGenerator() == EFFECTS_LIST) potentiable = true;
                    ArrayList<ArrayList<Damage>> possibleDamages;
                    boolean toApply = true;
                    if (!potentiable)
                        possibleDamages = gameControllers.get(gameID).useEffect(weaponToUse, weaponToUse.getEffects().get(indexOfEffect), shooter, null);
                    else {
                        toApply = false;
                        Effect effect = weaponToUse.getEffects().get(indexOfEffect);
                        ArrayList<Damage> forPotentiableWeapon = gameControllers.get(gameID).getForPotentiableWeapon();
                        if (forPotentiableWeapon.isEmpty() || effect.getProperties().containsKey(EffectProperty.MoveMe))
                            forPotentiableWeapon = null;
                        if (effect.getProperties().containsKey(EffectProperty.MoveMe)) toApply = true;
                        possibleDamages = gameControllers.get(gameID).useEffect(weaponToUse, effect, shooter, forPotentiableWeapon);
                    }
                    ArrayList<Damage> damageToMake = new ArrayList<>();
                    for (ArrayList<Damage> damages : possibleDamages)
                        if (damages.toString().equals(damage)) damageToMake = damages;
                    if (damageToMake.isEmpty()) throw new IllegalArgumentException("This damage doesn't exist!");
                    if (toApply) {
                        for (Damage d : damageToMake)
                            gameControllers.get(gameID).applyDamage(d, playerColor);
                    } else {
                        boolean applyPotentiableDamage = Boolean.parseBoolean(args.get(PotentiableWeapon.forPotentiableWeapon_key));
                        if (applyPotentiableDamage) {
                            damageToMake.addAll(gameControllers.get(gameID).getForPotentiableWeapon());
                            for (Damage d : damageToMake)
                                gameControllers.get(gameID).applyDamage(d, playerColor);
                            gameControllers.get(gameID).wipePotentiableWeapon();
                        } else gameControllers.get(gameID).appendPotentiableWeapon(damageToMake);
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
                    Player shooter = gameControllers.get(gameID).getPlayer(playerColor);
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
                    ArrayList<Damage> forPotentiableWeapon = gameControllers.get(gameID).getForPotentiableWeapon();
                    if (forPotentiableWeapon.isEmpty()) forPotentiableWeapon = null;
                    ArrayList<ArrayList<Damage>> possibleDamages = gameControllers.get(gameID).useEffect(weapon, effect, shooter, forPotentiableWeapon);
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

    /**
     * When a client registers a new user
     * @param user the user to register
     * @param connectionHandler client connection handler
     * @return false if the user already exists and is connected, true otherwise
     */
    public boolean createUser(User user, ServerConnectionHandler connectionHandler) {
        if (checkUserAvailability(user)) {
            users.put(user, connectionHandler);
            return true;
        } else return false;
    }

    /**
     * When a client decides to join a game
     * @param user the user who will play the game
     */
    public void userLogin(User user) {
        waitingRoom.addUser(user);
    }

    private Weapon lookForWeapon(String weapon) {
        DecksHandler deck = new DecksHandler();
        Weapon weaponToUse = deck.drawWeapon();
        while(!weaponToUse.getName().equals(weapon)) weaponToUse = deck.drawWeapon();
        return weaponToUse;
    }



    public void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(portNumberRMI);
        registry.rebind("rmiInterface", this);
        UnicastRemoteObject.exportObject(this, 0);
        Logger.getGlobal().info("rmi Server running correctly...");
    }



    @Override
    public void newUser(String username) throws RemoteException {
        if (checkUsernameAvailability(username)==true) System.out.print(username + ", you are logged in");
    }

    @Override
    public void registerClient(ClientRMI clientRMI){
    }

    public boolean checkUsernameAvailability(String username) {
        return true;
    }

    @Override
    public void joinWaitingRoom() throws RemoteException {

    }

    @Override
    public ArrayList<Character> getAvailableCharacters() throws RemoteException{
        return null;
    }

    @Override
    public void chooseCharacter(Character character) throws RemoteException{

    }

    @Override
    public void chooseGameSettings() throws RemoteException{

    }

    @Override
    public ArrayList<AmmoColor> displaySpawnPoints() throws RemoteException{
        return null;
    }

    @Override
    public void chooseSpawnPoint() throws RemoteException{

    }

    @Override
    public void chooseMove() throws RemoteException{

    }

    @Override
    public void chooseMovement() throws RemoteException{

    }

    @Override
    public void chooseWhatToGrab() throws RemoteException{

    }

    @Override
    public ArrayList<String> getAvailableWeapons() throws RemoteException{
        return null;
    }

    @Override
    public void chooseWeapon(String weaponSelected) throws RemoteException{

    }

    @Override
    public Map<String, String> getAvailableDamages() throws RemoteException{
        return null;
    }

    @Override
    public void chooseDamage(Map<String, String> damageToDo) throws RemoteException{

    }

    @Override
    public Map<String, String> getAvailableModes() throws RemoteException{
        return null;
    }

    @Override
    public void chooseMode(Map<String, String> modalityChosen) throws RemoteException{

    }

    @Override
    public void getAvailableEffect() throws RemoteException{

    }

    @Override
    public void chooseEffects() throws RemoteException{

    }

    @Override
    public void getAvailablePowerups() throws RemoteException{

    }

    @Override
    public void getAvailablePowerupsEffects() throws RemoteException{

    }

    @Override
    public void choosePowerup() throws RemoteException{

    }

    @Override
    public void choosePowerupEffects() throws RemoteException{

    }

    @Override
    public ArrayList<String> canReload() throws RemoteException{
        return null;
    }

    @Override
    public void reload(String weaponSelected) throws RemoteException{

    }


}
