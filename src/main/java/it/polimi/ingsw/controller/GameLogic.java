package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.utility.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main controller. It will handle everything in regards to the communication between view and model.
 * It will manipulate the model in the way that the players ask.
 *
 * @author Daniele Chiappalupi
 */
@SuppressWarnings("squid:S00115")
public class GameLogic {

    /**
     * Number of weapons that are placed in every spawn point of the board.
     */
    private static final int NUM_OF_WEAPONS_IN_SPAWNS = 3;

    private int numberOfPlayers;
    private boolean finalFrenzy;
    private ArrayList<Player> players;
    private Board board;
    private UUID gameID;
    private DecksHandler decks;
    private WeaponController weaponController;
    private PowerupController powerupController;
    private GrabController grabController;
    private Player firstPlayer;

    /**
     * String constants used in messages between client-server
     */
    public static final String gameID_key       = "GAME_ID";
    public static final String available_moves  = "MOVES";
    public static final String movement         = "MOVEMENT";

    /**
     * Game Logic constructor.
     *
     * @param gameID it's the gameID.
     */
    public GameLogic(UUID gameID, int numberOfPlayers) {
        this.decks = new DecksHandler();
        this.finalFrenzy = false;
        this.gameID = gameID;
        this.weaponController = new WeaponController();
        this.powerupController = new PowerupController();
        this.grabController = new GrabController();
        this.players = new ArrayList<>();
    }

    /**
     * It's the game UUID getter.
     *
     * @return the {@link UUID} of the game.
     */
    public UUID getGameID() {
        return gameID;
    }

    /**
     * Method called when a connected user disconnects
     * @param user old user instance
     */
    public void userDidDisconnect(User user) {
        Optional.of(lookForPlayerFromUser(user)).ifPresent(Player::disablePlayer);
    }

    /**
     * Method called when a disabled user reconnects
     * @param user new user instance
     */
    public void userDidConnect(User user) {
        Optional.of(lookForPlayerFromUser(user)).ifPresent(player -> player.reEnablePlayer(user));
    }

    /**
     * It's the grab controller getter.
     *
     * @return the {@link GrabController} of the game.
     */
    public GrabController getGrabController() {
        return grabController;
    }

    /**
     * It's the powerup controller getter.
     *
     * @return the {@link PowerupController} of the game.
     */
    public PowerupController getPowerupController() {
        return powerupController;
    }

    /**
     * It's the weapon controller getter.
     *
     * @return the {@link WeaponController} of the game.
     */
    public WeaponController getWeaponController() {
        return weaponController;
    }

    /**
     * It's the player list getter.
     *
     * @return a clone of the list of the player in game.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }


    /**
     * First player getter
     * @return the first player in the game
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * First active player getter
     * @return the first player in the game
     */
    @SuppressWarnings("all")
    public Player getFirstActivePlayer() {
        return players.stream().filter(Player::isActive).findFirst().get();
    }

    /**
     * First player getter
     * @param firstPlayer it's the player to set
     */
    private void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * Adds a player to the list of players in game.
     *
     * @param player it's the player to be added.
     */
    public synchronized boolean addPlayer(Player player) {
        if (players.isEmpty()) setFirstPlayer(player);
        this.players.add(player);
        return this.players.size() >= numberOfPlayers;
    }

    /**
     * Board getter.
     *
     * @return the Board of the game.
     */
    public DecksHandler getDecks() {
        return decks;
    }

    /**
     * Board getter.
     *
     * @return the Board of the game.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Initializes the map of a game when the configuration has been chosen.
     *
     * @param mapType it's the MapType.toString() of the chosen configuration.
     */
    public void initializeMap(MapType mapType) {
        GameMap map = new GameMap(mapType);
        Map<AmmoColor, List<Weapon>> weapons = new EnumMap<>(AmmoColor.class);
        List<AmmoColor> spawnColorList = new ArrayList<>();
        spawnColorList.add(AmmoColor.blue);
        spawnColorList.add(AmmoColor.red);
        spawnColorList.add(AmmoColor.yellow);
        for(AmmoColor color : spawnColorList) {
            List<Weapon> weaponTrio = new ArrayList<>();
            for(int i = 0; i < NUM_OF_WEAPONS_IN_SPAWNS; i++)
                weaponTrio.add(decks.drawWeapon());
            weapons.put(color, weaponTrio);
        }
        this.board = new Board(map, weapons);
    }

    /**
     * This method let an user spawn in the decided color and adds the other powerup to the user's hand.
     * @param user it's the user who is spawning.
     * @param spawnPoint it's the Powerup::toString of the powerup that the player has chosen to discard.
     * @param otherPowerup it's the Powerup::toString of the powerup that the player will have in his hand.
     */
    public void spawn(User user, String spawnPoint, String otherPowerup) {
        Player player = lookForPlayerFromUser(user);

        Powerup spawnPowerup = decks.drawPowerup();
        while(!spawnPowerup.toString().equalsIgnoreCase(spawnPoint)) {
            decks.wastePowerup(spawnPowerup);
            spawnPowerup = decks.drawPowerup();
        }

        Powerup powerupInHand = decks.drawPowerup();
        while(!powerupInHand.toString().equalsIgnoreCase(otherPowerup)) {
            decks.wastePowerup(powerupInHand);
            powerupInHand = decks.drawPowerup();
        }

        AmmoColor spawnColor = spawnPowerup.getColor();

        this.board.getMap().setPlayerPosition(player, this.board.getMap().getSpawnPoint(spawnColor));
        player.getPlayerHand().addPowerup(powerupInHand);
        this.decks.wastePowerup(spawnPowerup);
    }

    /**
     * Skulls setter.
     *
     * @param skulls it's the number of skulls that will be in the game.
     */
    public void setSkulls(int skulls) {
        this.board.setSkulls(skulls);
    }

    /**
     * This method is used to find a player from its user.
     * @param user it's the user that is wanted to bind with its player.
     * @return the player that is being searched.
     */
    public Player lookForPlayerFromUser(User user) {
        Player player = null;
        for(Player p : players)
            if(p.getUser().equals(user))
                player = p;
        if(player == null) throw new NullPointerException("This user doesn't exists.");
        return player;
    }

    /**
     * This method is used to get the available characters.
     * @return a list of available player colors.
     */
    public synchronized List<String> getAvailableCharacters() {
        return Character.getCharacters()
                .stream()
                .filter(c -> !players.stream()
                        .map(Player::getCharacter)
                        .collect(Collectors.toList())
                        .contains(c))
                .map(Character::getColor)
                .map(PlayerColor::toString)
                .collect(Collectors.toList());
    }

    public List<String> getAvailableMoves(User user){
        ArrayList<String> availableMoves = new ArrayList<>();
        List<Cell> availableCells = getBoard().getMap().getCellsAtMaxDistance(lookForPlayerFromUser(user), lookForPlayerFromUser(user).getPlayerBoard().getStepsOfMovement());
        for(Cell cell : availableCells){
            availableMoves.add(cell.toString());
        }
        return availableMoves;
    }

    /**
     * Moves the player to the specified cell in the map
     * @param user user
     * @param movement string description of the movement
     */
    public void movePlayer(User user, String movement) {
        var map = getBoard().getMap();
        var player = lookForPlayerFromUser(user);
        var availableCells = map.getCellsAtMaxDistance(player, player.getPlayerBoard().getStepsOfMovement());
        availableCells.forEach( cell -> { if (cell.toString().equalsIgnoreCase(movement))map.setPlayerPosition(player, cell); });
    }
}