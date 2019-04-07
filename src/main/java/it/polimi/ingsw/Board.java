package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {

    private GameMap map;
    private HashMap<AmmoColor, ArrayList<Weapon>> weapons;
    private HashMap<Integer, ArrayList<PlayerColor>> skullTrack;

    public Board(GameMap map, HashMap<AmmoColor, ArrayList<Weapon>> weapons, HashMap<Integer, ArrayList<PlayerColor>> skullTrack){
        this.weapons = new HashMap<>();
        this.skullTrack = new HashMap<>();
        this.map=map;
        this.weapons.putAll(weapons);
        this.skullTrack.putAll(skullTrack);
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

    public void pickWeapon(Weapon chosenWeapon) {
        int index;
        for (AmmoColor ammoColor : weapons.keySet()){
            for (Weapon weapon : weapons.get(ammoColor)){
                if (weapon.equals(chosenWeapon)) {
                    index=weapons.get(ammoColor).indexOf(weapon);
                    weapons.get(ammoColor).set(index, null);
                }
            }
        }
    }

    public Integer skullsLeft() {
        int lastSkullConquered=-1;
        int totalSkulls=0;
        for(Integer skullsNumber : skullTrack.keySet()){
            if (skullTrack.get(skullsNumber).size()==0 && lastSkullConquered==-1) {
                lastSkullConquered=skullsNumber;
            }
            totalSkulls++;
        }
        return (totalSkulls-lastSkullConquered);
    }


    public void addBloodFrom(PlayerColor player, Integer count) {
        int i=0;
        ArrayList<PlayerColor> playerColors = new ArrayList<>();
        do {
            i++;
        } while (skullTrack.get(i).size()!=0);
        for (int j=0; j<count; j++){
            playerColors.add(player);
        }
        skullTrack.replace(i, playerColors );
    }

}