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
    private String name;

    /**
     * Description of the effect
     */
    private String description;

    /**
     * Cost of the effect
     */
    private HashMap<AmmoColor, Integer> cost;

    /**
     * Type of the effect
     * @see EffectType
     */
    private EffectType type;

    /**
     * ArrayList to save the lambdas of the effect
     */
    private ArrayList<Action> actions;

    /**
     * HashMap to save de properties of the effect, needed to the GameLogic function that returns the applicability of a Weapon
     */
    private HashMap<EffectProperty, Integer> properties;


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
     * Effect's description getter
     * @return the description of the effect
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds an action to the effect
     * @param a lambda to add to the card
     * @throws  NullPointerException if Action a is null
     */
    public void addAction(Action a) {
        //if(a == null) throw new NullPointerException("Action a can't be null");
        if(actions == null) actions = new ArrayList<>();
        actions.add(a);
    }

    /**
     * Effect's actions getter (returns a clone to preserve the rep invariant)
     * @return a clone of the actions
     */
    public ArrayList<Action> getActions() {
        return (ArrayList<Action>)actions.clone();
    }

    /**
     * Effect's properties getter (returns a clone to preserve the rep invariant)
     * @return a clone of the properties
     */
    public HashMap<EffectProperty, Integer> getProperties() {
        return (HashMap<EffectProperty, Integer>) properties.clone();
    }

}