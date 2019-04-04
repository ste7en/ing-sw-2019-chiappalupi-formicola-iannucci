package it.polimi.ingsw;

import java.util.*;

public class DecksHandler {

    private ArrayList<Weapon> weapons;
    private ArrayList<AmmoTile> ammoTiles;
    private ArrayList<Powerup> powerups;
    private ArrayList<AmmoTile> ammoRecycleBin;
    private ArrayList<Powerup> powerupsRecycleBin;

    public DecksHandler(ArrayList<Weapon> weapons, ArrayList<AmmoTile> ammoTiles, ArrayList<Powerup> powerups) {
        this.weapons = weapons;
        this.ammoTiles = ammoTiles;
        this.powerups = powerups;
        ammoRecycleBin = new ArrayList<>();
        powerupsRecycleBin = new ArrayList<>();
    }

    public Weapon drawWeapon() {
        Collections.shuffle(weapons);
        return weapons.remove(0);
    }

    public AmmoTile drawAmmoTile() {
        Collections.shuffle(ammoTiles);
        return ammoTiles.remove(0);
    }

    public Powerup drawPowerups() {
        Collections.shuffle(powerups);
        return powerups.remove(0);
    }

    public void wasteAmmoTile(AmmoTile item) {
        if(item != null) ammoRecycleBin.add(item);
    }

    public void wastePowerup(Powerup item) {
        if(item != null) powerupsRecycleBin.add(item);
    }

    private void recycleAmmos() {
        Collections.shuffle(ammoRecycleBin);
        ammoTiles = (ArrayList<AmmoTile>) ammoRecycleBin.clone();
        ammoRecycleBin.clear();
    }

    private void recyclePowerups() {
        Collections.shuffle(powerupsRecycleBin);
        powerups = (ArrayList<Powerup>) powerupsRecycleBin.clone();
        powerupsRecycleBin.clear();
    }

}