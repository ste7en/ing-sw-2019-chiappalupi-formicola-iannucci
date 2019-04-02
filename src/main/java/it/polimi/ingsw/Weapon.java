package it.polimi.ingsw;

import java.util.*;

public class Weapon {

    private String name;
    private ArrayList<AmmoColor> cost;
    private String notes;
    private Boolean loaded;
    private WeaponType type;
    private ArrayList<Effect> effects;

    public String getName() {
        return name;
    }

    public ArrayList<AmmoColor> getCost() {
        return cost;
    }

    public String getNotes() {
        return notes;
    }

    public Boolean isLoaded() {
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