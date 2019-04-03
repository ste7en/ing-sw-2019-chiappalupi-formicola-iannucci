package it.polimi.ingsw;

import java.util.*;

public class Effect {

    private String name;
    private HashMap<AmmoColor, Integer> cost;
    private EffectType type;

    public Effect(String name, HashMap<AmmoColor, Integer> cost, EffectType type) {
        this.name = name;
        this.cost = cost;
        this.type = type;
    }

    public String getName() {
        return null;
    }

    public HashMap<AmmoColor, Integer> getCost() {
        return cost;
    }

    public EffectType getType() {
        return type;
    }

}