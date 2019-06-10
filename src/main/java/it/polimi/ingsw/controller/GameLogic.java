package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.utility.*;

import java.util.*;

/**
 * Main controller. It will handle everything in regards to the communication between view and model.
 * It will manipulate the model in the way that the players ask.
 *
 * @author Daniele Chiappalupi
 */
public class GameLogic {

    /**
     * Number of weapons that are placed in every spawn point of the board.
     */
    private static int NUM_OF_WEAPONS_IN_SPAWNS = 3;

    private ArrayList<Player> players;
    private Board board;
    private boolean finalFrenzy;
    private UUID gameID;
    private ArrayList<Damage> forPotentiableWeapon;
    private DecksHandler decks;
    private WeaponController weaponController;
    private PowerupController powerupController;

    /**
     * String constants used in messages between client-server
     */
    public static final String gameID_key = "GAME_ID";

    //TODO: - Method implementation

    /**
     * Game Logic constructor.
     *
     * @param gameID it's the gameID.
     */
    public GameLogic(UUID gameID) {
        this.decks = new DecksHandler();
        this.finalFrenzy = false;
        this.gameID = gameID;
        this.forPotentiableWeapon = null;
        this.weaponController = new WeaponController();
        this.powerupController = new PowerupController();
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
     * It's the weapon controller getter.
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
     * Board setter.
     *
     * @param map it's the map of the game.
     * @param skulls it's the number of skulls that will be in the game.
     */
    public void setBoard(GameMap map, int skulls) {
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
        this.board = new Board(map, weapons, skulls);
    }

    /**
     * It's the player list getter.
     *
     * @return a clone of the list of the player in game.
     */
    public List<Player> getPlayers() {
        return (List<Player>)players.clone();
    }



    /**
     * Adds a player to the list of players in game.
     *
     * @param player it's the player to be added.
     */
    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void move(Player player) { }

    private void grabStuff(Player player) { }

    private void shootPeople(Player player) { }

    private void death(Player player) { }

    private void spawn(Player player) { }

    private void round(Player player) { }

    private void finalFrenzyRound(Player player) { }

    public void gameOver() { }

    /**
     * Board getter.
     *
     * @return the Board of the game.
     */
    public DecksHandler getDecks() {
        return decks;
    }

    /**
     * DecksHandler getter.
     *
     * @return the DecksHandler of the game.
     */
    public Board getBoard() {
        return board;
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
     * @return an arraylist of available player colors.
     */
    public List<String> getAvailableCharacters() {
        ArrayList<String> availableCharacters = new ArrayList<>();
        for (PlayerColor playerColor : PlayerColor.values()){
            availableCharacters.add(playerColor.toString());
        }
        for (Player player : players) {
            availableCharacters.remove(player.getCharacter().getColor().toString());
        }
        return availableCharacters;
    }

}