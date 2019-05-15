package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import it.polimi.ingsw.controller.*;

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
     * Weapons placed on the board near the spawpoints divided by colors
     */
    private HashMap<AmmoColor, ArrayList<Weapon>> weapons;

    /**
     * Killshot track on the board that takes into account the deaths occured during the game
     */
    private LinkedHashMap<Integer, ArrayList<PlayerColor>> skullsTrack;

    public Board(GameMap map, HashMap<AmmoColor, ArrayList<Weapon>> weapons, LinkedHashMap<Integer, ArrayList<PlayerColor>> skullsTrack){
        this.weapons = new HashMap<>();
        this.skullsTrack = new LinkedHashMap<>();
        this.map=map;
        this.weapons.putAll(weapons);
        this.skullsTrack.putAll(skullsTrack);
    }

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

    public void refillWeapons(DecksHandler decksHandler) {
        for(AmmoColor ammoColor : weapons.keySet()){
            for (int i=0; i<3; i++){
                if (weapons.get(ammoColor).get(i) == null) {
                    weapons.get(ammoColor).set(i, decksHandler.drawWeapon());
                }
            }
        }
    }

    public ArrayList<Weapon> showWeapons(AmmoColor color) {
        ArrayList<Weapon> decksweapons = new ArrayList<>();
        decksweapons.addAll(weapons.get(color));
        return decksweapons;
    }

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

    public void addBloodFrom(PlayerColor player, Integer count) {
        int i=0;
        ArrayList<PlayerColor> playerColors = new ArrayList<>();
        while (this.skullsTrack.get(i)!=null) i++;
        for (int j=0; j<count; j++){
            playerColors.add(player);
        }
        skullsTrack.put(i, playerColors);
    }

    public ArrayList<PlayerColor> getBlood(int i){
        ArrayList<PlayerColor> blood = new ArrayList<>();
        blood.addAll(skullsTrack.get(i));
        return blood;
    }

    public GameMap getMap() {
        return map;
    }

}