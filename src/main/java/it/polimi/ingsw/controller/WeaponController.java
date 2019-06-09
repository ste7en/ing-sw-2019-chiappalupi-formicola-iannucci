package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.PlayerColor;

import java.util.*;

/**
 * Class controller of the weapons.
 *
 * @author Daniele Chiappalupi
 */
public class WeaponController {

    private List<Damage> forPotentiableWeapon;
    private List<Powerup> powerupUsedToPayCost;
    private Map<AmmoColor, Integer> effectsCost;

    /**
     * Class constructor.
     */
    WeaponController() {
        this.forPotentiableWeapon = new ArrayList<>();
        this.effectsCost = new EnumMap<> (AmmoColor.class);
    }

    /**
     * Effects cost setter.
     * @param costs it's a map containing the costs to add.
     */
    public void addEffectsCost(Map<AmmoColor, Integer> costs) {
        effectsCost.putAll(costs);
    }

    /**
     * This method is used to check if a player can afford the cost of a mode, an effect or a combination of effects of a weapon.
     * @param weapon it's the weapon that is being used.
     * @param effects  it's the list of effects indexes to be tested +1.
     * @param player it's the player who is using the card.
     */
    public boolean canAffordCost(Weapon weapon, List<Integer> effects, Player player) {
        Map<AmmoColor, Integer> totalCost = new EnumMap<>(AmmoColor.class);
        Map<AmmoColor, Integer> playerAmmos = new EnumMap<>(AmmoColor.class);
        Map<AmmoColor, Integer> powerupAmmos = new EnumMap<>(AmmoColor.class);
        for(AmmoColor color : AmmoColor.values()) {
            totalCost.put(color, 0);
            powerupAmmos.put(color, 0);
            playerAmmos.put(color, player.getPlayerHand().getAmmosAmount(color));
        }
        for(Powerup powerup : this.powerupUsedToPayCost) {
            AmmoColor color = powerup.getColor();
            powerupAmmos.put(color, powerupAmmos.get(color) + 1);
        }
        for(Integer index : effects) {
            int trueIndex = index-1;
            Map<AmmoColor, Integer> effectCost = weapon.getEffects().get(trueIndex).getCost();
            for(AmmoColor color : effectCost.keySet()) {
                int box = totalCost.get(color);
                box += effectCost.get(color);
                totalCost.put(color, box);
            }
        }
        for(AmmoColor color : AmmoColor.values()) {
            while(powerupAmmos.get(color) > 0 && totalCost.get(color) > 0) {
                powerupAmmos.put(color, powerupAmmos.get(color) - 1);
                totalCost.put(color, totalCost.get(color) - 1);
            }
        }
        for(AmmoColor color : AmmoColor.values())
            if(totalCost.get(color) > player.getPlayerHand().getAmmosAmount(color) || powerupAmmos.get(color) > 0)
                return false;
        return true;
    }

    /**
     * This method is used to find out if a player can afford the cost of reloading the weapons he wants to reload.
     * If the player can afford the cost, the weapons are reloaded.
     * @param weapons it's the list of weapon that the player wants to reload.
     * @param player it's the player that is asking to reload his weapons.
     * @return TRUE if the reload has been successful, FALSE otherwise.
     */
    public boolean checkCostOfReload(List<Weapon> weapons, Player player) {
        Map<AmmoColor, Integer> cost = new EnumMap<>(AmmoColor.class);
        for(AmmoColor color : AmmoColor.values())
            cost.put(color, 0);
        for(Weapon weapon : weapons)
            for(AmmoColor color : weapon.getCost()) {
                int costBox = cost.get(color);
                costBox++;
                cost.put(color, costBox);
            }
        for(AmmoColor color : AmmoColor.values()) {
            int newAmmo = player.getPlayerHand().getAmmosAmount(color) - cost.get(color);
            if (newAmmo < 0)
                return false;
            else cost.put(color, newAmmo);
        }
        for(AmmoColor color : AmmoColor.values())
            player.getPlayerHand().updateAmmos(color, cost.get(color));
        for(Weapon weapon : weapons)
            weapon.reload();
        return true;
    }

    /**
     * This method is used to find all of the names of the unloaded weapons in the hand of a player.
     * @param player it's the player to return the weapons of.
     */
    public List<String> lookForUnloadedPlayerWeapons(Player player) {
        List<String> playerWeapons = new ArrayList<>();
        for(Weapon weapon : player.getPlayerHand().getWeapons())
            if(!weapon.isLoaded())
                playerWeapons.add(weapon.getName());
        return playerWeapons;
    }

    /**
     * This method is used to find all of the names of the weapons in the hand of a player.
     * @param player it's the player to return the weapons of.
     */
    public List<String> lookForPlayerWeapons(Player player) {
        List<String> playerWeapons = new ArrayList<>();
        for(Weapon weapon : player.getPlayerHand().getWeapons())
            playerWeapons.add(weapon.getName());
        return playerWeapons;
    }

    /**
     * This method is used to find a weapon from its name and the player who is using it.
     * @param weapon it's the name of the weapon that is being looked for.
     * @param player it's the player that has the weapon
     */
    public Weapon lookForWeapon(String weapon, Player player) {
        ArrayList<Weapon> playerWeapons = player.getPlayerHand().getWeapons();
        for(Weapon w : playerWeapons)
            if(w.getName().equals(weapon)) return w;
        throw new IllegalArgumentException("This user doesn't own this weapon!");
    }

    /**
     * This method is used to apply damages when a player shoots to another.
     * @param damage it's the damage that has to be applied.
     * @param shooter it's the color of the player who is shooting.
     * @param board it's the board of the game.
     */
    public void applyDamage(Damage damage, PlayerColor shooter, Board board) {
        Player target = damage.getTarget();
        if(damage.getPosition() != null) board.getMap().setPlayerPosition(target, damage.getPosition());
        if(damage.getDamage() != 0) target.getPlayerBoard().appendDamage(shooter, damage.getDamage());
        if(damage.getMarks() != 0) {
            ArrayList<PlayerColor> marks = new ArrayList<>();
            for(int i = 0; i < damage.getMarks(); i++)
                marks.add(shooter);
            target.getPlayerBoard().setMarks(marks);
        }
    }

    /**
     * This method is used to compute the possible damages that a weapon can do when used by a certain player.
     * @param weapon it's the weapon that is being used.
     * @param effect it's the effect that is being used.
     * @param shooter it's the player who is using the weapon.
     * @param forPotentiableWeapons it's an ArrayList containing all of the previous damages made by the other weapons.
     * @param board it's the board of the game.
     * @param players it's the list of players in the game.
     * @return an array list containing all of the possible damage alternatives when using the effect of the weapon.
     */
    public ArrayList<ArrayList<Damage>> useEffect(Weapon weapon, Effect effect, Player shooter, List<Damage> forPotentiableWeapons, Board board, List<Player> players) {
        return weapon.useEffect(shooter, effect, forPotentiableWeapons, board.getMap(), players);
    }

    /**
     * Clears the damage that has been done with a potentiable weapon after that it has been used.
     */
    public void wipePotentiableWeapon() {
        if(forPotentiableWeapon == null) {
            forPotentiableWeapon = new ArrayList<>();
            return;
        }
        if(!forPotentiableWeapon.isEmpty()) forPotentiableWeapon.clear();
    }

    /**
     * Adds damages to the list of damages that is being done using a potentiable weapon.
     *
     * @param damage it's the damage that is being done.
     */
    public void appendPotentiableWeapon(List<Damage> damage) {
        if(forPotentiableWeapon == null)
            forPotentiableWeapon = new ArrayList<>(damage);
        else forPotentiableWeapon.addAll(damage);
    }

    /**
     * Getter of the list of damage that has already been done with the weapon that is being used.
     *
     * @return an array list containing the damage.
     */
    public List<Damage> getForPotentiableWeapon() {
        return forPotentiableWeapon;
    }

    /**
     * Subs the cost of the weapon to the player's ammos.
     *
     * @param player it's the player who has used the weapon.
     * @param decks it's the deck of the game, needed to waste the powerup.
     */
    public void applyCost(Player player, DecksHandler decks) {
        for(AmmoColor color : effectsCost.keySet()) {
            int newAmount = player.getPlayerHand().getAmmosAmount(color) - effectsCost.get(color);
            player.getPlayerHand().updateAmmos(color, newAmount);
        }
        effectsCost.clear();
        for(Powerup powerup : powerupUsedToPayCost) {
            player.getPlayerHand().wastePowerup(powerup);
            decks.wastePowerup(powerup);
        }
    }

    /**
     * Adds the cost of the powerups sold by a player to his memory
     * @param powerups it's the list of powerups sold by the player.
     * @param player it's the player that is selling his powerups.
     */
    public void addPowerupSold(List<String> powerups, Player player) {
        for(Powerup powerup : player.getPlayerHand().getPowerups())
            for(String pow : powerups)
                if(powerup.toString().equalsIgnoreCase(pow))
                    this.powerupUsedToPayCost.add(powerup);
    }

}
