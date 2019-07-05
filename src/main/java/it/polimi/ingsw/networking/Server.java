package it.polimi.ingsw.networking;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import it.polimi.ingsw.utility.PersistenceManager;

import java.io.*;
import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
public class Server implements Loggable, WaitingRoomObserver, ServerInterface, Serializable {

    private static final String FINAL_FRENZY = "Final Frenzy mode has begun!";
    private static final String SPACE = " - ";
    private Registry registry;

    private Registry remoteRegistry;

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
     * Timeout, in seconds, to wait before a remote operation expires
     */
    private int clientOperationTimeoutInSeconds;

    /**
     * Minimum number of players
     */
    private int MIN_NUMBER_OF_PLAYERS = 3;

    /**
     * Maximum number of players
     */
    private int MAX_NUMBER_OF_PLAYERS = 3;

    /**
     * Log and exception strings
     */
    private static final String APPLICATION_LOADED          = "Server instance correctly loaded from a previous state";
    private static final String PREVIOUS_STATE_EXISTE       = "A previous Adrenaline Game Server exists. Do you want to restore it? [Y/N] ";
    private static final String EXC_SETUP                   = "Error while setting up the server :: ";
    private static final String EXC_CONFIG_FILE             = "Error while reading the configuration file :: ";
    private static final String OVERR_CONFIG_FILE           = "Configuration parameters overridden, reading from ./configuration.json";
    private static final String OP_TIMEOUT_CONF             = "Remote client operations timeout set to ";
    private static final String DID_DISCONNECT              = "User disconnected: ";
    private static final String START_NEW_GAME              = "New game started - ID: ";
    private static final String RMI_EXCEPTION               = "ServerRMI exception: ";
    private static final String SERVER_RMI_CONFIG           = "RMI Server configured on port ";
    private static final String SERVER_RMI_SUCCESS          = "RMI Server is running on ";
    private static final String ASKING_CHARACTER            = "Asking which character to use...";
    private static final String DAMAGE_DOESN_T_EXIST        = "This damage doesn't exist!";
    private static final String GAME_MAP_SET                = "GameMap set.";
    private static final String SKULLS_NUMBER_IS_NULL       = "Skulls number is null!";
    private static final String SKULLS_NUMBER_SET_TO        = "Skulls number set to ";
    private static final String GAME_ID                     = "Game ID: ";
    private static final String TURN_ENDED_USER             = "Turn ended for user ";
    private static final String NEXT_TURN                   = "Next turn will be played by the user ";


    /**
     * Entry point of the server application
     * @param args arguments passed as:
     *             [--run-saved-state] to restore a previously saved application state (WARNING! This will override other CLI arguments)
     *             [--socket=<socket-port-number>]
     *             [--rmi=<rmi-port-number>]
     *             [--waiting-room-timeout=<waiting-room-timeout-in-seconds>]
     *             [--operation-timeout=<operation-timeout-in-seconds>]
     *             [--debug] for debug purposes: sets waiting-room-timeout and operation-timeout to 10s
     */
    public static void main(String[] args) {
        AdrenalineLogger.setDebugMode(true);
        AdrenalineLogger.setLogName("Server");

        var arguments = Arrays.asList(args);

        if (arguments.contains("--run-saved-state")) { new Server(); return; }
        if (PersistenceManager.getInstance().isSnapshotAvailable()) {
            System.out.println(PREVIOUS_STATE_EXISTE);
            if (new Scanner(System.in).nextLine().equalsIgnoreCase("y")) {
                new Server();
                return;
            }
        }

        int socketPortNumber = 0, rmiPortNumber = 0, waitingRoomTimeout = 0, operationTimeout = 0;
        boolean socketSet = false, rmiSet = false, waitingRoomSet = false, operationTimeoutSet = false;

        var validArguments = arguments.stream()
                .filter(s -> s.matches("--\\w+=\\d+"))
                .map(s -> s.substring(2))
                .map(s -> Arrays.asList(s.split("=")))
                .collect(Collectors.toList());

        for(List<String> argument : validArguments) {
            switch (argument.get(0)) {
                case "socket":
                    socketPortNumber = Integer.parseInt(argument.get(1));
                    socketSet = true;
                    break;
                case "rmi":
                    rmiPortNumber = Integer.parseInt(argument.get(1));
                    rmiSet = true;
                    break;
                case "waiting-room-timeout":
                    waitingRoomTimeout = Integer.parseInt(argument.get(1));
                    waitingRoomSet = true;
                    break;
                case "operation-timeout":
                    operationTimeout = Integer.parseInt(argument.get(1));
                    operationTimeoutSet = true;
                    break;
                default:
                    AdrenalineLogger.error("Unsupported CLI argument: " + "--" + argument.get(0) + "=" + argument.get(1));
            }
        }

        try {
            var overrConfFile = new File("./configuration.json");
            var configurationFile = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "server-config.json");
            var mapper = new ObjectMapper();

            Map map;

            if (overrConfFile.exists()) {
                AdrenalineLogger.info(OVERR_CONFIG_FILE);
                map = mapper.readValue(overrConfFile, Map.class);
            }
            else map = mapper.readValue(configurationFile, Map.class);

            if (!socketSet) socketPortNumber = (int)map.get("socketPortNumber");
            if (!rmiSet) rmiPortNumber = (int)map.get("rmiPortNumber");
            if (!waitingRoomSet) waitingRoomTimeout = (int)map.get("waitingRoomTimeout");
            if (!operationTimeoutSet) operationTimeout = (int)map.get("operationTimeout");
        } catch (Exception e) {
            AdrenalineLogger.errorException(EXC_CONFIG_FILE, e);
        }

        if (arguments.contains("--debug")) { waitingRoomTimeout = 4; operationTimeout = 10; }

        new Server(socketPortNumber, rmiPortNumber, waitingRoomTimeout, operationTimeout);
    }

    /**
     * Server constructor responsible for setting up networking parameters and creates
     * the game and its controller.
     * @param portNumberSocket Socket port number
     * @param portNumberRMI RMI port number
     * @param waitingRoomTimeout timeout of the waiting room
     * @param clientOperationTimeoutInSeconds timeout of remote client operations
     */
    private Server(Integer portNumberSocket, Integer portNumberRMI, int waitingRoomTimeout, int clientOperationTimeoutInSeconds) {
        this.portNumberSocket = portNumberSocket;
        this.portNumberRMI    = portNumberRMI;
        this.users            = new ConcurrentHashMap<>();
        this.gameControllers  = new ConcurrentHashMap<>();
        this.clientOperationTimeoutInSeconds = clientOperationTimeoutInSeconds;

        this.waitingRoom = new WaitingRoom(MIN_NUMBER_OF_PLAYERS, MAX_NUMBER_OF_PLAYERS, waitingRoomTimeout, this);

        setupConnections();

        AdrenalineLogger.info(OP_TIMEOUT_CONF + clientOperationTimeoutInSeconds + " seconds.");
    }

    /**
     * The following convenience method is used to load a saved state of the application
     */
    private Server() {
        PersistenceManager.getInstance().loadServerSnapshot().setupConnections();
    }

    /**
     * Helper method used to setup server connections (Socket and RMI)
     */
    private void setupConnections() {
        ServerSocketHandler socketConnectionHandler;
        ServerRMIConnectionHandler RMIConnectionHandler;
        Thread socketConnectionHandlerThread;
        try {
            socketConnectionHandler = new ServerSocketHandler(portNumberSocket, this, clientOperationTimeoutInSeconds);
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
                        Ping.getInstance().removePing(connection);
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
                                .ifPresentOrElse(gameLogic -> {
                                    gameLogic.userDidDisconnect(user);
                                    this.turnEnded(user.hashCode(), gameLogic.getGameID());
                                }, ()-> waitingRoom.removeUser(user));

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

    @Override
    public void timeoutDidExpire(int userID) {
        var connectionHandler = users.get(findUserFromID(userID));
        connectionHandler.closeConnection();
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
            if(username.equalsIgnoreCase("ingconti")) printIngConti();
            return user.hashCode();
        } else return -1;
    }

    @Override
    public int registerClient(ClientInterface clientInterface, String username){
        ClientInterface clientRMI = clientInterface;
        ServerConnectionHandler connectionHandler = new ServerRMIConnectionHandler(this, clientRMI, clientOperationTimeoutInSeconds);
        Ping.getInstance().addPing(connectionHandler);
        return createUser(username, connectionHandler);
    }

    /**
     * When a client decides to join a game.
     * This method also handles the case when a user disconnected from a game and reconnects.
     * @param username username of the user who will play the game
     */
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

    /**
     * When the user chooses a character, the server is asked for its availability
     * @param gameID a gameID
     * @param userID a userID
     * @param characterChosen character chosen
     * @return true if the character is available, false otherwise
     */
    @Override
    public boolean choseCharacter(UUID gameID, int userID, String characterChosen) {
        var gameController      = gameControllers.get(gameID);
        var availableCharacters = gameController.getAvailableCharacters();
        var user                = findUserFromID(userID);

        String characterName = characterChosen.replaceAll("[^a-zA-Z\\-]", "").replaceAll("m", "");
        List<String> tempList = new ArrayList<>();
        for(String character : availableCharacters) {
            String temp = character.replaceAll("[^a-zA-Z\\-]", "").replaceAll("m", "");
            tempList.add(temp);
        }
        availableCharacters.clear();
        availableCharacters.addAll(tempList);

        if (availableCharacters.contains(characterName)) {
            var chosenCharacter = Character.getCharacterFromColor(PlayerColor.valueOf(Character.getColorFromToString(characterName)));
            if (gameController.addPlayer(new Player(user, chosenCharacter))) {
                // Number of players reached - server's going to ask the first
                // player (or the first one connected) for the choice of the game map
                var firstPlayer = gameController.getFirstActivePlayer();
                willChooseGameMap(firstPlayer.getUser(), gameID);
            }
            AdrenalineLogger.info("Game " + gameID + ": " + user.getUsername() + " chose " + characterChosen);
            return true;
        }
        AdrenalineLogger.error("Game " + gameID + ": " + user.getUsername() + " chose " + characterChosen);
        AdrenalineLogger.info(ASKING_CHARACTER);
        return false;
    }

    @Override
    public String getCharacterName(UUID gameID, int userID) throws RemoteException {
        String coloredName = gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)).getCharacter().getColouredName();
        coloredName = coloredName.replace("\n", "").replace("\r", "");
        return coloredName;
    }

    /**
     * Method called when the number of skulls has been chosen from the player and should be setted in the game.
     * @param skulls it's the Integer::toString of the number of skulls chosen
     * @param gameID it's the ID of the game
     */
    @Override
    public void didChooseSkulls(String skulls, UUID gameID) {
        gameControllers.get(gameID).getBoard().setSkulls(Integer.parseInt(skulls));
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + SKULLS_NUMBER_SET_TO + skulls);
    }

    @Override
    public String startActions(int userID, UUID gameID) {
        return gameControllers.get(gameID).newAction(findUserFromID(userID));
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
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + GAME_MAP_SET);
    }

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
    public void choseSpawnPoint(int userID, UUID gameID, String spawnPoint, String otherPowerup) {
        gameControllers.get(gameID).spawn(findUserFromID(userID), spawnPoint, otherPowerup);
    }

    @Override
    public List<String> getAvailableMoves(int userID, UUID gameID) {
        User user = findUserFromID(userID);
        return gameControllers.get(gameID).getAvailableMoves(user);
    }

    /**
     * When the user chooses a movement on the board
     * @param userID userID
     * @param gameID gameID
     * @param movement string description of the movement
     */
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
        var gameController  = gameControllers.get(gameID);
        var player          = gameController.lookForPlayerFromUser(user);
        var grabController  = gameController.getGrabController();
        return grabController.getPicks(player, gameController.getBoard(), powerupToSell);
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
        var gameController  = gameControllers.get(gameID);
        var player          = gameController.lookForPlayerFromUser(user);
        var grabController  = gameController.getGrabController();
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + user.getUsername() + " grabbed " + pick);
        return grabController.didGrab(pick, player, gameController.getDecks(), gameController.getBoard());
    }

    /**
     * Method used when the player has decided what powerup does he want to discard to let place for the new one
     * @param userID it's the ID of the user
     * @param gameID it's the ID of the game
     * @param powerup it's the Powerup::toString of the powerup that is being discarded
     */
    @Override
    public void powerupToDiscard(int userID, UUID gameID, String powerup) {
        User user = findUserFromID(userID);
        var gameController  = gameControllers.get(gameID);
        var player          = gameController.lookForPlayerFromUser(user);
        var grabController  = gameController.getGrabController();
        grabController.powerupToDiscard(powerup, player, gameController.getDecks(), gameController.getBoard());
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + user.getUsername() + " discarded the powerup " + powerup);
    }

    /**
     * Method used when the player has decided what weapon does he want to discard to let place for the new one
     * @param userID it's the ID of the user
     * @param gameID it's the ID of the game
     * @param weapon it's the Weapon::getName of the powerup that is being discarded
     */
    @Override
    public void weaponToDiscard(int userID, UUID gameID, String weapon) {
        User user = findUserFromID(userID);
        var gameController  = gameControllers.get(gameID);
        var player          = gameController.lookForPlayerFromUser(user);
        var grabController  = gameController.getGrabController();
        grabController.weaponToDiscard(weapon, player, gameController.getBoard(), gameController.getDecks());
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + user.getUsername() + " discarded the weapon " + weapon);
    }

    /**
     * Method used to start the process of shooting people.
     * @param userID it's the userID of the player who is using the weapon.
     * @param gameID it's the gameID of the game where the player is playing.
     * @return the list of weapons that a player has in his hand.
     */
    @Override
    public List<String> askWeapons(int userID, UUID gameID) {
        var gameController  = gameControllers.get(gameID);
        var user            = findUserFromID(userID);
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + user.getUsername() + " chose to shoot.");
        return gameController.getWeaponController().lookForPlayerWeapons(gameController.lookForPlayerFromUser(user), gameController.getBoard().getMap());
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
        var user = findUserFromID(userID);
        var gameController  = gameControllers.get(gameID);

        Player player = gameController.lookForPlayerFromUser(user);
        Weapon weapon = gameController.getWeaponController().lookForWeapon(weaponSelected, player);;
        CommunicationMessage weaponMessage = weapon.communicationMessageGenerator();
        Map<String, String> weaponProcess = new HashMap<>();
        weaponProcess.put(Weapon.weapon_key, weaponSelected);
        weaponProcess.put(CommunicationMessage.communication_message_key, weaponMessage.toString());
        switch (weaponMessage) {
            case DAMAGE_LIST: {
                weaponProcess.put(Effect.effect_key, "0");
                Player shooter = gameController.lookForPlayerFromUser(user);
                ArrayList<ArrayList<Damage>> possibleDamages = gameController.getWeaponController().useEffect(weapon, weapon.getEffects().get(0), shooter, null, gameController.getBoard(), gameController.getPlayers());
                if(possibleDamages.isEmpty()) weaponProcess.put(communication_message_key, DAMAGE_FAILURE.toString());
                else stringifyDamages(possibleDamages, weaponProcess);
                break;
            }
            default: {
                weaponProcess.put(communication_message_key, POWERUP_SELLING_LIST.toString());
                Map<String, String> box = gameController.getPowerupController().getPowerupInHand(player);
                weaponProcess.putAll(box);
                break;
            }
        }
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + user.getUsername() + " selected " + weapon);
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
        if (damageToMake.isEmpty()) throw new IllegalArgumentException(DAMAGE_DOESN_T_EXIST);
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
            Set<Player> playerShot = new HashSet<>();
            for(Damage d : damageToMake)
                if(d.getTarget() != shooter && d.getDamage() > 0)
                    playerShot.add(d.getTarget());
            List<Player> shotList = new ArrayList<>(playerShot);
            for(Player target : shotList) {
                gameControllers.get(gameID).getWeaponController().submitMarks(target, shooter.getCharacter().getColor());
                gameControllers.get(gameID).checkDeath(target);
            }
            gameControllers.get(gameID).getWeaponController().wipeMarksThisTurn();
            this.didUseWeapon(weapon, userID, gameID);
            powerups = gameControllers.get(gameID).getPowerupController().getAfterShotPowerups(shooter);
            if(!powerups.isEmpty())
                for(Damage d : damageToMake)
                    if(!d.getTarget().equals(shooter) && !d.getTarget().isDead() && d.getDamage() > 0)
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
        if (forPotentiableWeaponDamages.isEmpty()) forPotentiableWeaponDamages = null;
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

    /**
     * Method called when a player has done an action successfully.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return a String containing the situation of the game: it is null if the turn should continue.
     */
    @Override
    public String afterAction(int userID, UUID gameID) {
        String situation = gameControllers.get(gameID).updateNumOfRemainingActions(findUserFromID(userID));
        List<User> otherUsers = gameControllers.get(gameID).getOtherUsers(findUserFromID(userID));

        Map<String, List<String>> guiChanges = gameControllers.get(gameID).getUpdates(findUserFromID(userID));

        for(String key : guiChanges.keySet()) {
            Map<String, List<String>> singleChange = new HashMap<>();
            singleChange.put(key, guiChanges.get(key));
            users.get(findUserFromID(userID)).updateView(findUserFromID(userID).hashCode(), singleChange);
        }
        for(User user : otherUsers) {
            users.get(user).displayChanges(user.hashCode(), gameControllers.get(gameID).toStringFromPlayers(user));
            guiChanges = gameControllers.get(gameID).getUpdates(user);
            for(String key : guiChanges.keySet()) {
                Map<String, List<String>> singleChange = new HashMap<>();
                singleChange.put(key, guiChanges.get(key));
                users.get(user).updateView(user.hashCode(), singleChange);
            }

        }
        return situation;
    }

    /**
     * Method called before a turn finished: checks if any death has happened. If so, let the deads respawn.
     * @param gameID it's the ID of the game.
     */
    @Override
    public void checkDeathsBeforeEndTurn(UUID gameID) {
        List<User> deads = gameControllers.get(gameID).deathList();
        for(User deadUser : deads) {
            users.get(deadUser).spawnAfterDeath(deadUser.hashCode(), gameControllers.get(gameID).getPowerupToSpawn(deadUser));
        }
    }

    /**
     * Method called to get the powerup that can be used to spawn after a death.
     * @param userID it's the ID of the user.
     * @param gameID it's the ID of the game.
     * @return the list of Powerup::toString that can be used.
     */
    @Override
    public List<String> getSpawnAfterDeathPowerup(int userID, UUID gameID) {
        var user = findUserFromID(userID);
        return gameControllers.get(gameID).getPowerupToSpawn(user);
    }

  /**
   * Method called when a player has decided where he wants to spawn.
   * @param userID it's the ID of the user.
   * @param gameID it's the ID of the game.
   * @param powerup it's the powerup chosen.
   */
  @Override
  public void spawnAfterDeath(int userID, UUID gameID, String powerup) {
        var user = findUserFromID(userID);
        gameControllers.get(gameID).spawnAfterDeath(user, powerup);
        AdrenalineLogger.info(GAME_ID + gameID.toString() + SPACE + user.getUsername() + " spawned with the powerup " + powerup);
    }

    /**
   * Method called when the turn of a player is over.
   * @param userID it's the ID of the user.
   * @param gameID it's the ID of the game.
   */
  @Override
  public void turnEnded(int userID, UUID gameID) {
      AdrenalineLogger.info(GAME_ID + gameID + SPACE + TURN_ENDED_USER + findUserFromID(userID).getUsername());
      User nextUser = gameControllers.get(gameID).getNextPlayer(findUserFromID(userID));
      gameControllers.get(gameID).endTurn(nextUser);
      if(gameControllers.get(gameID).isFinalFrenzy()) {
          AdrenalineLogger.info(GAME_ID + gameID + SPACE + FINAL_FRENZY);
          for(User user : users.keySet()) {
              users.get(user).displayFinalFrenzy(user.hashCode());
          }
      }
      if(gameControllers.get(gameID).isThisTheEnd(findUserFromID(userID), nextUser)) {
          String scoreboard = gameControllers.get(gameID).endOfTheGame();
          for(User user : users.keySet())
              users.get(user).endOfTheGame(userID, scoreboard);
      }
      if(gameControllers.get(gameID).isAlreadyInGame(nextUser)) users.get(nextUser).startNewTurn(nextUser.hashCode());
      else users.get(nextUser).startNewTurnFromRespawn(nextUser.hashCode());
      AdrenalineLogger.info(GAME_ID + gameID + " - " + NEXT_TURN + nextUser.getUsername());

      synchronized (this) {
          PersistenceManager.getInstance().saveSnapshot(this);
      }
  }

    @Override
    public boolean canContinueAfterDeathsRespawn(int userID, UUID gameID) {
        return gameControllers.get(gameID).deathList().isEmpty();
    }

    @Override
    public List<String> movementsBeforeShot(int userID, UUID gameID) {
        return gameControllers.get(gameID).movesBeforeShot(findUserFromID(userID));
    }

    @Override
    public void movesBefore(String movement, int userID, UUID gameID) {
        gameControllers.get(gameID).getWeaponController().moveBefore(movement, gameControllers.get(gameID).getBoard(), gameControllers.get(gameID).lookForPlayerFromUser(findUserFromID(userID)));
    }

    private void printIngConti() {
      System.out.println( "\u001B[32m................................................................................\n" +
                          ".............................................,,/(########(*....................\n" +
                          "..........................................,/##########%#%%%%#/*,,..............\n" +
                          ".......................................*(##(((#((((#(/****/(#%%%%(*,...........\n" +
                          ".....................................*(##/*////////**,,,,,,,,/%%###(*,.........\n" +
                          "....................................,##(/((//((//,,,.........,*#%%%%%(,........\n" +
                          "...................................(##///***/*,,..............,/##%%&%(,.......\n" +
                          "..........,*/((..................,(#%(**,,,,,................,,/##%&&%%,,......\n" +
                          "*/(#%%%%%%%%%%%,.................(%%%/*,,,***,................,*(%%&&&%,.......\n" +
                          "%%#(/,,...,,,#%*.................%%&%/*,,,,,..............,,,,,,*%&&&&%,,......\n" +
                          "...       .,,(&/.................%&&%/**,,,,,........*/%%%%%%#(*,*%&&%#*/(((((/\n" +
                          "           ../&(.................(&&%/*(#%%%#/*,,,,,,/(#%%%%#(//**(%#%##/((((((\n" +
                          "            .*%#..................#%%/#%#%%%%#(//*,*/(((#%%/(///*//#%(/,*%%%##(\n" +
                          "            .,%#..................*/(((((##(##(/,***//////****,*,(#//,,&&&&&&\n" +
                          "            .,%%,.................,##//((///(//((*,.*,*/*,,*/*,*,,,*(,,.,&&&&&&\n" +
                          "            .,#%*..................(%****/((//(//*,.,,*,,....*,,,,***,../&&&&&&\n" +
                          "            .*#&/.......,*/#%%%%%%#//***,,,,,****,...,,,.....,,,*****,,(&&&&&&&\n" +
                          "            .*(&(....,//(@@@@@@@@@@(/***//*,,,*/*,,..,,*/*,,,,,******%&&&&&&&&&\n" +
                          "            .*#....##((((((((###(/*/*///****(/***,,***,**/*********#%&&&&&&&&\n" +
                          "            ./(&%....#&&@&((//,/(##((//////((((((((/**,***/********#&%&&&&&@@\n" +
                          "           ..*(&%....%&&@&&%%#&@@@@@@%%/////(/((((#//****//(/******/,(%%&&@&&%&\n" +
                          "          .../(&&..,,%&@@&@&%#@@@&%&&&&%/////#%#####(/**//**//***/(*.#%%&&&@@@&\n" +
                          ".         ...,(&&***/%&@@&@#&&&&&&&&&&&///(##(((#%%%#(/**,,***/(*..%%%%%&&&@@\n" +
                          " ...     ..../#&@////%&@@@@&%&&&&&&&&&&@@@%/((((((/*,,,,,,,**((#(,..*%%%%%%%&&@\n" +
                          "        . .../%&@(//(&&@@@@%&@&&&@@@@@@@@&&&(/////*******(###/...,%%%%%%%&%&@\n" +
                          "       .,,,.*%%&@###(&@@@@&&@@&&&@@@@@@@@@@&&&&(((((((((#####(,....#&%%%%&%%%&&\n" +
                          " .,*,...,/(#%#%&@#(((&@@@@%&@@@&@@@@@@@@@&&&&&@/*((########/,.....,&&&&&&&&&&&&\n" +
                          "...*(####(/**,#&&%(/(&@@@@%&@@&@@@@@@@&@@@&&&&@&,*/((####/.......,#&&%&&&&&&&&&\n" +
                          "###((**,/*/#%&%(#&/*(@&@&@&&@@&@@@@@@@@@&&&&&@@@(,,*/#(,.........(&&&&&&&&&&&&&\n" +
                          "/////(#&&&%&&&&&&(/*(@@&&&&&@@@@@@@@@@&&@&@&&@@@%/%&%&%*.  .....,%&&&&&&&&&&&&&\n" +
                          "#%&&&&&&&@@@&(*,,,,*/&&&&&&@@@@@@@@@@@@&@@@&&@@@@@&&&&&%&%(*,  ./&&&%&&&&&&&&&&\n" +
                          "&&&&%%%&&&(/*,,,,*/%%&&@&@@@@@@@@@@@@@@@@@&@@@@&%&%&&(/*****,(#&&&&&&&&&&&&&&\n" +
                          "%&&&&&&&&/*******,**/#%&&@@@@@@@@@@@@@@@@@&@&@@@@%%&&&%*,,. . .(%&&&&&&&&&&&&&@\n" +
                          "%&&&%#/*,*****,,,,,,**(&&&&@@@@@@@@@@@@@@@@@&&&&@%&&&&&%(**. .*%&&&&&&&&&&&&&&&\n" +
                          "&%###/******,,,,,,,,/&&&&&&@@@@@@@@@@@@@@@@@@@&&@&&%%&&//,.,/&&&&&&&&&&&&&&&&\n" +
                          "(%&&&%(,,,,,,,,,,**%&&&&&&&@@@@@@@@@@@@@@@@@@@&&@&&&&&%((/**(&&&&&&&&&&&&&&&&\n" +
                          "&%/%&,,,,,,,,,,,*&&&&@@&&&@@@@@@&@@@@@@@&@&@&&&@&&@@&**///%&&&&&&&&&&&&&&&&\n" +
                          "..,...,,,,,,,,,,,*%&&&&&@&@@@@@@@@@&&&@&@@@&&&@&&@&&%@&,,//#&&&&&&&&&&&&@&&&&\n" +
                          "........,,,,*,**(&&&&&&&&@@@@@@@@@@&&&&&&@@&&@@@&@&@@@&&(,*//%&&&&&&&&&&&&&&&&@");
  }
}