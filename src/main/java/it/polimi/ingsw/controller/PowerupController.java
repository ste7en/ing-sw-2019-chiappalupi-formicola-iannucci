package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.player.Player;

import java.io.Serializable;
import java.util.*;

/**
 * Powerup controller class.
 * It handles every request and model manipulation about powerups.
 *
 * @author Daniele Chiappalupi
 */
public class PowerupController implements Serializable {

    private static final String POWERUP_NOT_IN_HAND = "This powerup is not in the hand of this player!";
    /**
     * It's the set of targets that can be shot through a powerup that can be used after a weapon.
     * Set because no duplicates must be in it.
     */
    private Set<Player> targets;
    private Player tagback;

    /**
     * Class constructor: initializes the attributes.
     */
    PowerupController() {
        this.targets = new HashSet<>();
    }

    /**
     * This method returns a map containing all of the powerups that can be used right after have shot someone.
     */
    public List<String> getAfterShotPowerups(Player player) {
        List<String> returnPows = new ArrayList<>();
        for(Powerup powerup : player.getPlayerHand().getPowerups())
            if(powerup.isUsableAfterDamageMade())
                returnPows.add(powerup.toString());
        return returnPows;
    }

    /**
     * This method returns a map containing all of the powerups in the hand of a player.
     * @param player it's the player who we are looking the powerups of.
     * @return a map <indexOf_Powerup, Powerup::toString>
     */
    public Map<String, String> getPowerupInHand(Player player) {
        Map<String, String> returnValues = new HashMap<>();
        List<Powerup> powerups = player.getPlayerHand().getPowerups();
        for(Powerup powerup : powerups)
            returnValues.put(Integer.toString(powerups.indexOf(powerup)), powerup.toString());
        return returnValues;
    }

    /**
     * This method is used to add a powerup to the used deck.
     * @param powerup it'a the powerup::toString of the powerup.
     * @param player it's the player who is wasting the powerup.
     * @param decks it's the decks controller.
     */
    public void wastePowerup(String powerup, Player player, DecksHandler decks) {
        List<Powerup> powerups = player.getPlayerHand().getPowerups();
        Powerup toWaste = null;
        for(Powerup p : powerups)
            if(p.toString().equals(powerup))
                toWaste = p;
        player.getPlayerHand().wastePowerup(toWaste);
        decks.wastePowerup(toWaste);
    }

    /**
     * This method is used to get the possible damages that a powerup can do.
     * @param powerup it's the powerup::toString of the powerup.
     * @param player it's the player who is using the powerup.
     * @param map it's the GameMap.
     * @param players it's a list containing the players in game.
     * @return the list of possible damages that the powerup can make.
     */
    public List<Damage> getPowerupDamages(String powerup, Player player, GameMap map, List<Player> players) {
        if(player == null) player = this.tagback;
        List<Powerup> powerups = player.getPlayerHand().getPowerups();
        Powerup selectedPowerup = null;
        for(Powerup p : powerups)
            if(p.toString().equalsIgnoreCase(powerup))
                selectedPowerup = p;
        if(selectedPowerup == null) throw new NullPointerException(POWERUP_NOT_IN_HAND);
        if(selectedPowerup.isUsableAfterDamageMade()) return selectedPowerup.use(player, map, new ArrayList<>(targets));
        return selectedPowerup.use(player, map, players);
    }

    /**
     * This method is used to return the powerups that the player has in his hand and can use anytime during his turn.
     * @param player it's the player whose turn is.
     * @return the list of the names of the powerups that the player can use and its color [i.e. Teleporter - Blue].
     */
    public List<String> getUsablePowerups(Player player) {
        List<Powerup> powerupList = player.getPlayerHand().getPowerups();
        List<String> powerupNames = new ArrayList<>();
        for(Powerup powerup : powerupList)
            if(powerup.isUsableDuringTurn())
                powerupNames.add(powerup.toString());
        return powerupNames;
    }

    /**
     * This method adds a target to the targets of the powerup, if it isn't already in them.
     * @param target it's the target ot add.
     */
    public void addPowerupTarget(Player target) {
        this.targets.add(target);
    }

    /**
     * Clears the targets powerup set to make it ready for the next usage.
     */
    public void clearPowerupTargets() {
        this.targets.clear();
    }

    public List<String> getPowerupAfterGetDamage(Player target) {
        List<String> pows = new ArrayList<>();
        for(Powerup pow : target.getPlayerHand().getPowerups())
            if(pow.isUsableAfterDamageTaken())
                pows.add(pow.toString());
        return pows;
    }

    public void setTagback(Player tagback) {
        this.tagback = tagback;
    }
}
