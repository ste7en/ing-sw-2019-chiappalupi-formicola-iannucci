package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface used to implement the effects
 *
 * @author Daniele Chiappalupi
 */
interface Action {

    /**
     * Lamda
     * @param p player that is executing the action or to apply the effect on
     * @param map necessary to see the other players position
     */
    void execute(Player p, GameMap map);
}

/**
 *
 * @author Daniele Chiappalupi
 */
public class Effect {

    /**
     * Name of the effect
     */
    private final String name;

    /**
     * Cost of the effect
     */
    private final HashMap<AmmoColor, Integer> cost;

    /**
     * Type of the effect
     * @see EffectType
     */
    private final EffectType type;

    /**
     * ArrayList to save the lambdas of the effect
     */
    private ArrayList<Action> actions;

    /**
     * Constructor of the class: creates a new Effect, from its name, cost and type
     * @param name name of the effect
     * @param cost cost of the effect
     * @param type type of the effect
     */
    public Effect(String name, HashMap<AmmoColor, Integer> cost, EffectType type) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        actions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    /**
     * Effect's cost getter
     * @return the cost of the effect
     */
    public HashMap<AmmoColor, Integer> getCost() {
        return cost;
    }

    /**
     * Effect's type getter
     * @return the type of the effect
     */
    public EffectType getType() {
        return type;
    }

    /**
     * Adds an action to the effect
     * @param a lambda to add to the card
     * @throws  NullPointerException if Action a is null
     */
    public void addAction(Action a) {
        if(a == null) throw new NullPointerException("Action a can't be null");
        actions.add(a);
    }

    /**
     * Effect's actions getter (returns a clone to preserve the rep invariant)
     * @return a clone of the actions
     */
    public ArrayList<Action> getActions() {
        return (ArrayList<Action>)actions.clone();
    }

}