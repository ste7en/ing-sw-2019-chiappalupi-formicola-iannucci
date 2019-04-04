package it.polimi.ingsw;

import java.util.*;

public class Weapon {

    private final String name;
    private final ArrayList<AmmoColor> cost;
    private final String notes;
    private boolean loaded;
    private final WeaponType type;
    private final ArrayList<Effect> effects;

    public Weapon(String name, ArrayList<AmmoColor> cost, String notes, WeaponType type, ArrayList<Effect> effects) {
        this.name = name;
        this.cost = cost;
        this.notes = notes;
        this.loaded = true;
        this.type = type;
        this.effects = effects;
    }

    public WeaponType getType() { return type; }

    public String getName() {
        return name;
    }

    public ArrayList<AmmoColor> getCost() {
        return cost;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void unload() {
        loaded = false;
    }

    public void reload() {
        loaded = true;
    }

    public ArrayList<Effect> getEffect() {
        return effects;
    }

}