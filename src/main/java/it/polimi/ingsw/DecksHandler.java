package it.polimi.ingsw;


import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Daniele Chiappalupi
 */
public class DecksHandler {

    /**
     * String passed as message of RuntimeException when it is asked to draw from an empty deck.
     */
    private static final String WEAPONS_OVER_EXC = "Tried to draw from a finished deck.";

    /**
     * Deck of weapons
     */
    private ArrayList<Weapon> weapons;

    /**
     * Deck of ammo tiles
     */
    private ArrayList<AmmoTile> ammoTiles;

    /**
     * Deck of powerups
     */
    private ArrayList<Powerup> powerups;

    /**
     * List of used ammo tiles: every time that the ammo tiles deck finishes, it will be refilled with the tiles inside this list
     */
    private ArrayList<AmmoTile> ammoRecycleBin;

    /**
     * List of discarded powerups: every time the powerups deck finishes, it will be refilled with the powerups inside this list
     */
    private ArrayList<Powerup> powerupsRecycleBin;

    /**
     * Constructor: creates a new DecksHandler from the list of weapons, ammo tiles and powerups
     * @param weapons starting deck of weapons
     * @param ammoTiles starting deck of ammo tiles
     * @param powerups starting deck of powerups
     */
    public DecksHandler(ArrayList<Weapon> weapons, ArrayList<AmmoTile> ammoTiles, ArrayList<Powerup> powerups) {
        this.weapons = weapons;
        this.ammoTiles = ammoTiles;
        this.powerups = powerups;
        ammoRecycleBin = new ArrayList<>();
        powerupsRecycleBin = new ArrayList<>();
    }

    /**
     * Method that lets other classes verify if the weapons are over (in that case, no weapon can be drawn)
     * @return true if there are no weapons in the weapons deck
     */
    public boolean weaponsOver() {
        return weapons.size() == 0;
    }

    /**
     * Method that draws a weapon from the weapons deck. For a better fairness, it shuffles the deck before every drawn
     * @return the weapon drawn
     * @throws RuntimeException if the deck is empty
     */
    public Weapon drawWeapon() {
        if(weaponsOver()) throw new RuntimeException(WEAPONS_OVER_EXC);
        Collections.shuffle(weapons);
        return weapons.remove(0);
    }

    /**
     * Method that draws an ammo tile from the ammo tiles deck. For a better fairness, it shuffles the deck before every drawn
     * if the deck finishes after the drawn, it gets refilled from the cards in the recycle bin
     * @return the ammo tile drawn
     */
    public AmmoTile drawAmmoTile() {
        Collections.shuffle(ammoTiles);
        if(ammoTiles.size() == 1) {
            AmmoTile a = ammoTiles.remove(0);
            recycleAmmos();
            return a;
        }
        return ammoTiles.remove(0);
    }

    /**
     * Method that draws a powerup from the powerups deck. For a better fairness, it shuffles the deck before every drawn
     * if the deck finishes after the drawn, it gets refilled from the cards in the recycle bin
     * @return the powerup drawn
     */
    public Powerup drawPowerup() {
        Collections.shuffle(powerups);
        if(powerups.size() == 1) {
            Powerup p = powerups.remove(0);
            recyclePowerups();
            return p;
        }
        return powerups.remove(0);
    }

    /**
     * Method that adds the used ammo tiles to the ammo tiles recycle bin
     * @param ammoTile it's the ammo tile to add to the bin
     */
    public void wasteAmmoTile(AmmoTile ammoTile) {
        if(ammoTile != null) {
            ammoRecycleBin.add(ammoTile);
        }
        else throw new NullPointerException("Ammo tile can't be null");
    }

    /**
     * Method that adds the used or wasted powerups to the powerups recycle bin
     * @param powerup it's the powerup to add to the bin
     */
    public void wastePowerup(Powerup powerup) {
        if(powerup != null) {
            powerupsRecycleBin.add(powerup);
        }
        else throw new NullPointerException("Powerup can't be null");
    }

    /**
     * Private method that refills the ammo tiles deck with all of the used ammo tiles, and clears the recycle bin after that
     */
    private void recycleAmmos() {
        Collections.shuffle(ammoRecycleBin);
        ammoTiles = (ArrayList<AmmoTile>) ammoRecycleBin.clone();
        ammoRecycleBin.clear();
    }

    /**
     * Private method that refills the powerups deck with all of the used powerups, and clears the recycle bin after that
     */
    private void recyclePowerups() {
        Collections.shuffle(powerupsRecycleBin);
        powerups = (ArrayList<Powerup>) powerupsRecycleBin.clone();
        powerupsRecycleBin.clear();
    }

}