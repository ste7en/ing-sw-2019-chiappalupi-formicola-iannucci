package it.polimi.ingsw;

import java.util.*;

/**
 *
 * @author Daniele Chiappalupi
 */
public class Weapon {

    /**
     * Name of the weapon
     */
    private final String name;

    /**
     *  Cost of the weapon
     */
    private final ArrayList<AmmoColor> cost;

    /**
     * Notes of the weapon; if the weapon doesn't have them, this attribute is null;
     */
    private final String notes;

    /**
     * Boolean that saves the state of the weapon: if it's true, the weapon can shoot; otherwise, it can't
     */
    private boolean loaded;

    /**
     * Type of the weapon
     */
    private final WeaponType type;

    /**
     * List of effects of the weapon
     */
    private final ArrayList<Effect> effects;

    /**
     * Constructor: creates a new Weapon from its name, cost, notes, type and effects
     * @param name name of the weapon
     * @param cost cost of the weapon, defined as an ArrayList of AmmoColors
     * @param notes notes of the weapon
     * @param type type of the weapon, defined as an ArrayList of types
     * @param effects effects of the weapon, defined as an ArrayList of effects
     */
    public Weapon(String name, ArrayList<AmmoColor> cost, String notes, WeaponType type, ArrayList<Effect> effects) {
        this.name = name;
        this.cost = cost;
        this.notes = notes;
        this.loaded = true;
        this.type = type;
        this.effects = effects;
    }

    /**
     * Weapon's type getter
     * @return the type of the weapon
     */
    public WeaponType getType() { return type; }

    /**
     * Weapon's name getter
     * @return the name of the weapon
     */
    public String getName() {
        return name;
    }

    /**
     * Weapon's cost getter
     * @return the cost of the weapon
     */
    public ArrayList<AmmoColor> getCost() {
        return cost;
    }

    /**
     * Weapon's notes getter
     * @return the notes of the weapon (null if the weapon hasn't got them: {@link Weapon#notes})
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Weapon's state viewer
     * @return a boolean that shows if the weapon can shoot {@link Weapon#loaded}
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Unloads the weapon, setting loaded to false
     */
    public void unload() {
        loaded = false;
    }

    /**
     * Loads the weapon, setting loaded to true
     */
    public void reload() {
        loaded = true;
    }

    /**
     * Weapon's effects getter
     * @return the ArrayList of Effects of the weapon
     */
    public ArrayList<Effect> getEffect() {
        return effects;
    }

}