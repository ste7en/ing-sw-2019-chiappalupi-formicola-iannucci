package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.utility.AmmoColor;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Daniele Chiappalupi
 */
public class Effect implements Serializable {

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
    private Map<EffectProperty, Integer> properties;

    /**
     * String constant used in messages between client-server
     */
    public static final String effect_key = "EFFECT";


    public String getName() {
        return name;
    }

    /**
     * Effect's cost getter
     * @return the cost of the effect
     */
    public Map<AmmoColor, Integer> getCost() {
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
    public Map<EffectProperty, Integer> getProperties() {
        return new EnumMap<>(properties);
    }

    /**
     * Effect's properties setter (needed for the creation of artificial effects)
     */
    void setProperties(Map<EffectProperty, Integer> properties) {
        this.properties = properties;
    }

}