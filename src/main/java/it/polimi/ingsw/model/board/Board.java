package it.polimi.ingsw.model.board;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.utility.AdrenalineLogger;

/**
 *
 * @author Elena Iannucci
 */

public class Board implements Serializable {

    /**
     * String constants used in Board::toString
     */
    private static final String WEAPON_IN_RESPAWN   = "Here are the weapons in the spawn points:";
    private static final String WEAPONS_IN_HAND     = "Here are your weapons:";
    private static final String NO_WEAPON_IN_HAND   = "you haven't got any weapon in your hand.";
    private static final String POWERUPS_IN_HAND    = "Here are your powerups: ";
    private static final String NO_POWERUP_IN_HAND  = "You haven't got any powerup in your hand.";
    private static final String AMMOS_IN_HAND       = "Here are your ammos:";
    private static final String SKULLS_REMAINING    = "Number of skulls remaining: ";
    private static final String NEW_LINE            = "\n";
    private static final String INDENT              = "\t";

    /**
     * String that contains the path information to where the resources are located.
     */
    private static final String PATHNAME            = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
    private static final String PATHNAME_POINTS     = "max_points_list.json";

    /**
     * Parameter for the jsno deparse
     */
    private static final Integer NUM_OF_MAX_ASSIGNABLE_POINTS               = 6;

    /**
     * String constant used in messages between client-server
     */
    public static final String skulls_key = "SKULLS";

    /**
     * Map of the board
     */
    private GameMap map;

    /**
     * Three collections of three Weapons placed on the board near the spawpoints divided by colors
     */
    private Map<AmmoColor, List<Weapon>> weapons;

    /**
     * Killshot track on the board that takes into account the deaths occured during the game
     */
    private Map<Integer, List<PlayerColor>> skullsTrack;

    /**
     * List of points that can be done through the skulls
     */
    private List<Integer> pointsFromSkulls;

    /**
     * Boolean that saves if the game is in final frenzy mode
     */
    private boolean finalFrenzy;

    /**
     * Constructor: creates a new board based on its map, its killshot track and weapons positioned near its spawnpoint
     * @param map the map of the board
     * @param weapons a collection of three boards placed next to the spawpoints defined by a color and that contain three weapons each
     * */
    public Board(GameMap map, Map<AmmoColor, List<Weapon>> weapons){
        this.weapons = new EnumMap<>(AmmoColor.class);
        this.skullsTrack = new LinkedHashMap<>();
        this.map = map;
        this.weapons.putAll(weapons);
        this.finalFrenzy = false;
        this.initializePointsFromSkulls();
    }

    /**
     * Initializes the list of points obtainable from the skullstrack  reading them from a json file.
     */
    private void initializePointsFromSkulls() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonName = PATHNAME_POINTS;
        Integer[] box = new Integer[NUM_OF_MAX_ASSIGNABLE_POINTS];
        try {
            File json = new File(PATHNAME + jsonName);
            box = objectMapper.readValue(json, Integer[].class);
        } catch (IOException e) {
            AdrenalineLogger.error(e.toString());
        }
        this.pointsFromSkulls = new ArrayList<>(Arrays.asList(box));
    }

    /**
     * Sets the number of skulls of the game.
     * @param skulls it's the number of skulls that the game will have.
     */
    public void setSkulls(int skulls) {
        for(int i = 0; i < skulls; i++) skullsTrack.put(i, null);
    }

    /**
     * Method that removes a weapon card from the board
     * @param chosenWeapon weapon chosen by the player between the ones next to the spawnpoint he is in
     */
    public void pickWeapon(Weapon chosenWeapon) {
        int index;
        for (AmmoColor ammoColor : weapons.keySet()){
            for (Weapon weapon : weapons.get(ammoColor)){
                if (weapon == chosenWeapon) {
                    index = weapons.get(ammoColor).indexOf(weapon);
                    weapons.get(ammoColor).set(index, null);
                }
            }
        }
    }

    /**
     * Method that places a weapon dealt randomly on the board if a weapon card is missing
     * @param decksHandler class that takes care of the draw of the card
     */
    public void refillWeapons(DecksHandler decksHandler) {
        for(AmmoColor ammoColor : weapons.keySet()){
            for (int i=0; i<3; i++){
                if (weapons.get(ammoColor).get(i) == null) {
                    weapons.get(ammoColor).set(i, decksHandler.drawWeapon());
                }
            }
        }
    }

    /**
     * Method that shows the three weapons placed next to a certain spawn point
     * @param color the color of the spawn point next to the weapons that have to be shawn, defined as a AmmoColor Enum type
     * @return a list containing the weapons in the spawn point of the color provided
     */
    public List<Weapon> showWeapons(AmmoColor color) {
        return new ArrayList<>(weapons.get(color));
    }

    /**
     * Method that shows the skulls left that represent the deaths that have to occur for the game to end
     * @return the number of skulls left
     */
    public int skullsLeft() {
        int lastSkullConquered = -1;
        int totalSkulls = 0;
        for(Map.Entry<Integer, List<PlayerColor>> entry : skullsTrack.entrySet()){
            if (entry.getValue() == null && lastSkullConquered == -1) {
                lastSkullConquered = entry.getKey();
            }
            totalSkulls++;
        }
        if (lastSkullConquered == -1) return 0;
        else return (totalSkulls - lastSkullConquered);
    }

    /**
     * Method that adds a certain number (either 1 or 2) of damage tokens of a certain color to the killshot track
     * @param player the color of the player that caused the death
     * @param count the number of damage tokens to add (either 1 or 2)
     */
    public void addBloodFrom(PlayerColor player, Integer count) {
        int i = 0;
        List<PlayerColor> playerColors = new ArrayList<>();
        while (this.skullsTrack.get(i) != null) i++;
        for (int j = 0; j < count; j++){
            playerColors.add(player);
        }
        skullsTrack.put(i, playerColors);
        if(skullsLeft() == 0) finalFrenzy = true;
    }

    /**
     * Return true if the game is in final frenzy mode
     */
    public boolean isFinalFrenzy() {
        return finalFrenzy;
    }

    /**
     * Method that shows the number of damage tokens and their color for a given death round
     * @param i an index that represents the death rounds starting from zero
     * @return a collection of damage tokens, all having the same color defined as a PlayerColor Enum type
     */
    public List<PlayerColor> getBlood(int i){
        ArrayList<PlayerColor> blood = new ArrayList<>();
        blood.addAll(skullsTrack.get(i));
        return blood;
    }

    /**
     * Board's map getter
     * @return the map of the board
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * This method assigns the points of the skull track at the end of the game
     */
    @SuppressWarnings("Duplicates")
    public void assignBlood() {
        List<Integer> points = new ArrayList<>(this.pointsFromSkulls);
        Map<PlayerColor, Integer> bloodFrom = new EnumMap<>(PlayerColor.class);
        for(PlayerColor color : PlayerColor.values()) bloodFrom.put(color, 0);
        for(List<PlayerColor> damage : skullsTrack.values())
            if(damage != null)
                bloodFrom.put(damage.get(0), damage.size());
        while(!bloodFrom.isEmpty()) {
            List<PlayerColor> bests = new ArrayList<>();
            int max = -1;
            for(Map.Entry<PlayerColor, Integer> entry : bloodFrom.entrySet())
                if(entry.getValue() > max)
                    max = entry.getValue();
            for(Map.Entry<PlayerColor, Integer> entry : bloodFrom.entrySet())
                if(entry.getValue() == max)
                    bests.add(entry.getKey());
            PlayerColor bestColor;
            List<PlayerColor> damage = new ArrayList<>();
            for(Map.Entry<Integer, List<PlayerColor>> entry : skullsTrack.entrySet())
                if(entry.getValue() != null)
                    damage.add(entry.getValue().get(0));
            if(bests.size() == 1) bestColor = bests.get(0);
            else {
                int firstOccurrence = damage.size() + 1;
                for(PlayerColor color : bests)
                    if(damage.indexOf(color) < firstOccurrence)
                        firstOccurrence = damage.indexOf(color);
                bestColor = damage.get(firstOccurrence);
            }
            Player bestPlayer = null;
            for(Player player : this.map.getPlayersPosition().keySet())
                if(player.getCharacter().getColor().equals(bestColor))
                    bestPlayer = player;
            if(bestPlayer != null) bestPlayer.addPoints(points.get(0));
            if(points.size() > 1) points.remove(0);
            bloodFrom.remove(bestColor);
        }
    }

    /**
     * Saves the situation of the board in a string
     * @param player it's the player that is playing right now
     * @return the String with the game situation
     */
    public String toStringFromPlayer(Player player) {
        StringBuilder board = new StringBuilder();
        board.append(this.getMap().toString());

        board.append(NEW_LINE).append(SKULLS_REMAINING).append(skullsLeft()).append(NEW_LINE);
        board.append(NEW_LINE).append(WEAPON_IN_RESPAWN).append(NEW_LINE);


        for(AmmoColor color : AmmoColor.values()) {
            board.append(INDENT).append(color.toString()).append(": ");
            List<Weapon> weaponList = this.weapons.get(color);
            for(Weapon weapon : weaponList) {
                if(weapon != null) board.append(weapon.getName()).append("; ");
            }
            board.append(NEW_LINE);
        }
        board.append(NEW_LINE);

        List<Weapon> weaponList = player.getPlayerHand().getWeapons();
        board.append(player.getNickname()).append(", ");
        if(weaponList.isEmpty()) {
            board.append(NO_WEAPON_IN_HAND).append(NEW_LINE);
        }
        else {
            board.append(WEAPONS_IN_HAND).append(NEW_LINE);
            for(Weapon weapon : weaponList) {
                board.append(INDENT).append(weaponList.indexOf(weapon)).append(") ").append(weapon.getName()).append(";").append(NEW_LINE);
            }
        }
        board.append(NEW_LINE);

        List<Powerup> powerupList = player.getPlayerHand().getPowerups();
        board.append(player.getNickname()).append(", ");
        if(powerupList.isEmpty()) {
            board.append(NO_POWERUP_IN_HAND).append(NEW_LINE);
        }
        else {
            board.append(POWERUPS_IN_HAND).append(NEW_LINE);
            for(Powerup powerup : powerupList) {
                board.append(INDENT).append(powerupList.indexOf(powerup)).append(") ").append(powerup.toString()).append(";\n");
            }
        }
        board.append(NEW_LINE);

        board.append(AMMOS_IN_HAND).append(NEW_LINE);
        for(AmmoColor color : AmmoColor.values())
            board.append(INDENT).append(color.toString()).append(": ").append(player.getPlayerHand().getAmmosAmount((color))).append(NEW_LINE);
        board.append(NEW_LINE);

        return board.toString();
    }

}