package it.polimi.ingsw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.utility.AdrenalineLogger;

import javax.swing.plaf.TableHeaderUI;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Daniele Chiappalupi
 */
public class DecksHandler implements Serializable {

    /**
     * String passed as message of RuntimeException when it is asked to draw from an empty deck.
     */
    private static final String WEAPONS_OVER_EXC = "Tried to draw from a finished deck.";

    /**
     * String that contains the path to where the resources are located.
     */
    private static final URL SIMPLE_WEAPONS_PATH = Thread.currentThread().getContextClassLoader().getResource("simpleWeapons.json");
    private static final URL POTENTIABLE_WEAPONS_PATH = Thread.currentThread().getContextClassLoader().getResource("potentiableWeapons.json");
    private static final URL SELECTABLE_WEAPONS_PATH = Thread.currentThread().getContextClassLoader().getResource("selectableWeapons.json");
    private static final URL AMMO_TILES_PATH = Thread.currentThread().getContextClassLoader().getResource("ammoTiles.json");
    private static final URL NEWTON_POWERUP_PATH = Thread.currentThread().getContextClassLoader().getResource("newtonPowerups.json");
    private static final URL TAGBACK_POWERUP_PATH = Thread.currentThread().getContextClassLoader().getResource("tagbackGrenadePowerups.json");
    private static final URL TARGETINGSCOPE_POWERUP_PATH = Thread.currentThread().getContextClassLoader().getResource("targetingScopePowerups.json");
    private static final URL TELEPORTER_POWERUP_PATH = Thread.currentThread().getContextClassLoader().getResource("teleporterPowerups.json");

    /**
     * Integer to initialize the weaponsDeck
     */
    private static final Integer NUM_OF_SIMPLE_WEAPONS = 2;

    /**
     * Integer to initialize the weaponsDeck
     */
    private static final Integer NUM_OF_POTENTIABLE_WEAPONS = 8;

    /**
     * Integer to initialize the weaponsDeck
     */
    private static final Integer NUM_OF_SELECTABLE_WEAPONS = 11;

    /**
     * Integer to initialize the ammoTilesDeck
     */
    private static final Integer NUM_OF_AMMOTILES = 36;

    /**
     * Integer to initialize the powerupsDeck
     */
    private static final Integer NUM_OF_POWERUPS = 6;

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
     * Constructor: creates a new DecksHandler from the jsons of weapons, ammo tiles and powerups
     */
    public DecksHandler() {
        initializeWeapons();
        initializeAmmoTiles();
        initializePowerups();
        ammoRecycleBin = new ArrayList<>();
        powerupsRecycleBin = new ArrayList<>();
    }

    /**
     * Method that initializes the weapons deck using a json file
     * @return an ArrayList containing the deck of weapons for testing purposes
     */
    List<Weapon> initializeWeapons() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleWeapon[] boxSimple = new SimpleWeapon[NUM_OF_SIMPLE_WEAPONS];
        PotentiableWeapon[] boxPotentiable = new PotentiableWeapon[NUM_OF_POTENTIABLE_WEAPONS];
        SelectableWeapon[] boxSelectable = new SelectableWeapon[NUM_OF_SELECTABLE_WEAPONS];
        try {
            boxSimple = objectMapper.readValue(SIMPLE_WEAPONS_PATH, SimpleWeapon[].class);
            boxPotentiable = objectMapper.readValue(POTENTIABLE_WEAPONS_PATH, PotentiableWeapon[].class);
            boxSelectable = objectMapper.readValue(SELECTABLE_WEAPONS_PATH, SelectableWeapon[].class);
        } catch (IOException e) {
            AdrenalineLogger.error(e.toString());
        }
        ArrayList<SimpleWeapon> simpleWeapons = new ArrayList<>(Arrays.asList(boxSimple));
        ArrayList<PotentiableWeapon> potentiableWeapons = new ArrayList<>(Arrays.asList(boxPotentiable));
        ArrayList<SelectableWeapon> selectableWeapons = new ArrayList<>(Arrays.asList(boxSelectable));
        weapons = new ArrayList<>();
        weapons.addAll(simpleWeapons);
        weapons.addAll(selectableWeapons);
        weapons.addAll(potentiableWeapons);
        for(Weapon w : weapons) w.reload();
        Collections.shuffle(weapons);
        return new ArrayList<>(weapons);
    }

    /**
     * Method that initializes the ammo tiles deck using a json file
     * @return an ArrayList containing the deck of ammo tiles for testing purposes
     */
    List<AmmoTile> initializeAmmoTiles() {
        ObjectMapper objectMapper = new ObjectMapper();
        AmmoTile[] box = new AmmoTile[NUM_OF_AMMOTILES];
        try {
            box = objectMapper.readValue(AMMO_TILES_PATH, AmmoTile[].class);

        } catch (IOException e) {
            AdrenalineLogger.error(e.toString());
        }
        ammoTiles = new ArrayList<>(Arrays.asList(box));
        return new ArrayList<>(ammoTiles);
    }

    /**
     * Method that initializes the powerups deck using a json file
     * @return an ArrayList containing the deck of powerups for testing purposes
     */
    List<Powerup> initializePowerups() {
        ObjectMapper objectMapper = new ObjectMapper();
        Newton[] boxNewton = new Newton[NUM_OF_POWERUPS];
        TargetingScope[] boxTargetingScope = new TargetingScope[NUM_OF_POWERUPS];
        TagbackGrenade[] boxTagbackGrenade = new TagbackGrenade[NUM_OF_POWERUPS];
        Teleporter[] boxTeleporter = new Teleporter[NUM_OF_POWERUPS];
        try {
            boxNewton = objectMapper.readValue(NEWTON_POWERUP_PATH, Newton[].class);
            boxTagbackGrenade = objectMapper.readValue(TAGBACK_POWERUP_PATH, TagbackGrenade[].class);
            boxTargetingScope = objectMapper.readValue(TARGETINGSCOPE_POWERUP_PATH, TargetingScope[].class);
            boxTeleporter = objectMapper.readValue(TELEPORTER_POWERUP_PATH, Teleporter[].class);
        } catch (IOException e) {
            AdrenalineLogger.error(e.toString());
        }
        ArrayList<Newton> newtons = new ArrayList<>(Arrays.asList(boxNewton));
        ArrayList<TagbackGrenade> tagbackGrenades = new ArrayList<>(Arrays.asList(boxTagbackGrenade));
        ArrayList<TargetingScope> targetingScopes = new ArrayList<>(Arrays.asList(boxTargetingScope));
        ArrayList<Teleporter> teleporters = new ArrayList<>(Arrays.asList(boxTeleporter));
        powerups = new ArrayList<>();
        powerups.addAll(newtons);
        powerups.addAll(tagbackGrenades);
        powerups.addAll(targetingScopes);
        powerups.addAll(teleporters);
        return new ArrayList<>(powerups);
    }

    /**
     * Method that lets other classes verify if the weapons are over (in that case, no weapon can be drawn)
     * @return true if there are no weapons in the weapons deck
     */
    boolean weaponsOver() {
        return weapons.isEmpty();
    }

    /**
     * Method that draws a weapon from the weapons deck. For a better fairness, it shuffles the deck before every drawn
     * @return the weapon drawn
     * @throws RuntimeException if the deck is empty
     */
    public Weapon drawWeapon() {
        if(weaponsOver()) throw new IllegalStateException(WEAPONS_OVER_EXC);
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
     * @throws NullPointerException if ammoTile is null
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
     * @throws NullPointerException if powerup is null
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
        ammoTiles = new ArrayList<>(ammoRecycleBin);
        ammoRecycleBin.clear();
    }

    /**
     * Private method that refills the powerups deck with all of the used powerups, and clears the recycle bin after that
     */
    private void recyclePowerups() {
        Collections.shuffle(powerupsRecycleBin);
        powerups = new ArrayList<>(powerupsRecycleBin);
        powerupsRecycleBin.clear();
    }

}