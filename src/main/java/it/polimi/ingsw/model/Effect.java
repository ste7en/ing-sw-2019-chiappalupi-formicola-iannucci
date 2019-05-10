package it.polimi.ingsw.model;

import java.util.HashMap;

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
     * HashMap to save the properties of the effect, needed to the GameLogic function that returns the applicability of a Weapon
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
     * Effect's description getter
     * @return the description of the effect
     */
    public String getDescription() {
        return description;
    }

    /**
     * Effect's properties getter (returns a clone to preserve the rep invariant)
     * @return a clone of the properties
     */
    public HashMap<EffectProperty, Integer> getProperties() {
        return (HashMap<EffectProperty, Integer>) properties.clone();
    }

    /**
     * Effect's properties setter (needed for the creation of artificial effects)
     */
    public void setProperties(HashMap<EffectProperty, Integer> properties) {
        this.properties = properties;
    }

}