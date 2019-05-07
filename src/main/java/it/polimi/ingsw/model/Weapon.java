package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 *
 * @author Daniele Chiappalupi
 */
public class Weapon {

    /**
     * Name of the weapon
     */
    private String name;

    /**
     *  Cost of the weapon
     */
    private ArrayList<AmmoColor> cost;

    /**
     * Notes of the weapon; if the weapon doesn't have them, this attribute is null;
     */
    private String notes;

    /**
     * Boolean that saves the state of the weapon: if it's true, the weapon can shoot; otherwise, it can't
     */
    private boolean loaded;

    /**
     * Type of the weapon
     */
    private WeaponType type;

    /**
     * List of effects of the weapon
     */
    private ArrayList<Effect> effects;

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
        return (ArrayList<AmmoColor>)cost.clone();
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
    public ArrayList<Effect> getEffects() {
        return (ArrayList<Effect>)effects.clone();
    }

    /**
     * Implemented for testing and checking purposes
     * @return the weapon in a string form
     */
    @Override
    public String toString() {
        return "Name: " + name + ";\nCost: " + cost.toString() + ";\nType: " + type.toString() +  ";\nNotes: " + notes;
    }

}