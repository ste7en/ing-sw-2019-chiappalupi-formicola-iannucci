package it.polimi.ingsw.model.board;

import java.util.*;

import it.polimi.ingsw.controller.*;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.PlayerColor;

/**
 *
 * @author Elena Iannucci
 */

public class Board {

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
     * @param skulls it's the number of skulls that the game will have.
     * */
    public Board(GameMap map, Map<AmmoColor, List<Weapon>> weapons, int skulls){
        this.weapons = new EnumMap<>(AmmoColor.class);
        this.skullsTrack = new LinkedHashMap<>();
        this.map = map;
        this.weapons.putAll(weapons);
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
                    index=weapons.get(ammoColor).indexOf(weapon);
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

}