package it.polimi.ingsw.networking;

import it.polimi.ingsw.controller.GameLogic;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.rmi.ClientInterface;
import it.polimi.ingsw.networking.rmi.ServerInterface;
import it.polimi.ingsw.networking.rmi.ServerRMIConnectionHandler;
import it.polimi.ingsw.networking.socket.*;
import it.polimi.ingsw.networking.utility.Ping;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.utility.Loggable;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

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
    private static final String EXC_SETUP          = "Error while setting up the server :: ";
    private static final String DID_DISCONNECT     = "User disconnected: ";
    private static final String START_NEW_GAME     = "New game started - ID: ";
    private static final String RMI_EXCEPTION      = "ServerRMI exception: ";
    private static final String SERVER_RMI_CONFIG  = "RMI Server configured on port ";
    private static final String SERVER_RMI_SUCCESS = "RMI Server is running on ";
    private static final String ASKING_CHARACTER   = "Asking which character to use...";


    /**
     * Entry point of the server application
     * @param args arguments
     */
    public static void main(String[] args) {
        var arguments = Arrays.asList(args);
        AdrenalineLogger.setDebugMode(true);
        AdrenalineLogger.setLogName("Server");
        new Server(3334, 4444);
    }

    /**
     * Server constructor responsible for setting up networking parameters and creates
     * the game and its controller.
     */
    private Server(Integer portNumberSocket, Integer portNumberRMI) {
        this.portNumberSocket = portNumberSocket;
        this.portNumberRMI    = portNumberRMI;
        this.users            = new ConcurrentHashMap<>();
        this.gameControllers  = new ConcurrentHashMap<>();

        // TODO: - The following is a test with test parameters, the real waiting room settings must be read from a file
        this.waitingRoom = new WaitingRoom(3, 5, 5, this);

        setupConnections();
    }

    /**
     * Helper method used to setup server connections (Socket and RMI)
     */
    private void setupConnections() {
        ServerSocketHandler socketConnectionHandler;
        ServerRMIConnectionHandler RMIConnectionHandler;
        Thread socketConnectionHandlerThread;
        try {
            socketConnectionHandler = new ServerSocketHandler(portNumberSocket, this);
            socketConnectionHandlerThread = new Thread(socketConnectionHandler);
            socketConnectionHandlerThread.setPriority(Thread.MIN_PRIORITY);
            socketConnectionHandlerThread.start();
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }
        try {
            launch();
            logOnSuccess(SERVER_RMI_SUCCESS+Inet4Address.getLocalHost()+":"+portNumberRMI);
        } catch (Exception e) {
            logOnException(EXC_SETUP, e);
            return;
        }
    }

    /**
     * Method that export the server in order to create a RMI connection
     */
    private void launch() throws RemoteException {
        registry = LocateRegistry.createRegistry(portNumberRMI);
        registry.rebind(remoteReference, this);
        UnicastRemoteObject.exportObject(this, 0);
        AdrenalineLogger.config(SERVER_RMI_CONFIG+portNumberRMI);
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
        var gameLogic = new GameLogic(gameID, userList.size());
        var characters = gameLogic.getAvailableCharacters();

        gameControllers.put(gameID, gameLogic);

        logOnSuccess(START_NEW_GAME+gameID.toString());

        userList.stream()
                .map(users::get)
                .forEach(s -> s.gameDidStart(gameID.toString()));

        userList.parallelStream()
                .map(users::get)
                .forEach(s-> s.willChooseCharacter(characters));

    }

    /**
     * Called when a connectionHandler notifies the server the connection has been closed
     * @param connection connectionHandler instance
     */
    public void didDisconnect(ServerConnectionHandler connection) {
        users.forEach(
                (user, connectionHandler) -> {
                    if (connectionHandler == connection) {
                        gameControllers                             // if the user is binded to a gameLogic, set it as
                                .values()                           // disconnected, otherwise remove it from the
                                .stream()                           // waiting room
                                .filter(gameLogic -> gameLogic
                                        .getPlayers()
                                        .stream()
                                        .filter(player -> player
                                                .getUser()
                                                .equals(user))
                                        .count() != 0)
                                .findFirst()
                                .ifPresentOrElse(gameLogic -> gameLogic.userDidDisconnect(user), ()-> waitingRoom.removeUser(user));

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
            if (user.hashCode() == connectionID) {
                return user;
            }
        }
        return null;
    }

    /**
     * Checks if a user with the same username already exists and is connected
     * @param username String username to check
     * @return true if the user doesn't exist or isn't connected, false otherwise
     */
    @Override
    public boolean checkUsernameAvailability(String username){
        var user = new User(username);
        return checkUserAvailability(user);
    }

    /**
     * Checks if a user with the same username already exists and is connected
     * @param user User instance to check
     * @return true if the user doesn't exist or isn't connected, false otherwise
     */
    private boolean checkUserAvailability(User user) {
        var connectionHandler = users.get(user);
        if ( connectionHandler != null ) return !connectionHandler.isConnectionAvailable();
        else return true;
    }

    /**
     * When a client registers a new user
     * @param username the user to register
     * @param connectionHandler client connection handler
     * @return -1 if the user already exists and is connected, user's hashCode  otherwise
     */
    public int createUser(String username, ServerConnectionHandler connectionHandler) {
        var user = new User(username);
        if (checkUserAvailability(user)) {
            users.put(user, connectionHandler);
            return user.hashCode();
        } else return -1;
    }

    @Override
    public int createUserRMIHelper(String username) throws RemoteException {
        try {
            Registry remoteRegistry = LocateRegistry.getRegistry(portNumberRMI);
            logDescription(registry);
            ClientInterface clientRMI = (ClientInterface) remoteRegistry.lookup(username);
            ServerConnectionHandler connectionHandler = new ServerRMIConnectionHandler(this, clientRMI);
            Ping.getInstance().addPing(connectionHandler);
            return createUser(username, connectionHandler);
        } catch (Exception e) {
            logOnException(RMI_EXCEPTION, e);
        }
        return -1;
    }

    @Override
    public void joinWaitingRoom(String username) {
        gameControllers.values()
                .stream()
                .filter( gameLogic -> gameLogic     // for every GameController
                        .getPlayers()               // filter the ones
                        .stream()                   // whose players
                        .filter( player -> player   // have the specified username
                                .getUser()
                                .getUsername()
                                .equals(username)
                        )
                        .count() != 0
                ).findAny()                         // return an optional from this filtering operation
                .ifPresentOrElse(                   // if a GameController exists
                        gameLogic -> {              // find the user connected to the server and
                            users.forEach(          // notify him that the game has started (in this case, resumed)
                                    (user, conn) -> {
                                        if (user.getUsername().equals(username)) {
                                            conn.gameDidStart(gameLogic.getGameID().toString());
                                            gameLogic.userDidConnect(user);
                                        }
                                    }
                            );
                        },
                        () -> users.forEach(
                                (user, conn) -> {
                                    if (user.getUsername().equals(username)) waitingRoom.addUser(user);
                                }
                        )
                );
    }

    @Override
    public List<String> getAvailableCharacters(UUID gameID) {
        return gameControllers.get(gameID).getAvailableCharacters();
    }

    @Override
    public boolean choseCharacter(UUID gameID, int userID, String characterColor) {
        var gameController      = gameControllers.get(gameID);
        var availableCharacters = gameController.getAvailableCharacters();
        var user                = findUserFromID(userID);

        if (availableCharacters.contains(characterColor)) {
            var chosenCharacter = Character.getCharacterFromColor(PlayerColor.valueOf(characterColor));
            if (gameController.addPlayer(new Player(user, chosenCharacter))) {
                // Number of players reached - server's going to ask the firs
                // player (or the first one connected) for the choice of the game map
                var firstPlayer = gameController.getFirstActivePlayer();
                willChooseGameMap(firstPlayer.getUser(), gameID);
            }
            AdrenalineLogger.info("Game " + gameID + ": " + user.getUsername() + " chose " + characterColor);
            return true;
        }
        AdrenalineLogger.error("Game " + gameID + ": " + user.getUsername() + " chose " + characterColor);
        AdrenalineLogger.info(ASKING_CHARACTER);
        return false;
    }

    /**
     * Method used to ask a player to choose a GameMap
     * @param user user
     * @param gameID gameID
     */
    private void willChooseGameMap(User user, UUID gameID) {
        users.get(user).willChooseGameMap(gameID);
    }

    /**
     * Method called when a player chose the GameMap
     * @param gameID gameID
     * @param configuration string representation of the GameMap
     */
    @Override
    public void didChooseGameMap(UUID gameID, String configuration) {
        MapType mapType = MapType.valueOf(configuration);
        this.gameControllers.get(gameID).initializeMap(mapType);
    }

    //HERE
    @Override
    public List<String> getSpawnPowerups(int userID, UUID gameID) {
        ArrayList<String> powerups = new ArrayList<>();
        Powerup pow1 = gameControllers.get(gameID).getDecks().drawPowerup();
        Powerup pow2 = gameControllers.get(gameID).getDecks().drawPowerup();
        powerups.add(pow1.toString());
        powerups.add(pow2.toString());
        gameControllers.get(gameID).getDecks().wastePowerup(pow1);
        gameControllers.get(gameID).getDecks().wastePowerup(pow2);
        return powerups;
    }

    @Override
    public String choseSpawnPoint(int userID, UUID gameID, String spawnPoint, String otherPowerup) {
        gameControllers.get(gameID).spawn(findUserFromID(userID), spawnPoint, otherPowerup);
        return gameControllers.get(gameID).getBoard().toStringFromPlayer(gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)));
    }

    @Override
    public List<String> getAvailableMoves(int userID, UUID gameID) {
        User user = findUserFromID(userID);
        return gameControllers.get(gameID).getAvailableMoves(user);
    }

    @Override
    public void move(int userID, UUID gameID, String movement) {
        gameControllers.get(gameID).movePlayer(findUserFromID(userID), movement);
    }

    /**
     * Method used to start the process of picking something from the map.
     * @param userID it's the ID of the user who wants to pick something.
     * @param gameID it's the ID of the game.
     * @param powerupToSell it's the list of powerup that the player want to sell
     * @return the list of possible picks.
     */
    @Override
    public List<String> askPicks(int userID, UUID gameID, List<String> powerupToSell) {
        User user = findUserFromID(userID);
        return gameControllers.get(gameID).getGrabController().getPicks(gameControllers.get(gameID).lookForPlayerFromUser(user), gameControllers.get(gameID).getBoard(), powerupToSell);
    }

    /**
     * Mehod used when the player has decided what does he want to grab, and also where does he want to move because of the grabbing.
     * @param pick it's the toString() of the thing that the player has decided to grab.
     * @param userID it's the ID of the user who wants to pick something
     * @param gameID it's the ID of the game
     * @return a map containing the information about the success of the operation
     */
    @Override
    public Map<String, String> didChooseWhatToGrab(String pick, int userID, UUID gameID) {
        User user = findUserFromID(userID);
        return gameControllers.get(gameID).getGrabController().didGrab(pick, gameControllers.get(gameID).lookForPlayerFromUser(user), gameControllers.get(gameID).getDecks(), gameControllers.get(gameID).getBoard());
    }

    /**
     * Method used when the player has decided what powerup does he want to discard to let place for the new one
     * @param userID it's the ID of the user
     * @param gameID it's the ID of the game
     * @param powerup it's the Powerup::toString of the powerup that is being discarded
     * @return the current situation of the board for the given user
     */
    @Override
    public String powerupToDiscard(int userID, UUID gameID, String powerup) {
        User user = findUserFromID(userID);
        return gameControllers.get(gameID).getGrabController().powerupToDiscard(powerup, gameControllers.get(gameID).lookForPlayerFromUser(user), gameControllers.get(gameID).getDecks(), gameControllers.get(gameID).getBoard());
    }

    /**
     * Method used when the player has decided what weapon does he want to discard to let place for the new one
     * @param userID it's the ID of the user
     * @param gameID it's the ID of the game
     * @param weapon it's the Weapon::getName of the powerup that is being discarded
     * @return the current situation of the board for the given user
     */
    @Override
    public String weaponToDiscard(int userID, UUID gameID, String weapon) {
        User user = findUserFromID(userID);
        return gameControllers.get(gameID).getGrabController().weaponToDiscard(weapon, gameControllers.get(gameID).lookForPlayerFromUser(user), gameControllers.get(gameID).getBoard(), gameControllers.get(gameID).getDecks());
    }

    /**
     * Method used to start the process of shooting people.
     * @param userID it's the userID of the player who is using the weapon.
     * @param gameID it's the gameID of the game where the player is playing.
     * @return the list of weapons that a player has in his hand.
     */
    @Override
    public List<String> askWeapons(int userID, UUID gameID) {
        return this.gameControllers.get(gameID).getWeaponController().lookForPlayerWeapons(gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)), gameControllers.get(gameID).getBoard().getMap());
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
        Player player = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        Weapon weapon = gameControllers.get(gameID).getWeaponController().lookForWeapon(weaponSelected, player);;
        CommunicationMessage weaponMessage = weapon.communicationMessageGenerator();
        Map<String, String> weaponProcess = new HashMap<>();
        weaponProcess.put(Weapon.weapon_key, weaponSelected);
        weaponProcess.put(CommunicationMessage.communication_message_key, weaponMessage.toString());
        switch (weaponMessage) {
            case DAMAGE_LIST: {
                weaponProcess.put(Effect.effect_key, "0");
                Player shooter = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
                ArrayList<ArrayList<Damage>> possibleDamages = gameControllers.get(gameID).getWeaponController().useEffect(weapon, weapon.getEffects().get(0), shooter, null, gameControllers.get(gameID).getBoard(), gameControllers.get(gameID).getPlayers());
                if(possibleDamages.isEmpty()) weaponProcess.put(communication_message_key, DAMAGE_FAILURE.toString());
                else stringifyDamages(possibleDamages, weaponProcess);
                break;
            }
            default: {
                weaponProcess.put(communication_message_key, POWERUP_SELLING_LIST.toString());
                Map<String, String> box = gameControllers.get(gameID).getPowerupController().getPowerupInHand(player);
                weaponProcess.putAll(box);
                break;
            }
        }
        return weaponProcess;
    }

    @Override
    public Map<String, String> useWeaponAfterPowerupSelling(int userID, UUID gameID, String weaponSelected, List<String> powerups) {
        Player player = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        Weapon weapon = gameControllers.get(gameID).getWeaponController().lookForWeapon(weaponSelected, player);;
        if(!powerups.isEmpty()) gameControllers.get(gameID).getWeaponController().addPowerupSold(powerups, player);
        CommunicationMessage weaponMessage = weapon.communicationMessageGenerator();
        Map<String, String> weaponProcess = new HashMap<>();
        weaponProcess.put(Weapon.weapon_key, weaponSelected);
        weaponProcess.put(CommunicationMessage.communication_message_key, weaponMessage.toString());
        switch(weaponMessage) {
            case MODES_LIST: {
                int i = 0;
                for (Effect effect : weapon.getEffects()) {
                    List<Integer> boxList = new ArrayList<>();
                    boxList.add(weapon.getEffects().indexOf(effect)+1);
                    if(gameControllers.get(gameID).getWeaponController().canAffordCost(weapon, boxList, player)) weaponProcess.put(Integer.toString(i), effect.getName());
                    i++;
                }
                break;
            }
            case EFFECTS_LIST: {
                ArrayList<ArrayList<Integer>> effectsCombinations = weapon.effectsCombinations();
                for (ArrayList<Integer> combination : effectsCombinations)
                    if(gameControllers.get(gameID).getWeaponController().canAffordCost(weapon, combination, player))
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
    public List<String> makeDamage(int userID, String potentiableBoolean, String effectIndex, UUID gameID, String damage, String weapon) {
        List<String> powerups = new ArrayList<>();
        boolean applied = true;
        Player shooter = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        Weapon weaponToUse = gameControllers.get(gameID).getWeaponController().lookForWeapon(weapon, shooter);
        PlayerColor playerColor = shooter.getCharacter().getColor();
        int indexOfEffect = Integer.parseInt(effectIndex);
        boolean potentiable = false;
        if (weaponToUse.communicationMessageGenerator() == EFFECTS_LIST) potentiable = true;
        ArrayList<ArrayList<Damage>> possibleDamages;
        boolean toApply = true;
        if (!potentiable)
            possibleDamages = gameControllers.get(gameID).getWeaponController().useEffect(weaponToUse, weaponToUse.getEffects().get(indexOfEffect), shooter, null, gameControllers.get(gameID).getBoard(), gameControllers.get(gameID).getPlayers());
        else {
            toApply = false;
            Effect effect = weaponToUse.getEffects().get(indexOfEffect);
            List<Damage> forPotentiableWeapon = gameControllers.get(gameID).getWeaponController().getForPotentiableWeapon();
            if (forPotentiableWeapon.isEmpty() || effect.getProperties().containsKey(EffectProperty.MoveMe))
                forPotentiableWeapon = null;
            if (effect.getProperties().containsKey(EffectProperty.MoveMe)) {
                toApply = true;
                applied = false;
            }
            possibleDamages = gameControllers.get(gameID).getWeaponController().useEffect(weaponToUse, effect, shooter, forPotentiableWeapon, gameControllers.get(gameID).getBoard(), gameControllers.get(gameID).getPlayers());
        }
        ArrayList<Damage> damageToMake = new ArrayList<>();
        for (ArrayList<Damage> damages : possibleDamages)
            if (damages.toString().equals(damage)) damageToMake = damages;
        if (damageToMake.isEmpty()) throw new IllegalArgumentException("This damage doesn't exist!");
        if (toApply) {
            for (Damage d : damageToMake)
                gameControllers.get(gameID).getWeaponController().applyDamage(d, playerColor, gameControllers.get(gameID).getBoard());
        } else {
            boolean applyPotentiableDamage = Boolean.parseBoolean(potentiableBoolean);
            if (applyPotentiableDamage) {
                damageToMake.addAll(gameControllers.get(gameID).getWeaponController().getForPotentiableWeapon());
                for (Damage d : damageToMake)
                    gameControllers.get(gameID).getWeaponController().applyDamage(d, playerColor, gameControllers.get(gameID).getBoard());
                gameControllers.get(gameID).getWeaponController().wipePotentiableWeapon();
            } else {
                gameControllers.get(gameID).getWeaponController().appendPotentiableWeapon(damageToMake);
                applied = false;
            }
        }
        if(applied) {
            this.didUseWeapon(weapon, userID, gameID);
            powerups = gameControllers.get(gameID).getPowerupController().getAfterShotPowerups(shooter);
            if(!powerups.isEmpty())
                for(Damage d : damageToMake)
                    if(!d.getTarget().equals(shooter))
                        gameControllers.get(gameID).getPowerupController().addPowerupTarget(d.getTarget());
        }
        return powerups;
    }

    /**
     * Private method called after the successfull usage of a weapon: it unloads the weapon and subs the cost of its usage from the player who has used it.
     * @param weapon it's the weapon used.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     */
    private void didUseWeapon(String weapon, int userID, UUID gameID) {
        Player shooter = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        Weapon weaponUsed = gameControllers.get(gameID).getWeaponController().lookForWeapon(weapon, shooter);
        weaponUsed.unload();
        gameControllers.get(gameID).getWeaponController().applyCost(shooter, gameControllers.get(gameID).getDecks());
        gameControllers.get(gameID).getPowerupController().clearPowerupTargets();
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
        Weapon weapon = gameControllers.get(gameID).getWeaponController().lookForWeapon(weaponSelected, shooter);
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
        List<Damage> forPotentiableWeaponDamages = gameControllers.get(gameID).getWeaponController().getForPotentiableWeapon();
        if (forPotentiableWeapon.isEmpty()) forPotentiableWeapon = null;
        ArrayList<ArrayList<Damage>> possibleDamages = gameControllers.get(gameID).getWeaponController().useEffect(weapon, effect, shooter, forPotentiableWeaponDamages, gameControllers.get(gameID).getBoard(), gameControllers.get(gameID).getPlayers());
        if(possibleDamages.isEmpty()) {
            responseArgs.put(Damage.damage_key, Damage.no_damage);
            gameControllers.get(gameID).getWeaponController().restoreMap(gameControllers.get(gameID).getBoard().getMap(), gameControllers.get(gameID).getPlayers());
        }
        else stringifyDamages(possibleDamages, responseArgs);
        Map<AmmoColor, Integer> effectCost = effect.getCost();
        gameControllers.get(gameID).getWeaponController().addEffectsCost(effectCost);
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
        Player player = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        for(String w : weaponsSelected) weapons.add(gameControllers.get(gameID).getWeaponController().lookForWeapon(w, player));
        return gameControllers.get(gameID).getWeaponController().checkCostOfReload(weapons, player);
    }

    /**
     * Method used to know the weapons that a player has in his hand.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return the list of the names of the weapons that the player has in his hand.
     */
    @Override
    public List<String> getUnloadedWeaponInHand(int userID, UUID gameID) {
        Player player = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID));
        return gameControllers.get(gameID).getWeaponController().lookForUnloadedPlayerWeapons(player);
    }

    /**
     * Method used to know the usable powerups that a player has in his hand.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return the list of powerups that a player can use through its turn.
     */
    @Override
    public List<String> getUsablePowerups(int userID, UUID gameID) {
        return gameControllers.get(gameID).getPowerupController().getUsablePowerups(gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)));
    }

    /**
     * Method used to know the possible damages that a player can do when using a powerup.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @param powerup it's the Powerup::toString that is being used {@link Powerup#toString()}
     * @return the list of possible damages that can be done with this powerup.
     */
    @Override
    public List<String> getPowerupDamages(int userID, UUID gameID, String powerup) {
        List<Damage> possibleDamages = gameControllers.get(gameID).getPowerupController().getPowerupDamages(powerup, gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)), gameControllers.get(gameID).getBoard().getMap(), gameControllers.get(gameID).getPlayers());
        List<String> returnValues = new ArrayList<>();
        for(Damage damage : possibleDamages)
            returnValues.add(damage.toString());
        return returnValues;
    }

    /**
     * Method used to apply the powerup effect selected by the user.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @param powerup it's the Powerup::toString of the powerup that is being used.
     * @param damage it's the Damage::toString of the damage that has been selected.
     */
    @Override
    public void applyPowerupDamage(int userID, UUID gameID, String powerup, String damage) {
        List<Damage> possibleDamages = gameControllers.get(gameID).getPowerupController().getPowerupDamages(powerup, gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)), gameControllers.get(gameID).getBoard().getMap(), gameControllers.get(gameID).getPlayers());
        for(Damage d : possibleDamages)
            if(d.toString().equals(damage))
                gameControllers.get(gameID).getWeaponController().applyDamage(d, gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)).getCharacter().getColor(), gameControllers.get(gameID).getBoard());
        gameControllers.get(gameID).getPowerupController().wastePowerup(powerup, gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)), gameControllers.get(gameID).getDecks());
    }

    /**
     * Method used to find the powerups in the hand of the user.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return the list of Powerup::toString that the player owns.
     */
    @Override
    public List<String> getPowerupsInHand(int userID, UUID gameID) {
        Map<String, String> powerupsMap = gameControllers.get(gameID).getPowerupController().getPowerupInHand(gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)));
        List<String> powerupsList = new ArrayList<>(powerupsMap.values());
        return powerupsList;
    }

    /**
     * Method used to sell the powerups selected to reload the weapons.
     * @param powerups it's the list of powerups to sell.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     */
    @Override
    public void sellPowerupToReload(List<String> powerups, int userID, UUID gameID) {
        gameControllers.get(gameID).getWeaponController().addPowerupSold(powerups, gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)));
    }

}