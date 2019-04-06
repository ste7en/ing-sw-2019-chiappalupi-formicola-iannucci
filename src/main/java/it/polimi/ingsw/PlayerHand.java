package it.polimi.ingsw;

import java.util.*;

/**
 * This class provides methods and attributes to manage every card the player
 * has during the game, giving an easy way to update, add or remove them.
 *
 * @author Stefano Formicola
 */
public class PlayerHand {

    private static final String INVALID_AMOUNT_EXC = "Invalid amount of ammos to increment or decrement.";
    private static final String MAX_CUBES_ALLOWED = "Player has already the maximum number of cubes allowed.";
    private static final String MAX_POWERUPS_ALLOWED = "Player has already the maximum number of powerups allowed.";
    private static final String NO_COLOR_EXCEPTION = "Invalid color added";
    /**
     * Keeps track of which weapons the player has.
     */
    private List<Weapon> weapons;

    /**
     * Keeps track of how many cubes (called ammos) the player has,
     * without counting powerups' cubes.
     */
    private EnumMap<AmmoColor, Integer> ammos;

    /**
     * Keeps track of which powerups the player has and can use.
     */
    private List<Powerup> powerups;

    /**
     * When a new player instance is created it calls the default constructor
     * of PlayerHand without parameters because all the data structures must be
     * empty.
     */
    public PlayerHand() {
        this.weapons = new ArrayList<>();
        this.powerups = new ArrayList<>();
        this.ammos = new EnumMap<>(AmmoColor.class);
    }

    /**
     * Returns the structure containing player's weapons
     * @return the structure containing player's weapons
     */
    public List<Weapon> getWeapons() {
        return weapons;
    }

    /**
     * Returns the structure containing player's powerups
     * @return the structure containing player's powerups
     */
    public List<Powerup> getPowerups() {
        return powerups;
    }

    /**
     * Returns the amount of cubes of the specified color
     * @param color the color of the cubes
     * @return the amount of cubes of the specified color
     */
    public Integer getAmmosAmount(AmmoColor color) {
        return ammos.getOrDefault(color, 0);
    }


    /**
     * Updates the list of weapons of the player
     * @param weapons the list of weapons to update
     * @throws NullPointerException if a null pointer is passed as parameter
     */
    public void setWeapons(List<Weapon> weapons) {
        if (weapons != null) {
            this.weapons = weapons;
        } else {
            throw new NullPointerException("Weapons list can't be null");
        }
    }

    /**
     * Adds a powerup to the player's deck of powerups
     * @param powerup the powerup to add to the player's list
     * @throws NullPointerException if a null pointer is passed as parameter
     * @throws RuntimeException if there's already the maximum number of powerups allowed
     */
    public void addPowerup(Powerup powerup) {
        if (powerup == null) {
            throw new NullPointerException("Powerup object references to null");
        } else if (powerups.size() >= 3) {
            throw new RuntimeException(MAX_POWERUPS_ALLOWED);
        } else powerups.add(powerup);
    }

    /**
     * This method updates the number of cubes a player has
     * @param ammoColor the color of the cubes
     * @param amount the amount of cubes to increment or decrement
     * @throws IllegalArgumentException if the total number of ammos is less than zero or
     *          the ammoColor parameter is none
     * @throws RuntimeException if there's already the maximum number of cubes (ammos) allowed
     */
    public void updateAmmos(AmmoColor ammoColor, Integer amount) {
        if (ammoColor == AmmoColor.none) { throw new IllegalArgumentException(NO_COLOR_EXCEPTION); }

        if (getAmmosAmount(ammoColor) == 3) throw new RuntimeException(MAX_CUBES_ALLOWED);
        else if (getAmmosAmount(ammoColor) + amount < 0 || getAmmosAmount(ammoColor) + amount > 3)
            throw new IllegalArgumentException(INVALID_AMOUNT_EXC);
        else ammos.put(ammoColor, getAmmosAmount(ammoColor) + amount);
    }

}