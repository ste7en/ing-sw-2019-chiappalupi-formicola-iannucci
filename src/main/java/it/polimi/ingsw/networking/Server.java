package it.polimi.ingsw.networking;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.rmi.ClientInterface;
import it.polimi.ingsw.networking.rmi.ServerInterface;
import it.polimi.ingsw.networking.rmi.ServerRMIConnectionHandler;
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
public class Server implements Loggable, WaitingRoomObserver, ServerInterface {

    private ServerRMIConnectionHandler serverRMIConnectionHandler;

    private ClientInterface clientRMI;

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
    private static final String EXC_SETUP      = "Error while setting up the server :: ";
    private static final String DID_DISCONNECT = "User disconnected: ";



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
        ServerRMIConnectionHandler RMIConnectionHandler;
        try {
            socketConnectionHandler = new ServerSocketHandler(portNumberSocket, this);
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
     * Method that export the server in order to create a RMI connection
     */
    public void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(portNumberRMI);
        registry.rebind("rmiInterface", this);
        UnicastRemoteObject.exportObject(this, 0);
        Logger.getGlobal().info("rmi Server running correctly...");
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
     * Called when a connectionHandler notifies the server the connection has been closed
     * @param connection connectionHandler instance
     */
    public void didDisconnect(ServerConnectionHandler connection) {
        users.forEach(
                (user, connectionHandler) -> {
                    if (connectionHandler == connection) {
                        waitingRoom.removeUser(user);
                        AdrenalineLogger.info(DID_DISCONNECT + user.getUsername());
                    }
                });
    }

    /**
     * Finds an user from the connectionID, iterating from its hashcode.
     * @param connectionID it's the connectionID from what the user has to be found.
     * @return the User looked for.
     */
    private User findUserFromID(int connectionID) {
        for(User user : users.keySet()) {
            if(users.get(user).hashCode() == connectionID) return user;
        }
        return null;
    }



    /**
     * When a client registers a new user
     * @param user the user to register
     * @param connectionHandler client connection handler
     * @return false if the user already exists and is connected, true otherwise
     */
    public boolean createUser(String username, ServerConnectionHandler connectionHandler) {
        var user = new User(username);
        if (checkUserAvailability(user)) {
            users.put(user, connectionHandler);
            return true;
        } else return false;
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

    @Override
    public boolean createUserRMIHelper(String username) throws RemoteException {
        try {
            Registry remoteRegistry = LocateRegistry.getRegistry(portNumberRMI);
            System.out.println(registry);
            ClientInterface clientRMI = (ClientInterface) remoteRegistry.lookup("ClientInterface");
        }catch (Exception e) {
            System.err.println("ServerRMI exception: " + e.toString());
            e.printStackTrace();
        }
        ServerConnectionHandler connectionHandler = new ServerRMIConnectionHandler(this, clientRMI);
        return createUser(username, connectionHandler);
    }

    /**
     * When a client decides to join a game
     * @param user the user who will play the game
     */
    public void userLogin(User user) {
        waitingRoom.addUser(user);
    }

    @Override
    public void joinWaitingRoom(String username) {

    }

    @Override
    public ArrayList<Character> getAvailableCharacters() {
        return null;
    }

    @Override
    public void chooseCharacter(Character character) {

    }

    @Override
    public void chooseGameSettings() {

    }

    @Override
    public ArrayList<AmmoColor> displaySpawnPoints() {
        return null;
    }

    @Override
    public void chooseSpawnPoint() {

    }

    @Override
    public void chooseMove() {

    }

    @Override
    public void chooseMovement() {

    }

    @Override
    public void chooseWhatToGrab() {

    }

    @Override
    public ArrayList<String> getAvailableWeapons() {
        return null;
    }

    @Override
    public void chooseWeapon(String weaponSelected) {

    }

    /**
     * Method used to start the process of using a weapon.
     * @param userID it's the userID of the player who is using the weapon.
     * @param gameID it's the gameID of the game where the player is playing.
     * @param weaponSelected it's the weapon that has to be used.
     * @return a Map containing the information about how to continue the weapon using process.
     */
    @Override
    public Map<String, String> useWeapon(int userID, UUID gameID, String weaponSelected) {
        Weapon weapon = gameControllers.get(gameID).lookForWeapon(weaponSelected, findUserFromID(userID));;
        CommunicationMessage weaponMessage = weapon.communicationMessageGenerator();
        Map<String, String> weaponProcess = new HashMap<>();
        weaponProcess.put(Weapon.weapon_key, weaponSelected);
        weaponProcess.put(CommunicationMessage.communication_message_key, weaponMessage.toString());
        switch (weaponMessage) {
            case DAMAGE_LIST: {
                weaponProcess.put(Effect.effect_key, "0");
                Player shooter = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
                ArrayList<ArrayList<Damage>> possibleDamages = gameControllers.get(gameID).useEffect(weapon, weapon.getEffects().get(0), shooter, null);
                stringifyDamages(possibleDamages, weaponProcess);
                break;
            }
            case MODES_LIST: {
                int i = 0;
                for (Effect effect : weapon.getEffects()) {
                    weaponProcess.put(Integer.toString(i), effect.getName());
                    i++;
                }
                break;
            }
            case EFFECTS_LIST: {
                ArrayList<ArrayList<Integer>> effectsCombinations = weapon.effectsCombinations();
                for (ArrayList<Integer> combination : effectsCombinations)
                    weaponProcess.put(Integer.toString(effectsCombinations.indexOf(combination)), combination.toString());
                break;
            }
            default:
                break;
        }
        return weaponProcess;
    }

    /**
     * Method used to make damages when using a weapon.
     * @param userID it's the ID of the user who is using the weapon.
     * @param args it's a map containing the damages to do with the weapon.
     * @param gameID it's the ID of the game where the damage has to be done.
     * @param damage it's the damage to be done.
     * @param weapon it's the name of the weapon that is being used.
     */
    @Override
    public void makeDamage(int userID, String potentiableBoolean, String effectIndex, UUID gameID, String damage, String weapon) {
        Weapon weaponToUse = gameControllers.get(gameID).lookForWeapon(weapon, findUserFromID(userID));
        Player shooter = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        PlayerColor playerColor = shooter.getCharacter().getColor();
        int indexOfEffect = Integer.parseInt(effectIndex);
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
            boolean applyPotentiableDamage = Boolean.parseBoolean(potentiableBoolean);
            if (applyPotentiableDamage) {
                damageToMake.addAll(gameControllers.get(gameID).getForPotentiableWeapon());
                for (Damage d : damageToMake)
                    gameControllers.get(gameID).applyDamage(d, playerColor);
                gameControllers.get(gameID).wipePotentiableWeapon();
            } else gameControllers.get(gameID).appendPotentiableWeapon(damageToMake);
        }
    }

    /**
     * Method used to calculate the damages made by an effect of the weapon.
     * @param userID it's the ID of the user that is using the weapon.
     * @param gameID it's the ID of the game.
     * @param forPotentiableWeapon it's the forPotentiableWeapon boolean.
     * @param effectSelected it's the effect to use.
     * @param weaponSelected it's the weapon to use.
     * @return a map containing all of the information about the damages of the effect used.
     */
    @Override
    public Map<String, String> useEffect(int userID, UUID gameID, String forPotentiableWeapon, String effectSelected, String weaponSelected) {
        Map<String, String> responseArgs = new HashMap<>();
        responseArgs.put(Weapon.weapon_key, weaponSelected);
        Player shooter = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        Weapon weapon = gameControllers.get(gameID).lookForWeapon(weaponSelected, findUserFromID(userID));
        Effect effect = null;
        if (weapon.communicationMessageGenerator().equals(MODES_LIST)) {
            for (Effect e : weapon.getEffects())
                if (e.getName().equals(effectSelected)) effect = e;
        } else {
            responseArgs.put(PotentiableWeapon.forPotentiableWeapon_key, forPotentiableWeapon);
            int index = Integer.parseInt(effectSelected);
            effect = weapon.getEffects().get(index - 1);
        }
        int indexOfEffect = weapon.getEffects().indexOf(effect);
        responseArgs.put(Effect.effect_key, Integer.toString(indexOfEffect));
        ArrayList<Damage> forPotentiableWeaponDamages = gameControllers.get(gameID).getForPotentiableWeapon();
        if (forPotentiableWeapon.isEmpty()) forPotentiableWeapon = null;
        ArrayList<ArrayList<Damage>> possibleDamages = gameControllers.get(gameID).useEffect(weapon, effect, shooter, forPotentiableWeaponDamages);
        stringifyDamages(possibleDamages, responseArgs);
        return responseArgs;
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

    @Override
    public Map<String, String> getAvailableModes() {
        return null;
    }

    @Override
    public void chooseMode(Map<String, String> modalityChosen) {

    }

    @Override
    public void getAvailableEffect() {

    }

    @Override
    public void chooseEffects() {

    }

    @Override
    public void getAvailablePowerups() {

    }

    @Override
    public void getAvailablePowerupsEffects() {

    }

    @Override
    public void choosePowerup() {

    }

    @Override
    public void choosePowerupEffects() {

    }

    @Override
    public ArrayList<String> canReload() {
        return null;
    }

    /**
     * Method used when a player has decided what weapons does he wants to reload.
     * @param weaponsSelected it's the list of weapons that the player wants to reload.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return TRUE if the process ended with success, FALSE otherwise.
     */
    @Override
    public boolean reload(List<String> weaponsSelected, int userID, UUID gameID) {
        List<Weapon> weapons = new ArrayList<>();
        for(String w : weaponsSelected) weapons.add(gameControllers.get(gameID).lookForWeapon(w, findUserFromID(userID)));
        return gameControllers.get(gameID).checkCostOfReload(weapons, findUserFromID(userID));
    }

    /**
     * Method used to know the weapons that a player has in his hand.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return the list of the names of the weapons that the player has in his hand.
     */
    @Override
    public List<String> weaponInHand(int userID, UUID gameID) {
        User user = findUserFromID(userID);
        return gameControllers.get(gameID).lookForPlayerWeapons(user);
    }

    /**
     * Method used to know the usable powerups that a player has in his hand.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return the list of powerups that a player can use through its turn.
     */
    @Override
    public List<String> usablePowerups(int userID, UUID gameID) {
        return gameControllers.get(gameID).getUsablePowerups(findUserFromID(userID));
    }

    /**
     * Method used to know the possible damages that a player can do when using a powerup.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @param powerup it's the Powerup::toString that is being used.
     * @see Powerup#toString()
     */
    @Override
    public List<String> powerupDamages(int userID, UUID gameID, String powerup) {
        List<Damage> possibleDamages = gameControllers.get(gameID).getPowerupDamages(powerup, findUserFromID(userID));
        return null;
    }

}
