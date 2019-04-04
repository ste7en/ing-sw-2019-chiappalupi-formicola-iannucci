package it.polimi.ingsw;

import java.util.*;

interface Action {
    void execute(Player p, GameMap map);
}

public class Effect {

    private final String name;
    private final HashMap<AmmoColor, Integer> cost;
    private final EffectType type;
    private ArrayList<Action> actions;

    public Effect(String name, HashMap<AmmoColor, Integer> cost, EffectType type) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        actions = new ArrayList<>();
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

    public void addAction(Action a) {
        actions.add(a);
    }

    public ArrayList<Action> getActions() {
        return (ArrayList<Action>)actions.clone();
    }

}