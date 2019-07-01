package it.polimi.ingsw.model.board;

import java.util.*;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.PlayerColor;

/**
 *
 * @author Elena Iannucci
 */

public class Board {

    /**
     * String constants used in Board::toString
     */
    private static final String WEAPON_IN_RESPAWN = "Here are the weapons in the spawn points:";
    private static final String WEAPONS_IN_HAND = "Here are your weapons:";
    private static final String NO_WEAPON_IN_HAND = "You haven't got any weapon in your hand.";
    private static final String POWERUPS_IN_HAND = "Here are your powerups: ";
    private static final String NO_POWERUP_IN_HAND = "You haven't got any powerup in your hand.";
    private static final String AMMOS_IN_HAND = "Here are your ammos:";

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
     * Constructor: creates a new board based on its map, its killshot track and weapons positioned near its spawnpoint
     * @param map the map of the board
     * @param weapons a collection of three boards placed next to the spawpoints defined by a color and that contain three weapons each
     * */
    public Board(GameMap map, Map<AmmoColor, List<Weapon>> weapons){
        this.weapons = new EnumMap<>(AmmoColor.class);
        this.skullsTrack = new LinkedHashMap<>();
        this.map = map;
        this.weapons.putAll(weapons);
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
     * Method that shows the three weapons placed next to a certain spawnpoint
     * @param color the color of the spawnpoint next to the weapons that have to be shawn, defined as a AmmoColor Enum type
     * @return
     */
    public ArrayList<Weapon> showWeapons(AmmoColor color) {
        ArrayList<Weapon> decksweapons = new ArrayList<>();
        decksweapons.addAll(weapons.get(color));
        return decksweapons;
    }

    /**
     * Method that shows the skulls left that represent the deaths that have to occur for the game to end
     * @return the number of skulls left
     */
    public int skullsLeft() {
        int lastSkullConquered=-1;
        int totalSkulls=0;
        for(Integer skullsNumber : skullsTrack.keySet()){
            if (skullsTrack.get(skullsNumber)==null && lastSkullConquered==-1) {
                lastSkullConquered=skullsNumber;
            }
            totalSkulls++;
        }
        if (lastSkullConquered == -1) return 0;
        else return (totalSkulls-lastSkullConquered);
    }

    /**
     * Method that adds a certain number (either 1 or 2) of damage tokens of a certain color to the killshot track
     * @param player the color of the player that caused the death
     * @param count the number of damage tokens to add (either 1 or 2)
     */
    public void addBloodFrom(PlayerColor player, Integer count) {
        int i=0;
        ArrayList<PlayerColor> playerColors = new ArrayList<>();
        while (this.skullsTrack.get(i)!=null) i++;
        for (int j=0; j<count; j++){
            playerColors.add(player);
        }
        skullsTrack.put(i, playerColors);
    }

    /**
     * Method that shows the number of damage tokens and their color for a given death round
     * @param i an index that represents the death rounds starting from zero
     * @return a collection of damage tokens, all having the same color defined as a PlayerColor Enum type
     */
    public ArrayList<PlayerColor> getBlood(int i){
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

    public String toStringFromPlayer(Player player) {
        StringBuilder board = new StringBuilder();
        board.append(this.getMap().toString());
        board.append(WEAPON_IN_RESPAWN).append("\n");


        for(AmmoColor color : AmmoColor.values()) {
            board.append(color.toString()).append(": ");
            List<Weapon> weaponList = this.weapons.get(color);
            for(Weapon weapon : weaponList) {
                board.append(weapon.getName()).append("; ");
            }
            board.append("\n");
        }
        board.append("\n");

        List<Weapon> weaponList = player.getPlayerHand().getWeapons();
        board.append(player.getNickname()).append(", ");
        if(weaponList.isEmpty()) {
            board.append(NO_WEAPON_IN_HAND).append("\n");
        }
        else {
            board.append(WEAPONS_IN_HAND);
            for(Weapon weapon : weaponList) {
                board.append(weapon.getName()).append("; ");
            }
            board.append("\n");
        }
        board.append("\n");

        List<Powerup> powerupList = player.getPlayerHand().getPowerups();
        board.append(player.getNickname()).append(", ");
        if(powerupList.isEmpty()) {
            board.append(NO_POWERUP_IN_HAND).append("\n");
        }
        else {
            board.append(POWERUPS_IN_HAND);
            for(Powerup powerup : powerupList) {
                board.append(powerup.toString()).append("; ");
            }
            board.append("\n");
        }
        board.append("\n");

        board.append(AMMOS_IN_HAND).append("\n");
        for(AmmoColor color : AmmoColor.values())
            board.append(color.toString()).append(": ").append(player.getPlayerHand().getAmmosAmount((color))).append("\n");
        board.append("\n");

        return board.toString();
    }

}